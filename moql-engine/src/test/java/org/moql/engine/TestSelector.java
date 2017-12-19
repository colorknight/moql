package org.moql.engine;

import junit.framework.TestCase;
import org.moql.*;
import org.moql.simulation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestSelector extends TestCase {

  public void testUnaryWhere() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "select a.id, a.name, a.num%50 from BeanA a where true";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testLimit() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "select a.id, a.name, a.num%50 from BeanA a where true limit 20";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50 from BeanA a where true limit 10,10%";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50 from BeanA a where true limit 50,20";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testOneSingleTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "select a.id, a.name, a.num%50 from BeanA a where a.num%500 > 10 order by 3";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public void testAggregationOneTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select Count(a.id) cnt, Sum(a.num) sum FROM BeanA a";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testGroupOneTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select Count(a.id) cnt, Sum(a.num) sum, a.num%50 mod1, a.num%8 mod2 FROM BeanA a group by mod2, 3 having mod1 > 10 order by 1";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testGroupOrderSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select Count(a.id) cnt, Sum(a.num) sum, a.num%50 mod FROM BeanA a group by 3 having mod > 10 order by a.name desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testIsConditionSelector() {
    List<Object[]> arrayList = BeanFactory.createObjectArrayList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("arylist", arrayList);
    String sql = "Select a[2] grp, count(a[0]) cnt FROM arylist a group by a[2] having grp is not null";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoSingleTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num%500 "
        + "from BeanA a, BeanB b where a.num%50 > 10 or b.num%500 < 400 Order By 3 desc, 6 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoLeftJoinTableSelector1() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 10);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num%50 > 10 or b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a left Join BeanB b on a.id = b.id Where a.num%50 > 10 and b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoLeftJoinTableSelector2() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 10);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num%50 > 10 or b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a LEFT join BeanB b on a.id = b.id where a.num%50 > 10 and b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoRightJoinTableSelector1() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 10);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a RIGHT join BeanB b on a.id = b.id where a.num%50 > 10 or b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num%50 > 10 and b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoRightJoinTableSelector2() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 10);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num%50 > 10 or b.num > 200 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("--------------------------------------");
    sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num%50 > 10 and b.num > 200 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoFullJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(3, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a full join BeanB b on a.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoInnerJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(3, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id, a.name, a.num%50, b.id, b.name, b.num "
        + "from BeanA a inner join BeanB b on a.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
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

  protected void outputRecordNode(List<RecordNode> recordNodes, int indentation) {
    StringBuffer prefix = new StringBuffer();
    for (int i = 0; i < indentation; i++) {
      prefix.append('\t');
    }
    for (RecordNode recordNode : recordNodes) {
      StringBuffer sbuf = new StringBuffer(prefix);
      int i = 0;
      for (Object column : recordNode.getColumns()) {
        if (i != 0) {
          sbuf.append(',');
        }
        sbuf.append(column.toString());
        i++;
      }
      System.out.println(sbuf.toString());
      outputRecordNode(recordNode.getChildren(), indentation + 1);
    }
  }

  public void testThreeSingleTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(0, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(0, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a, BeanB b, BeanC c where a.num%50 > 100 and b.num%500 < 400 order by 3 desc, 6 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testThreeLeftJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(2, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a left join BeanB b on a.id = b.id left join BeanC c on c.id = b.id where a.num%500 > 100 or b.num < 400 order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testThreeRightJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(2, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a right join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testThreeFullJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(2, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a full join BeanB b on a.id = b.id full join BeanC c on c.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testThreeInnerJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(2, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a inner join BeanB b on a.id = b.id inner join BeanC c on c.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testThreeLeftAndRightJoinTableSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    List<BeanC> beanCList = BeanFactory.createBeanCList(2, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    dataSetMap.putDataSet("BeanC", beanCList);
    String sql = "select a.id 标示, a.name 名称, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
        + "from BeanA a left join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoUnionSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "(select a.id id, a.name name, a.num num from BeanA a) "
        + "union (select b.name name, b.id id, b.num num from BeanB b) order by id desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoIntersectSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id id, a.num num from BeanA a "
        + "intersect select b.id id, b.num num from BeanB b order by id desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoExceptSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "(select a.id id, a.num num from BeanA a order by id desc) "
        + "except (select b.id id, b.num num from BeanB b order by id asc limit 3) order by id desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoSymExceptSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id id, a.num num from BeanA a "
        + "symexcept select b.id id, b.num num from BeanB b order by id desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTwoComplementationSelector() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 5);
    List<BeanB> beanBList = BeanFactory.createBeanBList(1, 5);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    dataSetMap.putDataSet("BeanB", beanBList);
    String sql = "select a.id id, a.num num from BeanA a "
        + "complementation select b.num num, b.id id from BeanB b order by id desc";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testNestSelector() {
    List<BeanE> beanAList = BeanFactory.createBeanEList();
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanE", beanAList);
    String sql = "select b.src src, count(b.dst) num from (select distinct a.src src, a.dst dst from BeanE a) b group by src";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testCountFunctionAsNestSelector() {
    List<BeanE> beanAList = BeanFactory.createBeanEList();
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanE", beanAList);
    String sql = "select b.src src, count(b.dst, true) num from BeanE b group by src";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testOutputRecordNode() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "select count(a.id) cnt, sum(a.num) sum, a.num%50 mod from BeanA a group by 3 having mod > 10 order by 1";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
      System.out.println("-----------------------------------");
      List<RecordNode> recordNodes = recordSet.getRecordsAsNodes();
      outputRecordNode(recordNodes, 0);
      String[] columns = new String[] {
          "cnt", "mod"
      };
      recordNodes = recordSet.getRecordNodesByColumns(columns);
      outputRecordNode(recordNodes, 0);
      columns = new String[] {
        "cnt,mod"
      };
      recordNodes = recordSet.getRecordNodesByColumns(columns);
      outputRecordNode(recordNodes, 0);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testGroupOrdinalDecorator() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select a.num%100%2 mod, a.num%1000%3 num, a.id id, 1 序号 FROM BeanA a order by mod,num,id decorate by groupOrdinal('mod,num','序号')";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testRowTranspositonDecorator() {
    List<ScoreItem> scoreItems = BeanFactory.createScoreItems();
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("SI", scoreItems);
    String sql = "Select si.id id, si.student student, si.subject subject,"
        + "si.term1 t1, si.term2 t2, si.term3 t3, si.term4 t4 FROM SI si ";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
      sql = "Select si.id id, si.student student, si.subject subject,"
          + "si.term1 t1, si.term2 t2, si.term3 t3, si.term4 t4 FROM SI si decorate by rowTransposition('subject','t1,t2,t3,t4', 'term', 'student' )";
      selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
      sql = "Select si.id id, si.student student, si.subject subject,"
          + "si.term1 t1, si.term2 t2, si.term3 t3, si.term4 t4 FROM SI si decorate by rowTransposition('subject','t1', '', 'student' )";
      selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);

    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testTotalCaculationDecorator() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select a.num%5 grp1, a.num%50 grp2, count(a.id) cnt, Sum(a.num) sum FROM BeanA a group by grp1,grp2 decorate by totalCaculation('sum1', 'sum2')";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    sql = "Select a.num%5 grp1, a.num%50 grp2, count(a.id) cnt, Sum(a.num) sum FROM BeanA a group by grp1,grp2 decorate by totalCaculation(null, 'sum2')";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testOtherCaculationDecorator() {
    List<BeanA> beanAList = BeanFactory.createBeanAList(0, 100);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("BeanA", beanAList);
    String sql = "Select a.num%5 grp1, a.num%50 grp2, count(a.id) cnt, Sum(a.num) sum FROM BeanA a group by grp1,grp2 decorate by otherCaculation('other1:5', 'other2:5')";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    sql = "Select a.num%5 grp1, a.num%50 grp2, count(a.id) cnt, Sum(a.num) sum FROM BeanA a group by grp1,grp2 decorate by otherCaculation(null, 'other2:5')";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testMergeColumns() {
    List<Map<String, String>> mapList = new LinkedList<Map<String, String>>();
    Map<String, String> record = new HashMap<String, String>();
    record.put("name", "a");
    record.put("col1", "a1");
    mapList.add(record);
    record = new HashMap<String, String>();
    record.put("name", "a");
    record.put("col2", "a2");
    mapList.add(record);
    DataSetMap dataSetMap = new DataSetMapImpl();
    dataSetMap.putDataSet("MapA", mapList);
    String sql = "Select a.name name, notNull(a.col1) col1, "
        + "notNull(a.col2) col2, notNull(a.col3, 3) col3 "
        + "FROM MapA a group by name";
    try {
      Selector selector = MoqlEngine.createSelector(sql);
      selector.select(dataSetMap);
      RecordSet recordSet = selector.getRecordSet();
      outputRecordSet(recordSet);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
