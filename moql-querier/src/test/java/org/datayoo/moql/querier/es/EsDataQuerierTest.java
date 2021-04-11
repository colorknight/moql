package org.datayoo.moql.querier.es;

import junit.framework.TestCase;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;

import java.io.IOException;
import java.util.Properties;

public class EsDataQuerierTest extends TestCase {

  protected EsDataQuerier dataQuerier = new EsDataQuerier();

  @Override
  public void setUp() throws Exception {
    //    String[] serverIps = new String[] {"172.21.5.221"};
    String[] serverIps = new String[] { "127.0.0.1" };
    Properties properties = new Properties();
    //properties.put(EsDataQuerier.HTTP_PORT, 9200);
    //    properties.put(EsDataQuerierOld.CLUSTER_NAME, "bdp-test");
    //    properties.put(EsDataQuerierOld.CLIENT_TRANSPORT_SNIFF, true);
    dataQuerier.connect(serverIps, properties);
    super.setUp();
  }

  @Override
  public void tearDown() throws Exception {
    dataQuerier.disconnect();
    super.tearDown();
  }

  public void testCommonQuery() {
    String sql = "select ip.fw, ip.pri, ip.recorder, ip.proto from ip3 ip where ip.pri = 6 order by ip.src LIMIT 1000";
    try {
      RecordSet recordSet = dataQuerier.query(sql);
      outputRecordSet(recordSet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testCommonQuery2() {
    String sql = "select t.DVC_ADDRESS, t.MESSAGE from ins_test t order by t.SEVERITY LIMIT 5";
    try {
      CommonSupplementReader supplementReader = new CommonSupplementReader();
      RecordSet recordSet = dataQuerier.query(sql, supplementReader);
      outputRecordSet(recordSet);
      System.out.println(supplementReader.getTotalHits());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testGroupQuery() {
    String sql = "select ip.src, ip.proto, max(ip.sport), min(ip.sport) from ip3 ip group by ip.src, ip.proto order by ip.src desc limit 2 ";
    try {
      RecordSet recordSet = dataQuerier.query(sql);
      outputRecordSet(recordSet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testGroupQuery2() {
    String sql = "select ip.src, ip.proto, count(ip.sport) cnt, count(ip.sport, true), max(ip.sport), min(ip.sport) from ip3 ip group by ip.src, ip.proto order by ip.src desc limit 2 ";
    try {
      RecordSet recordSet = dataQuerier.query(sql);
      outputRecordSet(recordSet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void testGroupQuery3() {
    String sql = "select t.SEVERITY,t.DVC_RECEIPT_TIME, count(t.MESSAGE) from ins_test t group by t.SEVERITY, t.DVC_RECEIPT_TIME order by t.SEVERITY LIMIT 5";
    try {
      RecordSet recordSet = dataQuerier.query(sql);
      outputRecordSet(recordSet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void outputRecordSet(RecordSet recordSet) {
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    StringBuffer sbuf = new StringBuffer();
    for (ColumnDefinition column : recordSetDefinition.getColumns()) {
      sbuf.append(column.getName());
      sbuf.append("    ");
    }
    System.out.println(sbuf.toString());
    for (Object[] record : recordSet.getRecords()) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < record.length; i++) {
        if (record[i] != null) {
          sb.append(record[i].toString());
        } else {
          sb.append("NULL");
        }
        sb.append(" ");
      }
      System.out.println(sb.toString());
    }
    System.out.println("------------------------------------------------");
  }
}
