package org.moql.cep.sw;

import org.apache.commons.lang.Validate;
import org.moql.cep.metadata.CeperMetadata;

import java.util.List;

/**
 * Created by tangtadin on 17/1/24.
 */
public class BatchWindow<T> extends AbstractWindow<T> {

  public BatchWindow(String eventStreamName, CeperMetadata metadata) {
    super(eventStreamName, metadata);
    Validate.isTrue(metadata.getBucketSize() > 0, "bucket size less than 1!");
  }

  @Override synchronized public void push(List<T> dataSet) {
    for(T entity : dataSet) {
      push(entity);
    }
  }

  @Override synchronized public void push(T entity) {
    curBucket.add(entity);
    if (curBucket.size() == metadata.getBucketSize()) {
      operate();
      updateBuckets();
    }
  }

  @Override public void onTick(int tick) {

  }
}
