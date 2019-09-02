
package com.aidn5.mcqa.core;

import com.aidn5.mcqa.core.database.split.ByteMcqaAdapter;
import com.aidn5.mcqa.core.database.split.JsonMcqaAdapter;
import com.aidn5.mcqa.core.database.split.MemoryMcqaAdapter;
import com.aidn5.mcqa.core.database.split.SplitMcqaAdapter;
import com.aidn5.mcqa.utils.TutorialContent;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * Factory used to create database interfaces. Every method creates its own type
 * of adapter. all adapters have their own Constants {@link #JSON},
 * {@link #MEMORY}, etc.
 * 
 * @author aidn5
 */
public class DatabaseFactory {
  /**
   * Indicates {@link JsonMcqaAdapter}.
   */
  @Nonnull
  public static final String JSON = "JSON";
  /**
   * Indicates {@link ByteMcqaAdapter}.
   */
  @Nonnull
  public static final String BYTE = "BYTE";
  /**
   * Indicates {@link MemoryMcqaAdapter}.
   */
  @Nonnull
  public static final String MEMORY = "MEMORY";

  // public static final String HYPER = "HYPER";
  // public static final String SQLITE = "SQLITE";
  // public static final String MYSQL = "MYSQL";

  private DatabaseFactory() {
    throw new AssertionError();
  }

  /**
   * Create a database adapter for MCQA. Adapter Type: {@link JsonMcqaAdapter}
   * extended from {@link SplitMcqaAdapter}.
   * 
   * @param dataFolder
   *          the dictionary to utilize for the data to be saved in.
   * @param createTutorial
   *          create and add the tutorial content if the database is fresh new
   * 
   * @return
   *         an initiated adapter ready to be used.
   * 
   * @throws IOException
   *           if any I/O occurs while creating the dir/files for the data.
   * @throws NullPointerException
   *           if {@code dataFolder} is <code>null</code>.
   * 
   * @see JsonMcqaAdapter
   */
  @Nonnull
  public static JsonMcqaAdapter getJson(@Nonnull File dataFolder, boolean createTutorial)
      throws IOException, NullPointerException {

    File dir = new File(dataFolder, "mcqa_database/");
    dir.mkdirs();

    boolean newDB = dir.listFiles().length < 2;
    JsonMcqaAdapter database = new JsonMcqaAdapter(dir);

    if (newDB && createTutorial) {
      database.addContent(TutorialContent.getTutorialContent());
    }

    return database;
  }

  /**
   * Create a database adapter for MCQA. Adapter Type: {@link ByteMcqaAdapter}
   * extended from {@link SplitMcqaAdapter}.
   * 
   * @param dataFolder
   *          the dictionary to utilize for the data to be saved in.
   * @param createTutorial
   *          create and add the tutorial content if the database is fresh new
   * 
   * @return
   *         an initiated adapter ready to be used.
   * 
   * @throws IOException
   *           if any I/O occurs while creating the dir/files for the data.
   * @throws NullPointerException
   *           if {@code dataFolder} is <code>null</code>.
   * 
   * @see ByteMcqaAdapter
   */
  @Nonnull
  public static ByteMcqaAdapter getByte(@Nonnull File dataFolder, boolean createTutorial)
      throws IOException, NullPointerException {

    File dir = new File(dataFolder, "json/");
    dir.mkdirs();

    boolean newDB = dir.listFiles().length < 2;
    ByteMcqaAdapter database = new ByteMcqaAdapter(dir);

    if (newDB && createTutorial) {
      database.addContent(TutorialContent.getTutorialContent());
    }

    return database;
  }

  /**
   * Create a database adapter for MCQA. Adapter Type: In memory
   * {@link MemoryMcqaAdapter} extended from {@link SplitMcqaAdapter}.
   * 
   * @param createTutorial
   *          create and add the tutorial content if the database is fresh new
   * 
   * @return
   *         initiated adapter ready to be used.
   * 
   * @see MemoryMcqaAdapter
   **/
  @Nonnull
  public static MemoryMcqaAdapter getMemory(boolean createTutorial) {

    MemoryMcqaAdapter database = new MemoryMcqaAdapter();

    if (createTutorial) {
      database.addContent(TutorialContent.getTutorialContent());
    }

    return database;
  }
}
