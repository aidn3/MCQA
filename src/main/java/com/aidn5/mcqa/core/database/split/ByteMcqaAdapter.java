
package com.aidn5.mcqa.core.database.split;

import com.aidn5.mcqa.core.content.Content;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.annotation.Nonnull;


/**
 * Adapter saves the data in separate files for every individual content in
 * bytes form. This adapter has the best question search query and also one of
 * the fastest adapter. see {@link SplitMcqaAdapter}. Note: The generated data
 * from this adapter can not be edited with text editor. See
 * {@link JsonMcqaAdapter} for an adapter that have easy and readable files, but
 * also slower than this adapter.
 *
 * @author aidn5
 * 
 * @see SplitMcqaAdapter
 * @see JsonMcqaAdapter
 * @see MemoryMcqaAdapter
 */
public class ByteMcqaAdapter extends SplitMcqaAdapter {
  /**
   * Constructor for the adapter.
   * 
   * @param dirContents
   *          the dictionary to utilize for the database
   * 
   * @throws IOException
   *           if any I/O error occurs while trying to utilize the dictionary
   * @throws NullPointerException
   *           if {@code dirContents} is <code>null</code>
   */
  public ByteMcqaAdapter(@Nonnull File dirContents) throws IOException, NullPointerException {
    super(dirContents);
  }

  @Override
  protected Content toContent(byte[] contentBytes) throws Exception {
    final Content content = new Content();
    final ByteBuffer bf = ByteBuffer.wrap(contentBytes);

    byte[] bytes = new byte[bf.getShort()];
    bf.get(bytes);
    content.category = new String(bytes);

    content.addedAt = bf.getLong();

    if (bf.get() == 1) {
      bytes = new byte[36];
      bf.get(bytes);
      content.questionProvedBy = UUID.fromString(new String(bytes));
    }

    if (bf.get() == 1) {
      bytes = new byte[36];
      bf.get(bytes);
      content.questionCreator = UUID.fromString(new String(bytes));
    }

    content.questions = new String[readInt(bf)];
    for (int i = 0; i < content.questions.length; i++) {
      bytes = new byte[readInt(bf)];
      bf.get(bytes);
      content.questions[i] = new String(bytes);
    }

    content.answerProvedContent = new String[readInt(bf)];
    for (int i = 0; i < content.answerProvedContent.length; i++) {
      bytes = new byte[readInt(bf)];
      bf.get(bytes);
      content.answerProvedContent[i] = new String(bytes);
    }

    if (bf.get() == 1) {
      bytes = new byte[36];
      bf.get(bytes);
      content.creatorOfUnprovedAnswer = UUID.fromString(new String(bytes));
    }

    content.answerUnProvedContent = new String[readInt(bf)];
    for (int i = 0; i < content.answerUnProvedContent.length; i++) {
      bytes = new byte[readInt(bf)];
      bf.get(bytes);
      content.answerUnProvedContent[i] = new String(bytes);
    }

    return content;
  }

  @Override
  protected byte[] fromContent(Content content) throws Exception {
    ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
    byte[] bytes;

    bytes = content.category.getBytes();
    writeShort(os, bytes.length);
    os.write(bytes);

    writeLong(os, content.addedAt);

    if (content.questionProvedBy != null) {
      os.write((byte) 1);
      os.write(content.questionProvedBy.toString().getBytes());

    } else {
      os.write((byte) 0);
    }

    if (content.questionCreator != null) {
      os.write((byte) 1);
      os.write(content.questionCreator.toString().getBytes());

    } else {
      os.write((byte) 0);
    }

    if (content.questions != null) {
      writeInt(os, content.questions.length);

      for (int i = 0; i < content.questions.length; i++) {
        String string = content.questions[i];
        bytes = string.getBytes();
        writeInt(os, bytes.length);
        os.write(bytes);
      }

    } else {
      writeInt(os, 0);
    }

    if (content.answerProvedContent != null) {
      writeInt(os, content.answerProvedContent.length);

      for (int i = 0; i < content.answerProvedContent.length; i++) {
        String string = content.answerProvedContent[i];
        bytes = string.getBytes();
        writeInt(os, bytes.length);
        os.write(bytes);
      }

    } else {
      writeInt(os, 0);
    }

    if (content.creatorOfUnprovedAnswer != null) {
      os.write((byte) 1);
      os.write(content.creatorOfUnprovedAnswer.toString().getBytes());

    } else {
      os.write((byte) 0);
    }

    if (content.answerUnProvedContent != null) {
      writeInt(os, content.answerUnProvedContent.length);

      for (int i = 0; i < content.answerUnProvedContent.length; i++) {
        String string = content.answerUnProvedContent[i];
        bytes = string.getBytes();
        writeInt(os, bytes.length);
        os.write(bytes);
      }

    } else {
      writeInt(os, 0);
    }

    return os.toByteArray();
  }

  public static void writeShort(OutputStream os, int s) throws IOException {
    os.write((byte) ((s >>> 8) & 0xFF));
    os.write((byte) ((s >>> 0) & 0xFF));
  }

  public static void writeInt(OutputStream os, int i) throws IOException {
    os.write((byte) ((i >>> 24) & 0xFF));
    os.write((byte) ((i >>> 16) & 0xFF));
    os.write((byte) ((i >>> 8) & 0xFF));
    os.write((byte) ((i >>> 0) & 0xFF));
  }

  public static int readInt(ByteBuffer bf) {
    int ch1 = bf.get();
    int ch2 = bf.get();
    int ch3 = bf.get();
    int ch4 = bf.get();
    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
  }

  public static void writeLong(OutputStream os, long l) throws IOException {
    os.write((byte) (l >>> 56));
    os.write((byte) (l >>> 48));
    os.write((byte) (l >>> 40));
    os.write((byte) (l >>> 32));
    os.write((byte) (l >>> 24));
    os.write((byte) (l >>> 16));
    os.write((byte) (l >>> 8));
    os.write((byte) (l >>> 0));
  }

}
