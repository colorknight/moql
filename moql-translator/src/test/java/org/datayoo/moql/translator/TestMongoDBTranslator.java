package org.datayoo.moql.translator;

import junit.framework.TestCase;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.sql.SqlDialectType;

public class TestMongoDBTranslator extends TestCase {

  public void testSimpleQuery() {
    String sql = "select w.item, w.name from web w";
    testMongoDialect(sql);
  }

  public void testConditionQuery() {
    String sql = "select u.name, u.age from users u where name='joe' and age=27";
    testMongoDialect(sql);
  }

  public void testConditionQuery2() {
    String sql =
        "select w.dns, w.ip from web w where (w.port=443 or w.port=8080) and "
            + "w.ip='127.0.0.1' or w.ip='127.0.0.2'";
    testMongoDialect(sql);
  }

  public void testGroupOrder() {
    String sql = "select ip.src, ip.proto, max(ip.sport) sport, min(ip.sport) from ip3 ip group by ip.src, ip.proto order by ip.src desc limit 2 ";
    testMongoDialect(sql);
  }

  public void testHaving() {
    String sql = "select ip.src, ip.proto, max(ip.sport) sport, min(ip.sport) from ip3 ip where ip.ip is not null group by ip.src, ip.proto having ip.proto < 1000";
    testMongoDialect(sql);
  }

  public void testNEConditionQuery() {
    String sql = "select w.dns, w.ip  from web w where w.port<>443";
    testMongoDialect(sql);
  }

  public void testDistinctQuery() {
    // unsupport distinct
    //    String sql = "select distinct w.port from web w where w.port<1000";
    //    testMongoDialect(sql);
  }

  public void testNotEqualQuery() {
    String sql = "select w.dns, w.ip from web w where not w.port=443 or w.port = 88";
    testMongoDialect(sql);
  }
  // nin
  public void testNotInQuery() {
    String sql = "select w.dns, w.ip from web w where not w.country in ('美国','澳大利亚')";
    testMongoDialect(sql);
  }
  // nor
  public void testNotAndQuery() {
    String sql = "select w.dns, w.ip from web w where not (w.port=443 and w.country in ('美国','澳大利亚'))";
    testMongoDialect(sql);
  }

  public void testAndOrConditionQuery() {
    String sql = "select w.dns, w.ip from web w where w.port=443 and w.country in ('美国','澳大利亚') or w.port = 88 and w.ip_num between 10000 and 1900000000";
    testMongoDialect(sql);
  }

  public void testLikeConditionQuery() {
    String sql = "select w.dns, w.ip from web w where w.country like '美%'";
    testMongoDialect(sql);
  }

  public void testIsConditionQuery() {
    String sql = "select w.dns, w.ip from web w where w.region is null";
    testMongoDialect(sql);
    sql = "select w.dns, w.ip  from web w where w.region is not null";
    testMongoDialect(sql);
  }

  public void testParenConditionQuery() {
    String sql = "select w.dns, w.ip  from web w where w.region is null and (w.port = 88 or w.port = 443)";
    testMongoDialect(sql);
  }

  public void testLimitQuery() {
    String sql = "select w.dns, w.ip  from web w where w.port<>443 limit 10,20";
    testMongoDialect(sql);
  }

  public void testLeftjoinQuery() {
    String sql = "select w.dns, w.ip from web w left join jmr j on j.dns = w.dns where w.port<>443 limit 10,20";
    testMongoDialect(sql);
  }

  public void testDateQuery() {
    String sql = "select w.dns, w.ip from web w where w.createTime > ISODate('2019-09-18T09:54:00.000Z')";
    testMongoDialect(sql);
  }

  public void testTextQuery() {
    String sql = "select w.dns, w.ip from web w where text('\"coffee shop\"')";
    testMongoDialect(sql);
  }

  protected void testMongoDialect(String sql) {
    try {
      String mongodb = MoqlTranslator
          .translateMoql2Dialect(sql, SqlDialectType.MONGODB);
      mongodb = mongodb.trim();
      System.out.println(mongodb);
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

}
