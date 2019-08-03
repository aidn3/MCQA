package com.aidn5.mcqa.core.dataholders;

public class Content {
	/**
	 * the category of the question
	 */
	private String category;

	/**
	 * the Unix timestamp in seconds when the question was added/created
	 */
	private long addedAt;
	/**
	 * the id associated with the question
	 */
	private long contentId;
	/**
	 * is the question proved by an authority to view
	 */
	private boolean isQuestionProved;
	/**
	 * The name of the person who created the question
	 */
	private String questionCreator;

	/**
	 * what the question is
	 */
	private String questionContent;
	/**
	 * The answer, which is proved by an authority, to view
	 */
	private String answerProvedContent;
	/**
	 * The answer, which <i><u>is waiting to be proved by an authority</u></i>, to
	 * view
	 * 
	 * @see #creatorOfUnprovedContent
	 */
	private String answerUnProvedContent;
	/**
	 * The creator/Editor of the content, which is waiting to be approved by an
	 * authority
	 */
	private String creatorOfUnprovedContent;

	/**
	 * create new instance to use, hold data or add to the database
	 * 
	 * @param category
	 *            the category of the question
	 * @param isProved
	 *            is the content of the question and answer are proved/made by an
	 *            Authority
	 * @param questionContent
	 *            what the question is
	 * @param answerContent
	 *            the content of the answer to the question
	 * @param questionCreator
	 *            the name of the person who created the question and the answer
	 * 
	 * @return an instance holds the content
	 */
	public static Content createNewContent(String category, boolean isProved, String questionContent,
			String answerContent, String questionCreator) {
		Content content = new Content();

		content.setCategory(category);
		content.setQuestionContent(questionContent);
		content.setQuestionProved(isProved);
		if (isProved) {
			content.setAnswerProvedContent(answerContent);
		} else {
			content.setAnswerUnProvedContent(answerContent);
		}
		content.setAddedAt(System.currentTimeMillis() / 1000L);
		content.setQuestionCreator(questionCreator);

		return content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (addedAt ^ (addedAt >>> 32));
		result = prime * result + ((answerProvedContent == null) ? "".hashCode() : answerProvedContent.hashCode());
		result = prime * result + ((answerUnProvedContent == null) ? "".hashCode() : answerUnProvedContent.hashCode());
		result = prime * result + ((category == null) ? "".hashCode() : category.hashCode());
		result = prime * result + (int) (contentId ^ (contentId >>> 32));
		result = prime * result
				+ ((creatorOfUnprovedContent == null) ? "".hashCode() : creatorOfUnprovedContent.hashCode());
		result = prime * result + (isQuestionProved ? 1231 : 1237);
		result = prime * result + ((questionContent == null) ? "".hashCode() : questionContent.hashCode());
		result = prime * result + ((questionCreator == null) ? "".hashCode() : questionCreator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;

		if (getClass() != obj.getClass()) return false;
		Content other = (Content) obj;

		if (!getCategory().equals(other.getCategory())) return false;
		if (getAddedAt() != other.getAddedAt()) return false;
		if (getContentId() != other.getContentId()) return false;

		if (isQuestionProved() != other.isQuestionProved()) return false;
		if (!getQuestionCreator().equals(other.getQuestionCreator())) return false;
		if (!getQuestionContent().equals(other.questionContent)) return false;

		if (!getAnswerProvedContent().equals(other.getAnswerProvedContent())) return false;
		if (!getAnswerUnProvedContent().equals(other.getAnswerUnProvedContent())) return false;
		if (!getCreatorOfUnprovedContent().equals(other.getCreatorOfUnprovedContent())) return false;

		return true;
	}

	public String getCategory() {
		return category;
	}

	public Content setCategory(String category) {
		this.category = category;
		return this;
	}

	public long getAddedAt() {
		return addedAt;
	}

	public Content setAddedAt(long addedAt) {
		this.addedAt = addedAt;
		return this;
	}

	public long getContentId() {
		return contentId;
	}

	public Content setContentId(long contentId) {
		this.contentId = contentId;
		return this;
	}

	public boolean isQuestionProved() {
		return isQuestionProved;
	}

	public Content setQuestionProved(boolean isQuestionProved) {
		this.isQuestionProved = isQuestionProved;
		return this;
	}

	public String getQuestionCreator() {
		return questionCreator;
	}

	public Content setQuestionCreator(String questionCreator) {
		this.questionCreator = questionCreator;
		return this;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public Content setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
		return this;
	}

	public String getAnswerProvedContent() {
		return answerProvedContent;
	}

	public Content setAnswerProvedContent(String answerProvedContent) {
		this.answerProvedContent = answerProvedContent;
		return this;
	}

	public String getAnswerUnProvedContent() {
		return answerUnProvedContent;
	}

	public Content setAnswerUnProvedContent(String answerUnProvedContent) {
		this.answerUnProvedContent = answerUnProvedContent;
		return this;
	}

	public String getCreatorOfUnprovedContent() {
		return creatorOfUnprovedContent;
	}

	public Content setCreatorOfUnprovedContent(String creatorOfUnprovedContent) {
		this.creatorOfUnprovedContent = creatorOfUnprovedContent;
		return this;
	}

}
