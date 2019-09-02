
package com.aidn5.mcqa;

import java.util.UUID;

public class PluginConfig {
  private PluginConfig() {
    throw new AssertionError();
  }

  public static final String ID = "mcqa";
  public static final String NAME = "MCQA";
  public static final String AUTHOR = "aidn5";
  public static final UUID AUTHOR_UUID = UUID.fromString("e4c0f223-ead4-4441-bfff-7f34d2e8bcab");
  public static final String VERSION = "0.0.3-SNAPSHOT";
}
