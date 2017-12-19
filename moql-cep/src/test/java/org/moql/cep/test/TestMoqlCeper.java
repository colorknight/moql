package org.moql.cep.test;

import junit.framework.TestCase;
import org.moql.cep.Ceper;
import org.moql.cep.main.CepPrintListener;
import org.moql.cep.main.MoqlCeper;
import org.moql.cep.metadata.CeperMetadata;
import org.moql.cep.sw.MatcherWindow;
import org.moql.cep.sw.SlideWindowEnum;

import java.util.List;

/**
 * Created by tangtadin on 17/1/27.
 */
public class TestMoqlCeper extends TestCase {


  public void testBatchWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    CeperMetadata metadata = new CeperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_BATCH.name());
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(5);
    metadata.setBucketSize(10);
    Ceper<Object[]> ceper = new MoqlCeper<Object[]>("evt", metadata);
    ceper.addCepListener(new CepPrintListener());
    for(Object[] data : dataList) {
      ceper.operate(data);
    }
  }

  public void testMatcherWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    dataList.add(new Object[] {'1', 0, 1});
    CeperMetadata metadata = new CeperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_MATCHER.name());
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(3);
    metadata.setBucketSize(0);
    metadata.getParameters().put(MatcherWindow.PARAM_MATCHER_EXPRESSION, "evt[0]");
    Ceper<Object[]> ceper = new MoqlCeper<Object[]>("evt", metadata);
    ceper.addCepListener(new CepPrintListener());
    for(Object[] data : dataList) {
      ceper.operate(data);
    }
  }

  public void testTimeWindow() {
    List<Object[]> dataList = DataSimulator.createDataList(5, 20);
    CeperMetadata metadata = new CeperMetadata();
    metadata.setName("cep1");
    metadata.setWinType(SlideWindowEnum.SW_TIME.name());
    metadata.setMoql("select evt[0] name, sum(evt[2]) sum from evt evt group by name having sum > 100");
    metadata.setBucketCount(3);
    metadata.setBucketSize(2);
    Ceper<Object[]> ceper = new MoqlCeper<Object[]>("evt", metadata);
    ceper.addCepListener(new CepPrintListener());
    int i = 0;
    long curMills = System.currentTimeMillis();
    for(Object[] data : dataList) {
      ceper.operate(data);
      i++;
      if (i == 10) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        ceper.onTick((int)(System.currentTimeMillis() - curMills));
        curMills = System.currentTimeMillis();
        i = 0;
      }
    }
  }
}
