package org.moql.cep.metadata;

import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangtadin on 17/1/24.
 */
public class CeperMetadata implements Serializable {

  private String name;

  private String moql;

  private String winType;

  private int bucketCount;   // count

  private int bucketSize; // entity count or second

  private Map<String, String> parameters = new HashMap<String, String>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    Validate.notEmpty(name, "name is empty!");
    this.name = name;
  }

  public String getMoql() {
    return moql;
  }

  public void setMoql(String moql) {
    Validate.notEmpty(moql, "moql is empty!");
    this.moql = moql;
  }

  public String getWinType() {
    return winType;
  }

  public void setWinType(String winType) {
    Validate.notEmpty(winType, "winType is empty!");
    this.winType = winType;
  }

  public int getCapacity() {
    return bucketCount*bucketSize;
  }

  public int getBucketCount() {
    return bucketCount;
  }

  public void setBucketCount(int bucketCount) {
    Validate.isTrue(bucketCount > 0, "bucketSize is less than 1!");
    this.bucketCount = bucketCount;
  }

  public int getBucketSize() {
    return bucketSize;
  }

  public void setBucketSize(int bucketSize) {
    Validate.isTrue(bucketSize >= 0, "bucketSize is less than 0!");
    this.bucketSize = bucketSize;
  }

  public void addParameter(String name, String value) {
    Validate.notEmpty(name, "name is empty!");
    parameters.put(name, value);
  }

  public void removeParameter(String name) {
    Validate.notEmpty(name, "name is empty!");
    parameters.remove(name);
  }

  public Map<String ,String> getParameters() {
    return parameters;
  }

}
