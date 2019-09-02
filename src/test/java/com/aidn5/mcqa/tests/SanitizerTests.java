
package com.aidn5.mcqa.tests;

import com.aidn5.mcqa.core.content.Sanitizer;

import org.junit.Assert;
import org.junit.Test;

public class SanitizerTests {

  @Test
  public void test_with_various_one_word_inputs() {
    Assert.assertEquals("", Sanitizer.sanitizeText((String) null, false));
    Assert.assertEquals("", Sanitizer.sanitizeText("", false));
    Assert.assertEquals("", Sanitizer.sanitizeText("!#$!#$", false));
    Assert.assertEquals("", Sanitizer.sanitizeText("123 #@ ", false));
    Assert.assertEquals("", Sanitizer.sanitizeText(".", false));
    Assert.assertEquals("", Sanitizer.sanitizeText(" ", false));
    Assert.assertEquals("", Sanitizer.sanitizeText("123", false));
    // Assert.assertEquals("a", " ä Ä a Ä ");

    // unicode char
    Assert.assertEquals("3 a",
        Sanitizer.sanitizeText(((char) ((123 << 8) + (244 << 0))) + "3 a", false));
  }

  @Test
  public void test_with_special_char_on() {
    Assert.assertEquals("", Sanitizer.sanitizeText("", true));
    Assert.assertEquals("!#$!#$", Sanitizer.sanitizeText("!#$!#$", true));
    Assert.assertEquals("123 #@", Sanitizer.sanitizeText("123 #@ ", true));
    Assert.assertEquals("123 #@", Sanitizer.sanitizeText("123 #@ ", true));
    Assert.assertEquals(".", Sanitizer.sanitizeText(".", true));
    Assert.assertEquals("", Sanitizer.sanitizeText("   ", true));
    Assert.assertEquals("", Sanitizer.sanitizeText("123", true));
  }

  @Test
  public void test_with_various_long_text_input() {
    Assert.assertEquals("abcdefghijklmnopqrstuvwxyz",
        Sanitizer.sanitizeText("abcdefghijklmnopqrstuvwxyz", true));

    Assert.assertEquals("abcdefghijklmnopqrstuvwxyz",
        Sanitizer.sanitizeText(" abcdefghijklmnopqrstuvwxyz ", true));
    Assert.assertEquals("abcdefghijklmnopqrstuvwxyz",
        Sanitizer.sanitizeText(" abcdefghijklmnopqrstuvwxyz ", false));

    Assert.assertEquals("E13e ads 2ed d", Sanitizer.sanitizeText("  E13e ads 2ed $@d  ", false));
    Assert.assertEquals("E13e ads 2ed d", Sanitizer.sanitizeText("  E13e ads 2ed d  ", true));

    Assert.assertEquals("Who are You", Sanitizer.sanitizeText("  Who, are. You?  ", false));
    Assert.assertEquals("Who, are. You?", Sanitizer.sanitizeText("  Who, are. You?  ", true));
  }

  @Test
  public void text_with_one_long_complicated_text() {
    String input = " here is. what?\r\n";
    input += "- one two!\r\n";
    input += "- two? NO$\r\n";
    input += "its <3 for :) \r\n";

    String expected = "here is. what?\r\n";
    expected += "- one two!\r\n";
    expected += "- two? NO$\r\n";
    expected += "its <3 for :)";

    Assert.assertEquals(expected, Sanitizer.sanitizeText(input, true));
  }

  @Test
  public void test_special_char() {
    String string = "!@#$%^&*()_+=-\\|'°\"/<>`~[]{}?.,:";
    Assert.assertEquals(string, Sanitizer.sanitizeText(string, true));
  }
}
