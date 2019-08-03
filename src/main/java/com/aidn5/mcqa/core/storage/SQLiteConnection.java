package com.aidn5.mcqa.core.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.core.storage.exceptions.StorageException;
import com.aidn5.mcqa.core.tools.Sanitizer;

/**
 * Default database adapter uses SQLite to save {@link Content}
 * 
 * @author aidn5
 * @version 1.0
 */
public class SQLiteConnection implements StorageInterface {
	private Connection connection = null;

	private static String QUERY_CREATE_TABLE;
	static {
		QUERY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS contents (";
		QUERY_CREATE_TABLE += "contentId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,";
		QUERY_CREATE_TABLE += "category TEXT,";
		QUERY_CREATE_TABLE += "addedAt INTEGER,"; // Unix timestamp
		QUERY_CREATE_TABLE += "isQuestionProved INTEGER,"; // boolean: 0->notProved, 1->isProved
		QUERY_CREATE_TABLE += "questionCreator TEXT,";
		QUERY_CREATE_TABLE += "questionContent TEXT,";
		QUERY_CREATE_TABLE += "answerProvedContent TEXT,";
		QUERY_CREATE_TABLE += "answerUnProvedContent TEXT,";
		QUERY_CREATE_TABLE += "creatorOfUnprovedContent TEXT)";
	}
	private static String QUERY_REMOVE_CONTENTS = "DELETE FROM contents WHERE contentId = ?";

	private static String QUERY_ADD_CONTENTS;
	static {
		QUERY_ADD_CONTENTS = "INSERT INTO contents (";
		QUERY_ADD_CONTENTS += "category, addedAt,";
		QUERY_ADD_CONTENTS += "isQuestionProved, questionCreator, questionContent,";
		QUERY_ADD_CONTENTS += "answerProvedContent, answerUnProvedContent, creatorOfUnprovedContent)";

		QUERY_ADD_CONTENTS += " VALUES (?,?,?,?,?,?,?,?)";
	}

	private final String dbPath;

	public SQLiteConnection() throws StorageException {
		// TODO: SQLiteConnection: add option to load the database into the memory
		// loading the database into the memory and working on it from there is a LOT
		// faster than working on a hard-drive.
		// Steps:
		// - open database in memory
		// - load database into it
		// - work on it
		// - dump it into a file (on-close)
		// - discard the database from the memory

		dbPath = ":memory:";
		openConnection();
		initDatabase();
	}

	public SQLiteConnection(String dbPath) throws StorageException {
		this.dbPath = new File(dbPath).getAbsolutePath();
		openConnection();
		initDatabase();
	}

	/**
	 * <b>example generated query: </b>
	 * <p>
	 * SELECT * FROM contents <br>
	 * WHERE (questionContent LIKE ? OR questionContent LIKE ?) <br>
	 * ORDER BY (questionContent = ?) desc, <br>
	 * cast((questionContent LIKE ?) as int) + cast((questionContent LIKE ?) as int)
	 * desc , <br>
	 * length(questionContent);
	 * <p>
	 * <b>explaining executed query: </b>
	 * <p>
	 * "SELECT * FROM contents <br>
	 * WHERE questionContent LIKE '%{word[0]}%' OR questionContent LIKE
	 * '%{word[1]}%' OR... <br>
	 * ORDER BY (questionContent = '{all_words_with_spaces}') DESC,<br>
	 * cast((questionContent LIKE '%{word[1]}%') as int) + cast((questionContent
	 * LIKE '%{word[1]}%') as int) + ... desc , <br>
	 * length(questionContent);"
	 */
	@Override
	public synchronized List<Content> searchForContents(boolean onlyApprovedContents, String[] words)
			throws StorageException {
		// create the prepared statements dynamically by using words[]

		// SQL query muster:
		// "SELECT * FROM contents WHERE
		// questionContent LIKE '%{word[0]}% OR questionContent LIKE '%{word[1]}% OR ...
		// ORDER BY (questionContent = '{all_words_with_spaces}') DESC,
		// length(questionContent);"
		try {
			if (words == null) {
				throw new IllegalArgumentException(
						"search query must not be null. if you need to get all contents, see StorageInterface#getAllContents");
			} else {
				words = Sanitizer.sanitizeText(words, false);
				if (words.length == 0) {
					throw new IllegalArgumentException("no REAL words to look up for");
				}
			}

			String query = "SELECT * FROM contents WHERE ";
			StringBuilder queryWhere = new StringBuilder(words.length * 25);
			StringBuilder orderByStatement = new StringBuilder(words.length * 30);
			StringBuilder searchSentence = new StringBuilder(words.length * 5);

			// create the query, sanity the search words and recreate the searched sentence
			for (int i = 0; i < words.length; i++) {

				String string = words[i];
				if (string == null) continue;

				// "%" conflicts with SQL query "WHERE column LIKE %value%"
				string = string.replaceAll("[^A-Za-z0-9]", "").trim();
				if (string.isEmpty()) {
					words[i] = null; // set invalid values NULL. to ignore them later
					continue;
				}

				queryWhere.append("questionContent LIKE ?  OR ");
				orderByStatement.append(" cast((questionContent LIKE ?) as int) +");
				searchSentence.append(string).append(" ");


			}

			query += " (" + queryWhere.substring(0, queryWhere.length() - 3) + ") ";
			if (onlyApprovedContents) {
				query += " AND isQuestionProved = 1 ";
			}

			// TODO: SQLIteConnection#searchForContents: "ORDER BY..." make it
			// case-insensitive
			query += " ORDER BY (questionContent = ?) desc, ";
			query += orderByStatement.substring(0, orderByStatement.length() - 1) + "desc ";
			query += ",length(questionContent);";


			// create the query statement and set its values
			PreparedStatement searchStatement = connection.prepareStatement(query);
			int count = 1;

			// set WHERE clause
			for (int i = 0; i < words.length; i++) {
				String string = words[i];
				if (string == null) continue; // ignore the value with NULL, which we set above

				searchStatement.setString(count, "%" + words[i] + "%");

				count++;
			}

			// set "ORDER BY (questionContent = ?) desc"
			searchStatement.setString(count, searchSentence.toString().trim());

			// set the ORDER BY (similar contents)
			for (int i = 0; i < words.length; i++) {
				String string = words[i];
				if (string == null) continue; // ignore the value with NULL, which we set above

				searchStatement.setString(count, "%" + words[i] + "%");

				count++;
			}


			ResultSet rs = searchStatement.executeQuery();

			SQLContentsIterator cr = new SQLContentsIterator(rs);
			List<Content> contents = cr.getAsList();

			rs.close();
			return contents;
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public synchronized List<Content> getAllContents(boolean onlyApprovedContents) throws StorageException {
		try {
			String query = "SELECT * FROM contents";

			if (onlyApprovedContents) {
				query += " WHERE isQuestionProved = 1";
			}

			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);

			List<Content> contents = new SQLContentsIterator(rs).getAsList();

			rs.close();
			return contents;

		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public Content getContent(long id) throws StorageException {
		// TODO: write unit tests to #getContent(long id)
		try {
			String query = "SELECT * FROM contents WHERE contentId = " + id;

			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);

			List<Content> contents = new SQLContentsIterator(rs).getAsList();
			rs.close();

			if (contents.size() == 0) return null;
			return contents.get(0);

		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public synchronized long addContent(Content content) throws StorageException {
		PreparedStatement STATEMENT_ADD_CONTENT = null;
		try {
			STATEMENT_ADD_CONTENT = connection.prepareStatement(QUERY_ADD_CONTENTS);

			STATEMENT_ADD_CONTENT.setString(1, returnSanitizedValue("Category", false, content.getCategory()));

			int addedAt = (int) content.getAddedAt();
			if (addedAt < 0 || (addedAt + "").length() != ((System.currentTimeMillis() / 1000L) + "").length()) {
				throw new IllegalArgumentException("addedAt must be unix timestamp");

			} else if (addedAt == 0) {
				addedAt = (int) (System.currentTimeMillis() / 1000L);
			}
			STATEMENT_ADD_CONTENT.setInt(2, addedAt);

			STATEMENT_ADD_CONTENT.setInt(3, (content.isQuestionProved() ? 1 : 0));
			STATEMENT_ADD_CONTENT.setString(4,
					returnSanitizedValue("QuestionCreator", false, content.getQuestionCreator()));


			STATEMENT_ADD_CONTENT.setString(5,
					returnSanitizedValue("QuestionContent", false, content.getQuestionContent()));


			String answerP = Sanitizer.sanitizeText(content.getAnswerProvedContent(), true);
			String answerUP = Sanitizer.sanitizeText(content.getAnswerUnProvedContent(), true);

			if (answerP.isEmpty() && answerUP.isEmpty()) {
				throw new IllegalArgumentException(
						"either AnswerProvedContent or AnswerUnProvedContent must contains something");
			}

			STATEMENT_ADD_CONTENT.setString(6, answerP);
			STATEMENT_ADD_CONTENT.setString(7, answerUP);

			if (!answerUP.isEmpty()) {
				STATEMENT_ADD_CONTENT.setString(8,
						returnSanitizedValue("CreatorOfUnprovedContent", false, content.getCreatorOfUnprovedContent()));
			}

			STATEMENT_ADD_CONTENT.executeUpdate();
			STATEMENT_ADD_CONTENT.close();

			return 1;
		} catch (Exception e) {
			try {
				STATEMENT_ADD_CONTENT.close();
			} catch (Exception ignored) {}

			throw new StorageException(null, e);
		}
	}

	private String returnSanitizedValue(String valueName, boolean ignoreSpecialChar, String value) {
		if (value == null || value.isEmpty())
			throw new IllegalArgumentException(valueName + " must not be empty or null");

		value = Sanitizer.sanitizeText(value, ignoreSpecialChar);
		if (value.isEmpty()) throw new IllegalArgumentException(valueName + " must only contains alphanumeric char");

		return value;
	}

	@Override
	public synchronized boolean removeContent(long contentId) throws StorageException {
		PreparedStatement STATEMENT_REMOVE_CONTENT = null;
		try {
			STATEMENT_REMOVE_CONTENT = connection.prepareStatement(QUERY_REMOVE_CONTENTS);

			STATEMENT_REMOVE_CONTENT.setInt(1, (int) contentId);
			return STATEMENT_REMOVE_CONTENT.executeUpdate() > 0 ? true : false;

		} catch (Exception e) {
			try {
				STATEMENT_REMOVE_CONTENT.close();
			} catch (Exception ignored) {}

			throw new StorageException(null, e);

		} finally {
			try {
				STATEMENT_REMOVE_CONTENT.close();
			} catch (Exception ignored) {}
		}
	}

	@Override
	public synchronized void openConnection() throws StorageException {
		try {
			closeConnection();
		} catch (Exception ignored) {}

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	private void initDatabase() throws StorageException {
		try {
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(15);
			statement.execute(QUERY_CREATE_TABLE);
			statement.close();
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public synchronized void closeConnection() throws StorageException {
		try {
			if (connection != null) connection.close();
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}
}
