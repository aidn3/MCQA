package com.aidn5.mcqa.tests.database;

import static com.aidn5.mcqa.tests.TestHelper.createDummyContent;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.tests.TestHelper;

public class SearchGetTests {
	private StorageInterface storageInterface;

	@Before
	public void initDatabase() {
		storageInterface = TestHelper.getNewStorageInterface();
	}

	@Test
	public void testGetOnlyApprovedContents() {
		List<Content> all = storageInterface.getAllContents(false);
		for (Content content2 : all) {
			storageInterface.removeContent(content2.getContentId());
		}


		Content content = createDummyContent();

		content.setQuestionProved(false);
		storageInterface.addContent(content);
		storageInterface.addContent(content);
		storageInterface.addContent(content);

		content.setQuestionProved(true);
		storageInterface.addContent(content);
		storageInterface.addContent(content);

		List<Content> allContents = storageInterface.getAllContents(false);
		List<Content> onlyProvedContents = storageInterface.getAllContents(true);

		Assert.assertFalse(allContents.size() == onlyProvedContents.size());
		Assert.assertEquals(5, allContents.size());
		Assert.assertEquals(2, onlyProvedContents.size());
	}
}
