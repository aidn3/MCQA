package com.aidn5.mcqa.core.storage.exceptions;

public class StorageException extends RuntimeException {
	private static final long serialVersionUID = -6195242828970531266L;
	private final boolean isInternal;

	public StorageException() {
		this(null);
	}

	public StorageException(String message) {
		this(message, null);
	}

	public StorageException(String message, Throwable e) {
		super(message, e);

		if (e != null && e instanceof IllegalArgumentException) {
			isInternal = false;
		} else {
			isInternal = true;
		}
	}

	public boolean isInternal() {
		return isInternal;
	}
}
