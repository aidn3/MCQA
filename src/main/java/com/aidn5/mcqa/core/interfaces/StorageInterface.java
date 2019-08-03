package com.aidn5.mcqa.core.interfaces;

import java.util.List;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.storage.SQLiteConnection;
import com.aidn5.mcqa.core.storage.exceptions.StorageException;

/**
 * An interface connects the plugin with the database to save and retrieve
 * {@link Content}.
 * <p>
 * Database-Adapter is changeable (e.g. SQLITE, MYSQLI, plain JSON, in memory,
 * etc.)
 * 
 * @author aidn5
 * @see SQLiteConnection
 * @see Content
 */
public interface StorageInterface {
	/**
	 * open the connection to the database and initiate it
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public void openConnection() throws StorageException;

	/**
	 * add new content to the database
	 * <p>
	 * use {@link Content#createNewContent(String, boolean, String, String, String)}
	 * to add
	 * 
	 * @param content
	 *            the content to add
	 * 
	 * @return the id of the new added content
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public long addContent(Content content) throws StorageException;

	/**
	 * remove the content from the database
	 * 
	 * @param contentId
	 *            the associated id with the targeted content
	 * 
	 * @return TRUE if the content found and removed. FALSE if nothing found to
	 *         remove
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public boolean removeContent(long contentId) throws StorageException;

	/**
	 * search for similar Contents
	 * 
	 * @param onlyApprovedContents
	 *            show only approved contents by the authorities
	 * 
	 * @param words
	 *            words to use to query and search for contents
	 * 
	 * @return List contains the results
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public List<Content> searchForContents(boolean onlyApprovedContents, String[] words) throws StorageException;

	/**
	 * get all the saved contents from the database
	 * <p>
	 * <b>Warning: </b><i>Too many contents will eat up the memory!<i>
	 * 
	 * @param onlyApprovedContents
	 *            show only approved contents by the authorities
	 * 
	 * @return List contains all the saved contents in the database
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public List<Content> getAllContents(boolean onlyApprovedContents) throws StorageException;

	/**
	 * get Content from the database
	 * 
	 * @param id
	 *            the id which the content associated to
	 * 
	 * @return the associated content with the id. or NULL if not found
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public Content getContent(long id) throws StorageException;

	/**
	 * close the connection to the database
	 * 
	 * @throws StorageException
	 *             if any error occurs
	 */
	public void closeConnection() throws StorageException;
}
