
package com.aidn5.mcqa.core.database;

import com.aidn5.mcqa.core.content.Content;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Interface used as an {@link Iterator} for {@link Content}s used with
 * {@link IMcqaDatabase#searchForContents(boolean, int, String[])}.
 * {@link AutoCloseable} interface is implemented to either remind users to
 * {@link #close()} the {@code Iterator} to free up some resources OR to
 * really close the connection and unlock the database for others. If
 * {@link #close()} is not called, the database can be blocked from future
 * requests. This {@link Iterator} is NOT Threadsafe. If the implementation
 * allows MultiThreading, be sure to call {@link #close()} from the thread that
 * created the object in the first place.
 * 
 * @author aidn5
 */
public interface IContentsIterator extends Iterable<Content>, Iterator<Content>, AutoCloseable {
  /**
   * Iterate over all the data and create a list of it. Not advised to be used
   * with for loops. Use {@code this} the {@link IContentsIterator} object instead
   * of this method for speed performance reasons.
   * 
   * @return
   *         all the contents as array,
   *         or an empty array if no content found.
   * 
   * @throws StorageException
   *           if any error occurs while fetching the data.
   */
  @Nonnull
  public List<Content> getAll() throws StorageException;
}
