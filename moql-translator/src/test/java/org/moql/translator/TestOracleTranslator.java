package org.moql.translator;

import junit.framework.TestCase;
import org.apache.commons.lang.Validate;
import org.moql.MoqlException;
import org.moql.SelectorDefinition;
import org.moql.parser.MoqlParser;
import org.moql.sql.SqlDialectType;

public class TestOracleTranslator extends TestCase {

  protected SqlDialectType sqlDialectType = SqlDialectType.ORACLE;

  public void testUnaryWhere() {
    String sql = "select a.id, a.name, a.num % 500 from BeanA a where true";
    testSqlDialect(sql, true);
  }

  protected void testSqlDialect(String sql, boolean validate) {
    try {
      SelectorDefinition selectorDefiniton = MoqlParser.parseMoql(sql);
      String moql = MoqlTranslator
          .translateMetadata2Sql(selectorDefiniton, sqlDialectType);
      moql = moql.trim();
      System.out.println(sql);
      System.out.println(moql);
      if (validate)
        Validate.isTrue(sql.equals(moql));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testOneSingleTableSelector() {
    String sql = "select a.id, a.name, a.num % 500 from BeanA a where a.num % 500 > 10 order by 3 asc";
    testSqlDialect(sql, true);
  }

  public void testGroupOneTableSelector() {
    String sql = "select count(a.id) cnt, sum(a.num) sum, a.num % 500 mod from BeanA a group by 3 having mod > 10 order by 1 asc";
    testSqlDialect(sql, true);
  }

  public void testTwoSingleTableSelector() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num % 500 "
        + "from BeanA a, BeanB b where a.num % 500 > 10 or b.num % 500 < 400 order by 3 desc, 6 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoLeftJoinTableSelector1() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 and b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoLeftJoinTableSelector2() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 and b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoRightJoinTableSelector1() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 and b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoRightJoinTableSelector2() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num > 200 order by 3 desc";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 and b.num > 200 order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoFullJoinTableSelector() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a full join BeanB b on a.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testTwoInnerJoinTableSelector() {
    String sql = "select a.id, a.name, a.num % 500, b.id, b.name, b.num "
        + "from BeanA a inner join BeanB b on a.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeSingleTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a, BeanB b, BeanC c where a.num % 500 > 100 and b.num % 500 < 400 order by 3 desc, 6 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeLeftJoinTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a left join BeanB b on a.id = b.id left join BeanC c on c.id = b.id where a.num % 500 > 100 or b.num < 400 order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeRightJoinTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a right join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeFullJoinTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a full join BeanB b on a.id = b.id full join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeInnerJoinTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a inner join BeanB b on a.id = b.id inner join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testThreeLeftAndRightJoinTableSelector() {
    String sql =
        "select a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a left join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql, true);
  }

  public void testConditionSelector() {
    String sql = "select a.id, a.name, a.num, b.id, b.name, b.num "
        + "from BeanA a, BeanB b where a.id between 3 and 13 and a.name like 't%' order by 3 desc";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name, a.num, b.id, b.name, b.num "
        + "from BeanA a, BeanB b where a.name in ('te', 'ta', 'tc') or a.name is null";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name "
        + "from BeanA a where exists(select b.id from BeanB b)";
    testSqlDialect(sql, true);
    sql = "select a.id, a.name "
        + "from BeanA a where a.num < 100 and (num > 100 or a.num = 100)";
    testSqlDialect(sql, true);

  }

  public void testValueSelector() {
    String sql =
        "select (select b.id from BeanB b) id, a.name " + "from BeanA a";
    testSqlDialect(sql, true);
  }

  public void testNonRegFunctionSelector() {
    String sql = "select a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss')";
    testSqlDialect(sql, true);
  }

  public void testTopSelector() {
    String sql = "select a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss') limit 20";
    testSqlDialect(sql, false);
  }

  public void testDistinctSelector() {
    String sql = "select distinct a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss')";
    testSqlDialect(sql, true);
  }

  public void testTwoUnionSelector() {
    String sql = "select a.id id, a.name name, a.num num from BeanA a "
        + "union all select b.name name, b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql, true);
  }

  public void testTwoIntersectSelector() {
    String sql = "select a.id id, a.num num from BeanA a "
        + "intersect select b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql, true);
  }

  public void testTwoExceptSelector() {
    String sql = "select a.id id, a.num num from BeanA a "
        + "except select b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql, true);
  }

  public void testTwoSymExceptSelector() {
    String sql = "select a.id id, a.num num from BeanA a "
        + "symexcept select b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql, true);
  }

  public void testTwoComplementationSelector() {
    String sql = "select a.id id, a.num num from BeanA a "
        + "complementation select b.num num, b.id id from BeanB b order by id desc";
    testSqlDialect(sql, true);
  }
}
