package org.datayoo.moql.metadata;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2023/2/12 15:01
 */
public class CaseMetadata implements Serializable {

  protected List<WhenMetadata> whenMetadatas = new LinkedList<>();

  protected String elseMetadata;

  public CaseMetadata(List<WhenMetadata> whenMetadatas, String elseMetadata) {
    Validate.notEmpty(whenMetadatas, "whenMetadatas is empty!");
    Validate.notEmpty(elseMetadata, "elseMetadata is empty!");
    this.whenMetadatas = whenMetadatas;
    this.elseMetadata = elseMetadata;
  }

  public List<WhenMetadata> getWhenMetadatas() {
    return whenMetadatas;
  }

  public String getElseMetadata() {
    return elseMetadata;
  }
}
