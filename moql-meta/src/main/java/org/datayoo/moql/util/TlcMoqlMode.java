package org.datayoo.moql.util;

public abstract class TlcMoqlMode {
  private static ThreadLocal<Boolean> moqlMode = new ThreadLocal<Boolean>() {
    protected synchronized Boolean initialValue() {
      return false;
    }
  };

  public static void setMoqlMode(boolean value) {
    moqlMode.set(value);
  }

  public static boolean isMoqlMode() {
    return moqlMode.get();
  }
}
