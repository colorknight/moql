package org.datayoo.moql.metadata;

import org.apache.commons.lang3.Validate;

import java.io.Serializable;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2023/2/12 15:02
 */
public class WhenMetadata implements Serializable {
  protected OperationMetadata whenMetadata;

  protected String thenMetadata;

  public WhenMetadata(OperationMetadata whenMetadata, String thenClause) {
    Validate.notNull(whenMetadata, "when clause is null!");
    Validate.notNull(thenClause, "then clause is null!");
    this.whenMetadata = whenMetadata;
    this.thenMetadata = thenClause;
  }

  public OperationMetadata getWhenMetadata() {
    return whenMetadata;
  }

  public String getThenMetadata() {
    return thenMetadata;
  }
}
