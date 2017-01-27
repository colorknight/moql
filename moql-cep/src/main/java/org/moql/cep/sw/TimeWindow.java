package org.moql.cep.sw;

import org.apache.commons.lang.Validate;
import org.moql.cep.CepListener;
import org.moql.cep.SlideWindow;
import org.moql.cep.metadata.CeperMetadata;

import java.util.List;

/**
 * Created by tangtadin on 17/1/24.
 */
public class TimeWindow<T> extends AbstractWindow<T> {

  protected long curBucketMills = 0;

  protected long bucketMills = 0;

  public TimeWindow(String eventStreamName, CeperMetadata metadata) {
    super(eventStreamName, metadata);
    Validate.isTrue(metadata.getBucketSize() > 0, "bucket size less than 1!");
    bucketMills = metadata.getBucketSize() * 1000;
    curBucketMills = System.currentTimeMillis();
  }

  @Override public synchronized void push(List<T> dataSet) {
    for(T entity : dataSet) {
      push(entity);
    }
  }

  @Override public synchronized void push(T entity) {
    curBucket.add(entity);
  }

  @Override public synchronized void onTick(int tick) {
    if (curBucketMills + bucketMills < System.currentTimeMillis()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }
}
