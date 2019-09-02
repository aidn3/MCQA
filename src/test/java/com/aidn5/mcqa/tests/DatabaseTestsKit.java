
package com.aidn5.mcqa.tests;

import com.aidn5.mcqa.core.DatabaseFactory;
import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.core.database.split.ByteMcqaAdapter;
import com.aidn5.mcqa.core.database.split.JsonMcqaAdapter;
import com.aidn5.mcqa.core.database.split.MemoryMcqaAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.junit.Assert;

public class DatabaseTestsKit {
  public static Collection<String> databaseTypes = Arrays.asList(new String[] {
      DatabaseFactory.JSON, DatabaseFactory.BYTE, DatabaseFactory.MEMORY
      /*
       * , DatabaseFactory.HYPER, DatabaseFactory.MEMORY, DatabaseFactory.SQLITE
       */
  });

  private DatabaseTestsKit() {
    throw new AssertionError();
  }

  public static IMcqaDatabase getStorageInterface(String dabaseType, boolean clean)
      throws Exception {

    if (dabaseType.equals(DatabaseFactory.JSON)) {
      final File dbPath = new File(TestParams.TestPath, "mcqa_database/");

      if (clean && dbPath.exists()) {
        for (File file : dbPath.listFiles()) {
          file.delete();
        }
      }

      return new JsonMcqaAdapter(dbPath);

    } else if (dabaseType.equals(DatabaseFactory.BYTE)) {
      final File dbPath = new File(TestParams.TestPath, "mcqa_database/");

      if (clean && dbPath.exists()) {
        for (File file : dbPath.listFiles()) {
          file.delete();
        }
      }

      return new ByteMcqaAdapter(dbPath);

    } else if (dabaseType.equals(DatabaseFactory.MEMORY)) {

      return new MemoryMcqaAdapter();
    }

    throw new RuntimeException("dabaseType not exists: " + dabaseType);
  }

  public static Content createDummyContent1() {
    Content content = new Content();

    content.category = "hello";
    content.addedAt = System.currentTimeMillis() / 1000L;

    content.questions = new String[] { "do planes fly?" };
    content.questionCreator = UUID.randomUUID();
    content.questionProvedBy = UUID.randomUUID();

    content.answerProvedContent = new String[] { "GOOD answer" };

    content.answerUnProvedContent = new String[] { "BAD answer" };
    content.creatorOfUnprovedAnswer = UUID.randomUUID();

    return content;
  }

  public static Content createDummyContent2() {
    Content content = createDummyContent1();
    content.category = "categoryyyy";
    content.questions = new String[] { "will flies walk?" };
    content.answerProvedContent = new String[] { "BEST answer EveR" };
    content.answerUnProvedContent = new String[] { "MySteriEs AnsSwer!" };

    return content;
  }

  public static Content createDummyContent3() {
    Content content = createDummyContent2();
    content.category = "whatcategory";
    content.questions = new String[] { "can Elephant sleep?", "Is This another question$" };
    content.answerProvedContent = new String[] { "SomeWire asdswer EveR:>2D" };
    content.answerUnProvedContent = new String[] { "Valid AnsWERASD@E@WE" };

    return content;
  }

  // used to test how good can database handle these contents
  public static Content createWorstContent() {
    Content content = new Content();
    content.addedAt = -24542546;
    content.answerProvedContent = new String[] { String.valueOf('\uffff'), " ", "234c2$#C #$#$R",
        "...", "", "SDF", "this is text with spaces", "space at end    " };
    content.answerUnProvedContent = new String[] { "abcdefghijklmnopqrstuvwxyz",
        "!@#$%^&*()_-0987654321`~|\\/.,<>]}[{" };
    content.category = "WERF#R #R# RFE#R@#$R";

    content.creatorOfUnprovedAnswer = null;
    content.questionCreator = UUID.randomUUID();
    content.questions = new String[] { "this is good", " W #R #WR", "yes hey wht?", "!" };
    content.questionProvedBy = UUID.randomUUID();
    content.contentId = -1;

    return content;
  }

  public static void assertContents(Content orginal, Content fromDB) {
    orginal.contentId = fromDB.contentId; // dummy contents don't have a real id from a database
    Assert.assertTrue(orginal.equals(fromDB));
  }
}
