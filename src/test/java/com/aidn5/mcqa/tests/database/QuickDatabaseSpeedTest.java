
package com.aidn5.mcqa.tests.database;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IContentsIterator;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.tests.DatabaseTestsKit;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QuickDatabaseSpeedTest {
  @Parameters(name = "{index}: {0}")
  public static Collection<String> data() {
    return DatabaseTestsKit.databaseTypes;
  }

  private static final Content content = getContent();

  @Parameter
  public String currentDabaseTypeTest;

  private IMcqaDatabase storageInterface;


  @Before
  public void initDatabase() throws Exception {
    storageInterface = DatabaseTestsKit.getStorageInterface(currentDabaseTypeTest, true);
  }

  @After
  public void closeDatabase() throws Exception {
    storageInterface.closeConnection();
  }

  @Test
  public void test_speed_1000() {
    System.gc();
    System.out.println(currentDabaseTypeTest + " contents 1K:");

    do_test_speed(1000, content, storageInterface);
  }

  @Test
  public void test_speed_10000() {
    System.gc();
    System.out.println(currentDabaseTypeTest + " contents 10K:");

    do_test_speed(10000, content, storageInterface);
  }

  @Test
  public void test_speed_100000() {
    // fail("this test is disabled, due to its extensive memory and storage
    // usage.");
    System.gc();
    System.out.println(currentDabaseTypeTest + " contents 100K:");

    do_test_speed(100000, content, storageInterface);
  }

  private static void do_test_speed(long count, Content content, IMcqaDatabase storageInterface) {
    System.out.println("current memory: " + usedMemory());
    long time = System.nanoTime();

    // write test
    for (int i = 0; i < count; i++) {
      content.questions = new String[] { "test" + i, "testBest" + i };
      storageInterface.addContent(content);
    }
    System.gc();
    System.out.println(
        "write in milli: " + ((System.nanoTime() - time) / 1000000) + ", memory: " + usedMemory());
    time = System.nanoTime();

    // search test
    IContentsIterator iterator = storageInterface
        .searchForContents(false, 0, new String[] { "test", "cool" });
    System.gc();
    System.out.println(
        "search in milli: " + ((System.nanoTime() - time) / 1000000) + ", memory: " + usedMemory());
    time = System.nanoTime();

    // read test
    while (iterator.hasNext()) {
      iterator.next();
    }
    try {
      iterator.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.gc();
    System.out.println(
        "read in milli: " + ((System.nanoTime() - time) / 1000000) + ", memory: " + usedMemory());
    time = System.nanoTime();

    iterator = storageInterface.searchForContents(false, 0, null);
    System.gc();
    System.out.println(
        "getAll in milli: " + ((System.nanoTime() - time) / 1000000) + ", memory: " + usedMemory());
    time = System.nanoTime();

    List<Content> contents = iterator.getAll();
    time = System.nanoTime();
    for (Content content2 : contents) {
      storageInterface.removeContent(content2.contentId);
    }
    contents = null;

    System.gc();
    System.out.println(
        "remove in milli: " + ((System.nanoTime() - time) / 1000000) + ", memory: " + usedMemory());
    time = System.nanoTime();
  }


  private static Content getContent() {
    Content content = DatabaseTestsKit.createDummyContent1();
    StringBuilder sb = new StringBuilder(10);
    for (int i = 0; i < 10; i++) {
      sb.append("t");
    }

    content.answerProvedContent = new String[] { sb.toString() };
    return content;
  }

  private static String usedMemory() {
    long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    String name = " Byte";
    if (memory > 1024) {
      name = " KB";
      memory = memory / 1024;
    }
    if (memory > 1024) {
      name = " MB";
      memory = memory / 1024;
    }
    if (memory > 1024) {
      name = " GB";
      memory = memory / 1024;
    }

    return memory + name;
  }
}
