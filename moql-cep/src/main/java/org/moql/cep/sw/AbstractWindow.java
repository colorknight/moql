package org.moql.cep.sw;

import org.apache.commons.lang.Validate;
import org.moql.*;
import org.moql.cep.CepListener;
import org.moql.cep.SlideWindow;
import org.moql.cep.metadata.CeperMetadata;
import org.moql.service.MoqlUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangtadin on 17/1/25.
 */
public abstract class AbstractWindow<T> implements SlideWindow<T> {

  protected String eventStreamName;

  protected CeperMetadata metadata;

  protected DataSetMap dataSetMap = new DataSetMapImpl();

  protected List<CepListener> cepListeners = new LinkedList<CepListener>();

  protected LinkedList<List<T>> buckets = new LinkedList<List<T>>();

  protected int bucketCount = 0;

  protected List<T> curBucket;

  protected Selector selector;

  public AbstractWindow(String eventStreamName, CeperMetadata metadata) {
    Validate.notEmpty(eventStreamName, "eventStreamName is empty!");
    Validate.notNull(metadata, "metadata is null!");
    this.eventStreamName = eventStreamName;
    this.metadata = metadata;
    bucketCount = calcBucketCount(metadata);
    curBucket = new LinkedList<T>();
    buckets.add(curBucket);
    try {
      selector = MoqlUtils.createSelector(metadata.getMoql());
    } catch (MoqlException e) {
      throw new IllegalArgumentException("moql is invalid!", e);
    }
  }

  protected int calcBucketCount(CeperMetadata metadata) {
    if (metadata.getBucketSize() == 0)
      return metadata.getCapacity();
    Validate.isTrue(metadata.getCapacity()%metadata.getBucketSize() == 0,
        "capacity should be divided with no remainder by bucketSize!");
    int bucketCount = metadata.getCapacity()/metadata.getBucketSize();
    return bucketCount;
  }

  @Override public void setContextDataSet(DataSetMap dataSetMap) {
    if (dataSetMap == null)
      return;
    this.dataSetMap = new DataSetMapImpl(dataSetMap);
  }

  @Override public void addCepListener(CepListener cepListener) {
    synchronized (cepListeners) {
      cepListeners.add(cepListener);
    }
  }

  @Override public void removeCepListener(CepListener cepListener) {
    synchronized (cepListener) {
      cepListeners.remove(cepListener);
    }
  }

  protected void nofityCepListeners(RecordSet recordSet) {
    for(CepListener listener : cepListeners) {
      listener.onTrigger(recordSet);
    }
  }

  protected void operate() {
    if (buckets.size() < bucketCount)
      return;
    List<T> list = packBuckets();
    dataSetMap.putDataSet(eventStreamName, list);
    selector.select(dataSetMap);
    try {
      RecordSet recordSet = selector.getRecordSet();
      if (recordSet.getRecords().size() > 0)
        nofityCepListeners(recordSet);
    } finally {
      selector.clear();
    }
  }

  protected List<T> packBuckets() {
    List<T> list = new LinkedList<T>();
    for(List<T> bucket : buckets) {
      list.addAll(bucket);
    }
    return list;
  }

  protected void updateBuckets() {
    if (buckets.size() == bucketCount)
      buckets.removeFirst();
    curBucket = new LinkedList<T>();
    buckets.add(curBucket);
  }

}
