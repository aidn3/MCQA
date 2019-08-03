package com.aidn5.mcqa.core.dataholders;

public enum ContentsType {
	contentId(long.class, 10), 
	category(String.class, 16), 
	addedAt(long.class, 10), 
	isQuestionProved(boolean.class, 1),
	questionCreator(String.class, 36),
	questionContent(String.class, 128), 
	answerProvedContent(String.class, 16384),
	answerUnProvedContent(String.class, 16384), 
	creatorOfUnprovedContent(String.class, 16384);

	private final Class<?> classType;
	private final int maxContentLength;

	private ContentsType(Class<?> classType, int maxContentLength) {
		this.classType = classType;
		this.maxContentLength = maxContentLength;
	}
	public Class<?> getClassType() {
		return classType;
	}

	public int getMaxContentLength() {
		return maxContentLength;
	}
}
