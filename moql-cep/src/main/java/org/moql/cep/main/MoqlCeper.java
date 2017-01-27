package org.moql.cep.main;

import org.apache.commons.lang.Validate;
import org.moql.DataSetMap;
import org.moql.cep.CepListener;
import org.moql.cep.Ceper;
import org.moql.cep.SlideWindow;
import org.moql.cep.metadata.CeperMetadata;
import org.moql.cep.sw.SlideWindowEnum;
import org.moql.cep.sw.SlideWindowFactory;

import java.util.List;

/**
 * Created by tangtadin on 17/1/26.
 */
public class MoqlCeper<T> implements Ceper<T> {

  protected SlideWindow<T> slideWindow;


  public MoqlCeper(String eventStreamName, CeperMetadata metadata) {
    SlideWindowFactory<T> factory = new SlideWindowFactory<T>();
    SlideWindowEnum swEnum = SlideWindowEnum.valueOf(metadata.getWinType());
    slideWindow = factory.createSlideWindow(swEnum, eventStreamName, metadata);
  }

  @Override public void setContextDataSet(DataSetMap dataSetMap) {
    slideWindow.setContextDataSet(dataSetMap);
  }

  @Override public void addCepListener(CepListener listener) {
    slideWindow.addCepListener(listener);
  }

  @Override public void removeCepListener(CepListener listener) {
    slideWindow.removeCepListener(listener);
  }

  @Override public void operate(List<T> dataSet) {
    slideWindow.push(dataSet);
  }

  @Override public void operate(T entity) {
    slideWindow.push(entity);
  }

  @Override public void onTick(int tick) {
    slideWindow.onTick(tick);
  }
}
