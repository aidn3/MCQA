package com.aidn5.mcqa.tests.database;

import static com.aidn5.mcqa.tests.TestHelper.createDummyContent;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.tests.TestHelper;

public class RemoveTests {
	private StorageInterface storageInterface;

	@Before
	public void initDatabase() {
		storageInterface = TestHelper.getNewStorageInterface();
	}

	@Test
	public void testRemoveContents() {
		Content content = createDummyContent();
		// add contents
		storageInterface.addContent(content);
		storageInterface.addContent(content);
		storageInterface.addContent(content);
		storageInterface.addContent(content);

		// check whether they are added
		List<Content> contentsB = storageInterface.searchForContents(false, content.getQuestionContent().split(" "));
		Assert.assertFalse(0 == contentsB.size());

		// remove every one of them
		for (Content content2 : contentsB) {
			storageInterface.removeContent(content2.getContentId());
		}

		storageInterface.removeContent(4123);
		// check now whether they are removed
		contentsB = storageInterface.searchForContents(false, content.getQuestionContent().split(" "));
		Assert.assertEquals(0, contentsB.size());
	}
}
