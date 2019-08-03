package com.aidn5.mcqa.tests.database;

import static com.aidn5.mcqa.tests.TestHelper.assertContents;
import static com.aidn5.mcqa.tests.TestHelper.createDummyContent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.tests.TestHelper;

public class InsertTests {
	private StorageInterface storageInterface;

	@Before
	public void initDatabase() {
		storageInterface = TestHelper.getNewStorageInterface();
	}

	@Test
	public void testInsertsGetCompare() {
		// TODO: fix testInsertsGetCompare: Content.equals()
		// problem between NULL and Empty strings

		Content content = createDummyContent();

		storageInterface.addContent(content);
		storageInterface.addContent(content);
		storageInterface.addContent(content);

		String[] question = content.getQuestionContent().split(" ");
		List<Content> contents = storageInterface.searchForContents(content.isQuestionProved(), question);

		Assert.assertEquals(3, contents.size());

		Content content2 = contents.get(0);
		assertContents(content, content2);
	}

	@Test
	public void test_inserts_sanity_with_invalid_contents() {
		try {
			storageInterface.addContent(null);
			Assert.fail();
		} catch (Exception ignored) {}

		Content content = new Content();
		try {
			storageInterface.addContent(content);
			Assert.fail();
		} catch (Exception ignored) {}
	}

	@Test
	public void test_inserts_sanity_with_invalid_question_content() {
		Content content = createDummyContent();
		List<String> questionContent = new ArrayList<>();
		questionContent.add(null);
		questionContent.add("");
		questionContent.add("!#$!#$");
		questionContent.add(" ");
		questionContent.add("123 #@ ");
		questionContent.add(".");
		questionContent.add(0X10FFDF + ""); // unicode char

		for (String string : questionContent) {
			try {
				content.setQuestionContent(string);
				storageInterface.addContent(content);
				Assert.fail(string + " must not be accepted as question's content!");
			} catch (Exception ignored) {}
		}
	}

	@Test
	public void test_inserts_sanity_with_invalid_timestamp() {
		Content content = createDummyContent();
		try {
			content.setAddedAt(System.currentTimeMillis() / 1000L / 1000);
			storageInterface.addContent(content);
			Assert.fail();
		} catch (Exception ignored) {}
	}

	@Test
	public void test_inserts_sanity_with_negative_numbers() {
		Content content = createDummyContent();
		try {
			content.setAddedAt(-400);
			storageInterface.addContent(content);
			Assert.fail();
		} catch (Exception ignored) {}
	}
}
