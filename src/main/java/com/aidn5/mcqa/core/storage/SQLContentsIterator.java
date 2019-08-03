package com.aidn5.mcqa.core.storage;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.storage.exceptions.StorageException;


public class SQLContentsIterator implements Iterable<Content>, Iterator<Content> {
	private final ResultSet resultSet;

	@SuppressWarnings("unused")
	private boolean next;

	public SQLContentsIterator(ResultSet rs) {
		this.resultSet = Objects.requireNonNull(rs);
	}

	public List<Content> getAsList() {
		List<Content> data = new ArrayList<>();

		for (Content content : this) {
			data.add(content);
		}

		return data;
	}

	@Override
	public boolean hasNext() {
		try {
			return (next = resultSet.next());
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public Content next() throws StorageException {
		try {
			Content content = new Content();

			content.setContentId(resultSet.getLong("contentId"));
			content.setCategory(resultSet.getString("category"));
			content.setAddedAt(resultSet.getLong("addedAt"));

			content.setQuestionProved(resultSet.getBoolean("isQuestionProved"));
			content.setQuestionCreator(resultSet.getString("questionCreator"));
			content.setQuestionContent(resultSet.getString("questionContent"));

			content.setAnswerProvedContent(resultSet.getString("answerProvedContent"));
			content.setAnswerUnProvedContent(resultSet.getString("answerUnProvedContent"));
			content.setCreatorOfUnprovedContent(resultSet.getString("creatorOfUnprovedContent"));

			return content;
		} catch (Exception e) {
			throw new StorageException(null, e);
		}
	}

	@Override
	public Iterator<Content> iterator() {
		return this;
	}
}
