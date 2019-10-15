package org.datayoo.moql.translator;

import junit.framework.TestCase;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.sql.SqlDialectType;
import org.datayoo.moql.sql.es.EsTranslationContextConstants;

import java.util.HashMap;
import java.util.Map;

public class TestElasticSearchTranslator extends TestCase {

  public void testSimpleQuery() {
    String sql = "select w.* from web w";
    testESDialect(sql);
  }

  public void testConditionQuery() {
    String sql = "select w.* from web w where w.port=443";
    testESDialect(sql);
  }

  public void testConditionQuery2() {
    String sql = "select w.* from web w where (w.port=443 or w.port=8080) and "
        + "w.ip='127.0.0.1' or w.ip='127.0.0.2'";
    testESDialect(sql);
  }

  public void testConditionAndHavingQuery() {
    String sql = "select w.* from web w where w.port=443 having w.ip='192.168.6.1'";
    testESDialect(sql);
  }

  public void testConditionAndHavingQuery2() {
    String sql = "select w.* from web w where w.port=443 and"
        + " not w.ip='192.1.2.3' having w.ip='192.168.6.1' or w.ip='192.168.3.2' or w.ip='192.168.7.3'";
    testESDialect(sql);
  }

  public void testHavingQuery() {
    String sql = "select w.* from web w having w.ip='192.168.6.1' "
        + "or w.ip='192.168.3.2' or w.ip='192.168.7.3'";
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
    String sql = "select w.* from web w where w.port=443 limit 20,10";
    testESDialect(sql);
  }

  public void testGroupLimit() {
    String sql = "select w.country, max(w.port), min(w.port) from web w where w.port=443 group by w.country limit 20, 10";
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

  public void testMatchConditionQuery() {
    String sql = "select w.* from web w where w.region is null and match(w.data, 'http')";
    testESDialect(sql);
    sql = "select w.* from web w where w.region is null and match('w.data, w.service', 'http')";
    testESDialect(sql);
  }

  public void testMatchPhraseConditionQuery() {
    String sql = "select w.* from web w where w.region is null and matchPhrase(w.data, 'http', 'myAnalyzer')";
    testESDialect(sql);
  }

  public void testMatchPhrasePrefixConditionQuery() {
    String sql = "select w.* from web w where w.region is null and matchPhrasePrefix(w.data, 'http')";
    testESDialect(sql);
  }

  public void testFuzzyConditionQuery() {
    String sql = "select w.* from web w where w.region is null and fuzzy(w.data, 'http')";
    testESDialect(sql);
  }

  public void testTypeConditionQuery() {
    String sql = "select w.* from web w where w.region is null and type('doc')";
    testESDialect(sql);
  }

  public void testIdsConditionQuery() {
    String sql = "select w.* from web w where w.region is null and ids('doc', '1,2,3')";
    testESDialect(sql);
  }

  public void testMoreLikeConditionQuery() {
    String sql = "select w.* from web w where w.region is null and moreLike('w.data,w.name', 'http', 1, 12)";
    testESDialect(sql);
  }

  public void testQuery() {
    String sql = "select es.* from virus_es es where es.country = 'CN' order by es.date_time desc";
    testESDialect(sql);
  }

  public void testQuery2() {
    String sql = "SELECT sim.EVENT_ID AS EVENT_ID, sim.NAME AS NAME, sim.CUSTOMER AS CUSTOMER  FROM SIM_EVENT sim WHERE 1=1 AND (END_TIME >= STR_TO_DATE( '2018-06-01 09:58:16', GET_FORMAT( DATETIME, 'ISO' ) ) AND END_TIME <= STR_TO_DATE( '2018-07-06 09:58:19', GET_FORMAT( DATETIME, 'ISO' ) ) ) ORDER BY EVENT_ID,NAME,CUSTOMER";
    testESDialect(sql);
  }

  public void testQuery3() {
    String sql = "select distinct l.name AS name from lyl l";
    testESDialect(sql);
  }

  public void testSearchAfter() {
    String sql = "select w.* from web w where w.port=443 limit 20,10";
    Map<String, Object> translationContext = new HashMap<String, Object>();
    Object[] features = new Object[] { 133, "test" };
    translationContext
        .put(EsTranslationContextConstants.RESULT_SORT_FEATURES, features);
    testESDialect(sql, translationContext);
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

  protected void testESDialect(String sql,
      Map<String, Object> translationContext) {
    try {
      String es = MoqlTranslator
          .translateMoql2Dialect(sql, SqlDialectType.ELASTICSEARCH,
              translationContext);
      es = es.trim();
      System.out.println(es);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
