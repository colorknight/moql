package org.datayoo.moql.translator;

import junit.framework.TestCase;
import org.apache.commons.lang3.Validate;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.sql.SqlDialectType;

public class TestMoqlTranslator extends TestCase {

  protected SqlDialectType sqlDialectType = SqlDialectType.MOQL;

  public void testUnaryWhere() {
    String sql = "select cache(150,fifo) a.id, a.name, a.num % 500 from BeanA a where true";
    testSqlDialect(sql);
  }

  protected void testSqlDialect(String sql) {
    try {
      SelectorDefinition selectorDefiniton = MoqlParser.parseMoql(sql);
      String moql = MoqlTranslator
          .translateMetadata2Sql(selectorDefiniton, sqlDialectType);
      moql = moql.trim();
      System.out.println(sql);
      System.out.println(moql);
      Validate.isTrue(sql.equals(moql));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testOneSingleTableSelector() {
    String sql = "select cache(100,lru) a.id, a.name, a.num % 500 from BeanA a where a.num % 500 > 10 order by 3 asc";
    testSqlDialect(sql);
  }

  public void testGroupOneTableSelector() {
    String sql = "select cache(100,lfu) count(a.id) cnt, sum(a.num) sum, a.num % 500 mod from BeanA a group by 3 having mod > 10 order by 1 asc";
    testSqlDialect(sql);
  }

  public void testTwoSingleTableSelector() {
    String sql =
        "select cache(100,lfu) a.id, a.name, a.num % 500, b.id, b.name, b.num % 500 "
            + "from BeanA a, BeanB b where a.num % 500 > 10 or b.num % 500 < 400 order by 3 desc, 6 desc";
    testSqlDialect(sql);
  }

  public void testTwoLeftJoinTableSelector1() {
    String sql =
        "select cache(100,filo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql);
  }

  public void testTwoLeftJoinTableSelector2() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a left join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql);
  }

  public void testTwoRightJoinTableSelector1() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num < 400 order by 3 desc";
    testSqlDialect(sql);
  }

  public void testTwoRightJoinTableSelector2() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a right join BeanB b on a.id = b.id where a.num % 500 > 10 or b.num > 200 order by 3 desc";
    testSqlDialect(sql);
  }

  public void testTwoFullJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a full join BeanB b on a.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testTwoInnerJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num % 500, b.id, b.name, b.num "
            + "from BeanA a inner join BeanB b on a.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testThreeSingleTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a, BeanB b, BeanC c where a.num % 500 > 100 and b.num % 500 < 400 order by 3 desc, 6 desc";
    testSqlDialect(sql);
  }

  public void testThreeLeftJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a left join BeanB b on a.id = b.id left join BeanC c on c.id = b.id where a.num % 500 > 100 or b.num < 400 order by 3 desc";
    testSqlDialect(sql);
  }

  public void testThreeRightJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a right join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testThreeFullJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a full join BeanB b on a.id = b.id full join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testThreeInnerJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a inner join BeanB b on a.id = b.id inner join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testThreeLeftAndRightJoinTableSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num, c.id, c.name, c.num "
            + "from BeanA a left join BeanB b on a.id = b.id right join BeanC c on c.id = b.id order by 3 desc";
    testSqlDialect(sql);
  }

  public void testConditionSelector() {
    String sql =
        "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num "
            + "from BeanA a, BeanB b where a.id between 3 and 13 and a.name like 't%' order by 3 desc";
    testSqlDialect(sql);
    sql = "select cache(100,fifo) a.id, a.name, a.num, b.id, b.name, b.num "
        + "from BeanA a, BeanB b where a.name in ('te', 'ta', 'tc') or a.name is null";
    testSqlDialect(sql);
    sql = "select cache(100,fifo) a.id, a.name "
        + "from BeanA a where exists(select cache(100,fifo) b.id from BeanB b)";
    testSqlDialect(sql);
    sql = "select cache(100,fifo) a.id, a.name "
        + "from BeanA a where a.num < 100 and (num > 100 or a.num = 100)";
    testSqlDialect(sql);

  }

  public void testValueSelector() {
    String sql =
        "select cache(100,fifo) (select cache(100,fifo) b.id from BeanB b) id, a.name "
            + "from BeanA a";
    testSqlDialect(sql);
  }

  public void testNonRegFunctionSelector() {
    String sql = "select cache(100,fifo) a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss')";
    testSqlDialect(sql);
  }

  public void testLimitSelector() {
    String sql = "select cache(100,fifo) a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss') limit 20";
    testSqlDialect(sql);
  }

  public void testDistinctSelector() {
    String sql = "select cache(100,fifo) distinct a.name from BeanA a where a.time > to_date('2013-01-01 13:14:20','yyyy-MM-dd HH24:mm:ss')";
    testSqlDialect(sql);
  }

  public void testTwoUnionSelector() {
    String sql =
        "select cache(100,fifo) a.id id, a.name name, a.num num from BeanA a "
            + "union all select cache(100,fifo) b.name name, b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql);
  }

  public void testTwoIntersectSelector() {
    String sql = "select cache(100,fifo) a.id id, a.num num from BeanA a "
        + "intersect select cache(100,fifo) b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql);
  }

  public void testTwoExceptSelector() {
    String sql = "select cache(100,fifo) a.id id, a.num num from BeanA a "
        + "except select cache(100,fifo) b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql);
  }

  public void testTwoSymExceptSelector() {
    String sql = "select cache(100,fifo) a.id id, a.num num from BeanA a "
        + "symexcept select cache(100,fifo) b.id id, b.num num from BeanB b order by id desc";
    testSqlDialect(sql);
  }

  public void testTwoComplementationSelector() {
    String sql = "select cache(100,fifo) a.id id, a.num num from BeanA a "
        + "complementation select cache(100,fifo) b.num num, b.id id from BeanB b order by id desc";
    testSqlDialect(sql);
  }

}
