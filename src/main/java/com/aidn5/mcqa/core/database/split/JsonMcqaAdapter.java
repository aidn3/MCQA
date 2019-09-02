
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;

/**
 * Adapter saves the data in separate files for every individual content in
 * json format. This adapter has the best question search query, but also one of
 * the slowest adapter. see {@link SplitMcqaAdapter}. Note: The generated data
 * from this adapter can be edited with text editor. See {@link ByteMcqaAdapter}
 * for an adapter that have double the speed performance.
 *
 * @author aidn5
 * 
 * @see SplitMcqaAdapter
 * @see ByteMcqaAdapter
 * @see MemoryMcqaAdapter
 */
public class JsonMcqaAdapter extends SplitMcqaAdapter {
  private static final Gson GSON = new Gson();

  /**
   * Constructor for the adapter.
   * 
   * @param dirContents
   *          the dictionary to utilize for the database
   * 
   * @throws IOException
   *           if any I/O error occurs while trying to utilize the dictionary
   * @throws NullPointerException
   *           if {@code dirContents} is <code>null</code>
   */
  public JsonMcqaAdapter(@Nonnull File dirContents) throws IOException, NullPointerException {
    super(dirContents);
  }

  @Override
  protected Content toContent(byte[] contentBytes) {
    return GSON.fromJson(new String(contentBytes, StandardCharsets.UTF_8), Content.class);
  }

  @Override
  protected byte[] fromContent(Content content) {
    return GSON.toJson(content).getBytes();
  }
}
