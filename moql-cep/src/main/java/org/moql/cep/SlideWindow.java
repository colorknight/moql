package org.moql.cep;

import org.moql.DataSetMap;

import java.util.List;

/**
 * Created by tangtadin on 17/1/22.
 */
public interface SlideWindow<T> extends Ticker {

  void setContextDataSet(DataSetMap dataSetMap);

  void push(List<T> dataSet);

  void push(T entity);

  void addCepListener(CepListener listener);

  void removeCepListener(CepListener listener);
}
