
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IContentsIterator;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.core.database.StorageException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Iterator for the database. Used to iterate through all the selected
 * contents while holding any other requests from interfering.
 * 
 * @author aidn5
 */
public class SplitContentsIterator implements IContentsIterator {
  @Nonnull
  private final IMcqaDatabase instance;
  @Nonnull
  private final Iterator<Long> contentsIds;

  @Nullable
  private Content nextContent = null;

  /**
   * Create an iterator for the database. Used to iterate through all the selected
   * contents while holding any other requests from interfering.
   * 
   * @param instance
   *          an instance from the parent database used to get the content by
   *          {@link IMcqaDatabase#getContent(long)}.
   * @param contents
   *          the contents id to iterate through and get.
   * @param onlyApprovedContents
   *          whether the contents should be approved by the staff to be viewed.
   *          If <code>true</code> the contents without the approved tag will be
   *          ignored.
   * 
   * @throws NullPointerException
   *           if {@code instance} or {@code contentsIds} is <code>null</code>
   */
  public SplitContentsIterator(@Nonnull IMcqaDatabase instance,
      @Nonnull Iterator<Long> contentsIds) throws NullPointerException {

    this.instance = Objects.requireNonNull(instance);
    this.contentsIds = Objects.requireNonNull(contentsIds);
  }

  @Nonnull
  @Override
  public Iterator<Content> iterator() {
    return this;
  }

  @Override
  public boolean hasNext() {
    if (nextContent != null) {
      return true;
    }

    fetchNextContent();
    return nextContent != null;
  }

  @Nonnull
  @Override
  public Content next() throws NoSuchElementException {
    if (!hasNext()) {
      throw new NoSuchElementException("No more contents found");
    }

    Content content = nextContent;
    nextContent = null;
    return content;
  }

  @Override
  public void close() {
    // nothing to close
  }

  private void fetchNextContent() throws StorageException {

    while (contentsIds.hasNext()) {
      long id = contentsIds.next();

      if (id == 0) {
        continue;
      }

      final Content content = instance.getContent(id);

      if (content != null) {
        content.contentId = id;
        nextContent = content;
        return;
      }
    }
  }

  @Nonnull
  @Override
  public List<Content> getAll() throws StorageException {
    List<Content> list = new ArrayList<>();

    for (Content content : this) {
      list.add(content);
    }

    return list;
  }
}
