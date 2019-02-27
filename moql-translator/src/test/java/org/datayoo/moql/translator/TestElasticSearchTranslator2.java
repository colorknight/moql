package org.datayoo.moql.translator;

import junit.framework.TestCase;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.sql.SqlDialectType;

public class TestElasticSearchTranslator2 extends TestCase {

  public void testEQQuery() {
    String sql = "select ip.fw, ip.pri, ip.recorder, ip.proto from ip3 ip where ip.pri = 6 order by ip.src LIMIT 1000";
    testESDialect(sql);
  }

  public void testConditionQuery() {
    String sql =
        "select ip.* from ip3 ip where (ip.sport=8888 or ip.sport=9999) and "
            + "ip.src='192.168.71.23' or ip.src='192.168.72.222'";
    testESDialect(sql);
  }

  public void testConditionAndHavingQuery() {
    String sql = "select ip.* from ip3 ip where ip.sport=1463 having ip.src='192.168.72.222'";
    testESDialect(sql);
  }

  public void testHavingQuery() {
    String sql = "select ip.* from ip3 ip having ip.src='192.168.71.23' "
        + "or ip.src='192.168.72.56'";
    testESDialect(sql);
  }

  public void testGroupOrder() {
    String sql = "select ip.src, ip.proto, max(ip.sport), min(ip.sport) from ip3 ip group by ip.src, ip.proto order by ip.src desc limit 2 ";
    testESDialect(sql);
  }

  public void testGroupOrder2() {
    String sql = "select ip.src, ip.proto, count(ip.sport), count(ip.sport, true), max(ip.sport), min(ip.sport) from ip3 ip group by ip.src, ip.proto order by ip.src desc limit 2 ";
    testESDialect(sql);
  }

  public void testAndOrConditionQuery() {
    String sql = "select ip.* from ip3 ip where ip.sport in (8888,9999) or ip.sport between 1000 and 2000";
    testESDialect(sql);
  }

  public void testNEQuery() {
    String sql = "select ip.* from ip3 ip where ip.proto <> 'tcp' LIMIT 1000";
    testESDialect(sql);
  }

  public void testLikeConditionQuery() {
    String sql = "select ip.* from ip3 ip where ip.src like '%72%'";
    testESDialect(sql);
  }

  public void testIsConditionQuery() {
    String sql = "select ip.* from ip3 ip where ip.src is not null";
    testESDialect(sql);
  }

  public void testMatchConditionQuery() {
    String sql = "select ip.* from ip3 ip where ip.proto is not null and match('ip.proto', 'udp')";
    testESDialect(sql);
  }

  public void testFuzzyConditionQuery() {
    String sql = "select ip.* from ip3 ip where ip.proto is not null and fuzzy('ip.proto', 'ud')";
    testESDialect(sql);
  }

  protected void testESDialect(String sql) {
    try {
      String es = MoqlTranslator
          .translateMoql2Dialect(sql, SqlDialectType.ELASTICSEARCH);
      es = es.trim();
      System.out.println(es);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
