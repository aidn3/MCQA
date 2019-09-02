
package com.aidn5.mcqa.tests.database;

import com.aidn5.mcqa.core.content.Content;
import com.aidn5.mcqa.core.database.IMcqaDatabase;
import com.aidn5.mcqa.tests.DatabaseTestsKit;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QuickDatabaseTest {
  @Parameters(name = "{index}: {0}")
  public static Collection<String> data() {
    return DatabaseTestsKit.databaseTypes;
  }

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
  public void test_inserts_then_compare_with_database_version() {
    Content c = DatabaseTestsKit.createDummyContent1();
    storageInterface.addContent(c);
    storageInterface.addContent(DatabaseTestsKit.createDummyContent2());
    storageInterface.addContent(DatabaseTestsKit.createDummyContent3());

    List<Content> contents = storageInterface
        .searchForContents(false, 0, null).getAll();

    Assert.assertEquals(3, contents.size());

    Content content2 = contents.get(0);
    DatabaseTestsKit.assertContents(c, content2);
  }

  @Test
  public void test_insert_contents_that_can_corrupt_database() {
    Content content = DatabaseTestsKit.createWorstContent();

    storageInterface.addContent(content);

    List<Content> contents = storageInterface
        .searchForContents(false, 0, null).getAll();

    Assert.assertEquals(1, contents.size());

    Content content2 = contents.get(0);
    DatabaseTestsKit.assertContents(content, content2);
  }

  @Test
  public void test_not_allowed_insert_same_content_properties() {
    Content content = DatabaseTestsKit.createDummyContent1();

    storageInterface.addContent(content);

    try {
      // since this content has no id
      // It will not count as an overwrite, but as a new content.
      // the question is already exists, so it must throw an error
      storageInterface.addContent(content);
      Assert.fail();
    } catch (@SuppressWarnings("unused") Exception ignored) {}
  }

  @Test
  public void test_overwrite_selfcontent() {
    final String oldC = "somethingOld";
    final String newC = "somethingNew";
    // write new content
    Content content = DatabaseTestsKit.createDummyContent1();
    content.category = oldC;
    storageInterface.addContent(content);

    // check if it is written
    List<Content> contents = storageInterface.searchForContents(false, 0, null).getAll();
    Assert.assertEquals(1, contents.size());

    // get the written content from the database, change it and re-add it
    content = contents.get(0);
    content.category = newC;
    storageInterface.addContent(content);

    // get the content again and check the changes
    contents = storageInterface.searchForContents(false, 0, null).getAll();
    Assert.assertEquals(1, contents.size());
    content = contents.get(0);
    Assert.assertEquals(newC, content.category);
  }


  @Test
  public void test_remove_Content() {
    // add contents
    storageInterface.addContent(DatabaseTestsKit.createDummyContent1());
    storageInterface.addContent(DatabaseTestsKit.createDummyContent2());
    storageInterface.addContent(DatabaseTestsKit.createDummyContent3());
    storageInterface.addContent(DatabaseTestsKit.createWorstContent());

    // check whether they are added
    List<Content> contentsB = storageInterface.searchForContents(false, 0, null).getAll();
    Assert.assertEquals(4, contentsB.size());

    // remove every one of them
    for (Content content2 : contentsB) {
      storageInterface.removeContent(content2.contentId);
    }

    // check now whether they are removed
    contentsB = storageInterface.searchForContents(false, 0, null)
        .getAll();
    Assert.assertEquals(0, contentsB.size());
  }

  @Test
  public void remove_try_remove_not_existed_Content() {
    try {
      Assert.assertFalse("#removeContent() with non-existed id must never return true",
          storageInterface.removeContent(9999999));

    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("#removeContent() must never throw Exception if no content found");
    }
  }


  @Test
  public void test_get_specfic_content() {
    Content content = DatabaseTestsKit.createDummyContent1();

    storageInterface.addContent(content);
    storageInterface.addContent(DatabaseTestsKit.createDummyContent2());
    storageInterface.addContent(DatabaseTestsKit.createDummyContent3());

    List<Content> list = storageInterface.searchForContents(false, 0,
        content.questions[0].split(" ")).getAll();

    Assert.assertTrue(list.size() > 0);

    DatabaseTestsKit.assertContents(content, list.get(0));
  }

  @Test
  public void test_get_only_approved_contents() {
    Content content1 = DatabaseTestsKit.createDummyContent1();
    content1.answerProvedContent = null;
    Assert.assertEquals(false, content1.hasProvedAnswer());
    storageInterface.addContent(content1);

    Content content2 = DatabaseTestsKit.createDummyContent2();
    content2.answerProvedContent = new String[] { "cool Content" };
    Assert.assertEquals(true, content2.hasProvedAnswer());
    storageInterface.addContent(content2);

    Content content3 = DatabaseTestsKit.createDummyContent3();
    content3.answerProvedContent = new String[] { "ThisISCONTENT", "YES, Multiple strings" };
    Assert.assertEquals(true, content3.hasProvedAnswer());
    storageInterface.addContent(content3);


    List<Content> allContents = storageInterface.searchForContents(false, 0, null).getAll();
    List<Content> onlyProvedContents = storageInterface.searchForContents(true, 0, null).getAll();

    Assert.assertNotEquals(allContents.size(), onlyProvedContents.size());
    Assert.assertEquals(3, allContents.size());
    Assert.assertEquals(2, onlyProvedContents.size());
  }

  @Test
  public void test_get_after_offset() {
    Content content = DatabaseTestsKit.createDummyContent1();

    storageInterface.addContent(content);
    storageInterface.addContent(DatabaseTestsKit.createDummyContent2());
    storageInterface.addContent(DatabaseTestsKit.createDummyContent3());

    List<Content> contents = storageInterface.searchForContents(false, 0, null).getAll();
    Assert.assertEquals(3, contents.size());

    contents = storageInterface.searchForContents(false, 2, null).getAll();
    Assert.assertEquals(1, contents.size());

    contents = storageInterface.searchForContents(false, Integer.MAX_VALUE, null).getAll();
    Assert.assertEquals(0, contents.size());

    contents = storageInterface.searchForContents(false, -2, null).getAll();
    Assert.assertEquals(3, contents.size());

    contents = storageInterface
        .searchForContents(false, -2, content.questions[0].split(" ")).getAll();
    Assert.assertEquals(1, contents.size());

    contents = storageInterface
        .searchForContents(false, 3, content.questions[0].split(" ")).getAll();
    Assert.assertEquals(0, contents.size());

    contents = storageInterface
        .searchForContents(false, Integer.MAX_VALUE, content.questions[0].split(" ")).getAll();
    Assert.assertEquals(0, contents.size());
  }
}
