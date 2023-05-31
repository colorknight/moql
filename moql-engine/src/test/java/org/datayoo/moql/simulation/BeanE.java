package org.datayoo.moql.simulation;

public class BeanE {

  private String src;

  private String dst;

  private Object ary;

  public BeanE(String src, String dst) {
    this.src = src;
    this.dst = dst;
  }

  public String getSrc() {
    return src;
  }

  public String getDst() {
    return dst;
  }

  public Object getAry() {
    return ary;
  }

  public void setAry(Object ary) {
    this.ary = ary;
  }
}
