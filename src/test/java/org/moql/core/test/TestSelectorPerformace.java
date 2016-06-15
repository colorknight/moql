package org.moql.core.test;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.moql.ColumnDefinition;
import org.moql.DataSetMap;
import org.moql.DataSetMapImpl;
import org.moql.MoqlException;
import org.moql.RecordSet;
import org.moql.RecordSetDefinition;
import org.moql.Selector;
import org.moql.core.simulation.BeanFactory;
import org.moql.core.simulation.LittileBean;
import org.moql.service.MoqlUtils;

public class TestSelectorPerformace extends TestCase {

  public void testOrderPerformance() {
    /*
    List<LittileBean> littileBeanList = BeanFactory.createLittleBeanList(0,500000);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", littileBeanList);
    String sql = "select a.id from BeanA a order by a.id desc";
    try {
      Selector selector = MoqlUtils.createSelector(sql);
      long start = System.currentTimeMillis();
      System.out.println(start);
      selector.select(dataSetMap);
      long end = System.currentTimeMillis();
      //System.out.println(end);
      System.out.println("Used "+(end-start)+" milliseconds");
      
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
  }
  
  public void testGroupArrayPerformance() {
    /*
    List<Object[]> aryList = BeanFactory.createArrayList(100000);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("Array", aryList);
    String sql = "select count(a[0]) cnt, a[1] name from Array a group by a[1]";
    try {
      Selector selector = MoqlUtils.createSelector(sql);
      long start = System.currentTimeMillis();
      System.out.println(start);
      selector.select(dataSetMap);
      long end = System.currentTimeMillis();
      //System.out.println(end);
      System.out.println("Used "+(end-start)+" milliseconds");
      outputRecordSet(selector.getRecordSet());
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
  }
  
  public void testGroupMapPerformance() {
    /*
    List<Map<String,Object>> mapList = BeanFactory.createMapList(100000);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("Map", mapList);
    String sql = "select count(a.name) cnt, a.name from Map a group by a.name";
    try {
      Selector selector = MoqlUtils.createSelector(sql);
      long start = System.currentTimeMillis();
      System.out.println(start);
      selector.select(dataSetMap);
      long end = System.currentTimeMillis();
      //System.out.println(end);
      System.out.println("Used "+(end-start)+" milliseconds");
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
  }

  
  protected void outputRecordSet(RecordSet recordSet) {
    RecordSetDefinition recordSetDefinition = recordSet.getRecordSetDefinition();
    StringBuffer sbuf = new StringBuffer();
    for(ColumnDefinition column : recordSetDefinition.getColumns()) {
      sbuf.append(column.getName());
      sbuf.append("    ");
    }
    System.out.println(sbuf.toString());
    for(Object[] record : recordSet.getRecords()) {
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < record.length; i++) {
        if (record[i] != null) {
          sb.append(record[i].toString());
        } else {
          sb.append("NULL");
        }
        sb.append(" ");
      }
      System.out.println(sb.toString());
    }
  }
}
