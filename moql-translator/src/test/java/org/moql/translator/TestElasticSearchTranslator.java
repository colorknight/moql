package org.moql.translator;

import junit.framework.TestCase;
import org.moql.MoqlException;
import org.moql.sql.SqlDialectType;

public class TestElasticSearchTranslator extends TestCase {
  
  public void testSimpleQuery() {
    String sql = "select w.* from web w";
    testESDialect(sql);
  }
  
  public void testConditionQuery() {
    String sql = "select w.* from web w where w.port=443";
    testESDialect(sql);
  }
  
  public void testGropuQuery() {
    String sql = "select w.country, max(w.port), min(w.port) from web w group by w.country";
    testESDialect(sql);
  }
  
  public void testMultiColumnsGropuQuery() {
    String sql = "select w.country, w.ip, max(w.port), min(w.port) from web w where w.port>10 group by w.country, w.ip";
    testESDialect(sql);
  }
  
  public void testSimpleLimit() {
    String sql = "select w.* from web w where w.port=443 limit 10";
    testESDialect(sql);
  }
  
  public void testGroupLimit() {
    String sql = "select w.country, max(w.port), min(w.port) from web w where w.port=443 group by w.country limit 10";
    testESDialect(sql);
  }
  
  public void testSimpleOrder() {
    String sql = "select w.* from web w order by w.ip asc limit 10 ";
    testESDialect(sql);
  }
  
  public void testGroupOrder() {
    String sql = "select w.country, max(w.port), min(w.port) from web w where w.port=443 group by w.country order by w.country desc limit 10 ";
    testESDialect(sql);
  }
  
  public void testNEConditionQuery() {
    String sql = "select w.* from web w where w.port<>443";
    testESDialect(sql);
  }
  
  public void testDistinctQuery() {
    String sql = "select distinct w.port from web w where w.port<1000";
    testESDialect(sql);
  }
  
  public void testAndConditionQuery() {
    String sql = "select w.* from web w where w.port=443 and w.ip = '64.91.250.164' ";
    testESDialect(sql);
  }
  
  public void testOrConditionQuery() {
    String sql = "select w.* from web w where w.port=443 or w.port = 88 ";
    testESDialect(sql);
  }
  
  public void testAndOrConditionQuery() {
    String sql = "select w.* from web w where w.port=443 and w.country in ('美国','澳大利亚') or w.port = 88 and w.ip_num between 10000 and 1900000000";
    testESDialect(sql);
  }
  
  public void testLikeConditionQuery() {
    String sql = "select w.* from web w where w.country like '美%'";
    testESDialect(sql);
  }
  
  public void testRegexConditionQuery() {
    String sql = "select w.* from web w where regex(w.data, '.*web.*')";
    testESDialect(sql);
  }
  
  public void testIsConditionQuery() {
    String sql = "select w.* from web w where w.region is null";
    testESDialect(sql);
    sql = "select w.* from web w where w.region is not null";
    testESDialect(sql);
  }
  
  public void testParenConditionQuery() {
    String sql = "select w.* from web w where w.region is null and (w.port = 88 or w.port = 443)";
    testESDialect(sql);
  }
  
  public void testQMatchConditionQuery() {
    String sql = "select w.* from web w where w.region is null and qmatch(w.data, 'http')";
    testESDialect(sql);
    sql = "select w.* from web w where w.region is null and qmatch('w.data, w.service', 'http')";
    testESDialect(sql);
  }
  
  public void testQMoreLikeConditionQuery() {
    String sql = "select w.* from web w where w.region is null and qmoreLike('w.data,w.name', 'http', 1, 12)";
    testESDialect(sql);
  }
  
  public void testQuery() {
    String sql = "select es.* from virus_es es where es.country = 'CN' order by es.date_time desc";
    testESDialect(sql);
  }
  
  protected void testESDialect(String sql) {
    try {
      String es = MoqlTranslator.translateMoql2Dialect(sql, SqlDialectType.ELASTICSEARCH);
      es = es.trim();
      System.out.println(es);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
