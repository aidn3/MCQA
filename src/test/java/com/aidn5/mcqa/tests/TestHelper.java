package com.aidn5.mcqa.tests;

import java.io.File;

import org.junit.Assert;

import com.aidn5.mcqa.core.dataholders.Content;
import com.aidn5.mcqa.core.interfaces.StorageInterface;
import com.aidn5.mcqa.core.storage.SQLiteConnection;

public class TestHelper {
	private TestHelper() {
		// private constructor
	}

	public static File dbPath = new File("/home/aidn5/testMC.db");
	private static StorageInterface storageInterface;

	public static StorageInterface getNewStorageInterface() {
		if (storageInterface != null) {
			storageInterface.closeConnection();
			if (dbPath.exists()) dbPath.delete();
		}

		// storageInterface = new SQLiteConnection(dbPath.getAbsolutePath());
		storageInterface = new SQLiteConnection();
		return storageInterface;

	}

	public static Content createDummyContent() {
		Content content = new Content();

		content.setCategory("hello");
		content.setAddedAt(System.currentTimeMillis() / 1000L);
		content.setAnswerProvedContent("GOOD answer");
		content.setAnswerUnProvedContent("BAD answer");
		content.setCreatorOfUnprovedContent("anyone");
		content.setQuestionContent("who am i?");
		content.setQuestionCreator("good Person");
		content.setQuestionProved(false);

		return content;
	}

	public static void assertContents(Content orginal, Content fromDB) {
		orginal.setContentId(fromDB.getContentId()); // dummy contents don't have a real id form a database
		Assert.assertTrue(orginal.equals(fromDB));
	}
}
