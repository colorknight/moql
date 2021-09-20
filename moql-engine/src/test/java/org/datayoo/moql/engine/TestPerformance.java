package org.datayoo.moql.engine;

import junit.framework.TestCase;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.Filter;
import org.datayoo.moql.MoqlException;

public class TestPerformance extends TestCase {

  protected EntityMap entityMap;

  @Override
  protected void setUp() throws Exception {
    // TODO Auto-generated method stub
    super.setUp();
    entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 123);
    entityMap.putEntity("name", "123");
  }

  public void testFilter() {
    try {
      Filter filter = MoqlEngine.createFilter("name = '123'");
      long mills = System.currentTimeMillis();
      for (int i = 0; i < 10000; i++) {
        if (!filter.isMatch(entityMap)) {
          System.out.println("-------------");
        }
      }
      System.out.println(System.currentTimeMillis() - mills);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
