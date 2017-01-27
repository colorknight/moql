package org.moql.cep;

import org.moql.DataSetMap;

import java.util.List;

/**
 * Created by tangtadin on 17/1/18.
 */
public interface Ceper<T> extends Ticker {

  void setContextDataSet(DataSetMap dataSetMap);

  void addCepListener(CepListener listener);

  void removeCepListener(CepListener listener);

  void operate(List<T> dataSet);

  void operate(T entity);
}
