package org.moql.cep.sw;

import org.apache.commons.lang.Validate;
import org.moql.cep.metadata.CeperMetadata;

import java.util.List;

/**
 * Created by tangtadin on 17/1/26.
 */
public class BatchAndTimeWindow<T> extends AbstractWindow<T> {

  public static final String PARAM_TIME_BUCKET_SIZE = "win.batchandtime.timebucketsize";

  protected long curBucketMills = 0;

  protected long bucketMills = 0;

  public BatchAndTimeWindow(String eventStreamName, CeperMetadata metadata) {
    super(eventStreamName, metadata);
    String param = metadata.getParameters().get(PARAM_TIME_BUCKET_SIZE);
    Validate.notEmpty(param, "time bucket size is empty!");
    bucketMills = Long.valueOf(param) * 1000;
    curBucketMills = System.currentTimeMillis();
  }

  @Override public synchronized void push(List<T> dataSet) {
    for (T entity : dataSet) {
      push(entity);
    }
  }

  @Override public synchronized void push(T entity) {
    curBucket.add(entity);
    if (curBucket.size() == metadata.getBucketSize()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }

  @Override public synchronized void onTick(int tick) {
    if (curBucketMills + bucketMills > System.currentTimeMillis()) {
      operate();
      updateBuckets();
      curBucketMills = System.currentTimeMillis();
    }
  }
}
