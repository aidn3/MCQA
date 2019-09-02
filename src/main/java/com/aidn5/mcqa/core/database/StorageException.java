
package com.aidn5.mcqa.core.database;

import javax.annotation.Nullable;

/**
 * SuperClass Exception for MCQA's database exdended from
 * {@link RuntimeException}. Used to wrap all exceptions in internal methods.
 * 
 * @author aidn5
 */
public class StorageException extends RuntimeException {
  private static final long serialVersionUID = -6195242828970531266L;
  private final boolean isInternal;

  /**
   * Create an empty exception and mark it as {@link #isInternal()}.
   */
  public StorageException() {
    this(null);
  }

  /**
   * Create an exception with the specified {@code message} and mark it as
   * {@link #isInternal()}.
   * 
   * @param message
   *          the detail message (which is saved for later retrieval by the
   *          {@link #getMessage()} method).
   */
  public StorageException(@Nullable String message) {
    this(message, null);
  }

  /**
   * Constructs a new Storage exception with the specified detail
   * message and cause.
   * 
   * @param message
   *          the detail message (which is saved for later retrieval by the
   *          {@link #getMessage()} method).
   * @param cause
   *          the cause (which is saved for later retrieval by the
   *          {@link #getCause()} method). (A null value is permitted, and
   *          indicates that the cause is nonexistent or unknown.)
   *          If the cause is {@link IllegalArgumentException}
   *          {@link #isInternal()} will return <code>false</code>.
   */
  public StorageException(@Nullable String message, @Nullable Throwable cause) {
    super(message, cause);

    if (cause != null && cause instanceof IllegalArgumentException) {
      isInternal = false;
    } else {
      isInternal = true;
    }
  }

  /**
   * Check whether the error was internal by checking if the constructed Exception
   * is not created by {@link IllegalArgumentException}.
   * 
   * @return <code>true</code> if the exception is not constructed by
   *         {@link IllegalArgumentException}.
   */
  public boolean isInternal() {
    return isInternal;
  }
}
