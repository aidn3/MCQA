
package com.aidn5.mcqa.core.database;

import com.aidn5.mcqa.core.content.Content;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sqlite.SQLiteConnection;


/**
 * An interface connects the plugin with the database to save and retrieve
 * {@link Content}.
 * 
 * <p>Database-Adapter is changeable (e.g. SQLITE, MYSQLI, plain JSON, in memory,
 * etc.)
 * 
 * @author aidn5
 * @see SQLiteConnection
 * @see Content
 */
public interface IMcqaDatabase {
  /**
   * Check the connection to the database, Whether is it valid and ready.
   */
  public boolean connectionOpened();

  /**
   * add new content to the database. if {@link Content#contentId} is given the
   * adapter will try to replace an already exists content. If the content does
   * not exists, the id will be ignored. An error will be thrown if one of the
   * question this new {@code content} is already been used by another content.
   * 
   * @param content
   *          the content to add
   * 
   * @return the id of the new added content
   * 
   * @throws StorageException
   *           if any error occurs
   */
  public long addContent(@Nonnull Content content) throws StorageException;

  /**
   * remove the content from the database.
   * 
   * @param contentId
   *          the associated id with the targeted content
   * 
   * @return TRUE if the content found and removed.
   *         FALSE if nothing found to be remove.
   * 
   * @throws StorageException
   *           if any error occurs
   */
  public boolean removeContent(long contentId) throws StorageException;

  /**
   * search for content and its similar. This can also be used to get all contents
   * by providing an empty array for {@code words} or <code>null</code>.
   * 
   * @param onlyApprovedContents
   *          show only approved contents by the authorities
   * @param offset
   *          ignore the first n content(s).
   * @param words
   *          words to use to query and search for contents.
   * 
   * @return
   *         {@link Iterable} contains the requested results.
   * 
   * @throws StorageException
   *           if any error occurs
   */
  @Nonnull
  public IContentsIterator searchForContents(boolean onlyApprovedContents, int offset,
      @Nullable String[] words) throws StorageException;

  /**
   * get Content from the database.
   * 
   * @param contentId
   *          the id which the content associated to
   * 
   * @return
   *         the associated content with the id,
   *         or <code>null</code> if not found.
   * 
   * @throws StorageException
   *           if any error occurs
   */
  @Nullable
  public Content getContent(long contentId) throws StorageException;

  /**
   * close the connection to the database. This method has no effect the
   * connection is already closed.
   * 
   * @throws StorageException
   *           if any error occurs
   */
  public void closeConnection() throws StorageException;
}
