package org.moql.translator;

import junit.framework.TestCase;
import org.moql.MoqlException;
import org.moql.SelectorDefinition;
import org.moql.parser.MoqlParser;
import org.moql.sql.SqlDialectType;

public class TestSqlTranslation extends TestCase {

  protected SqlDialectType sqlDialectType = SqlDialectType.MYSQL;

  public void testInNestedSql() {
    String sql =
        "select a.xx_ST_WBXXB_USER_ID, a.xx_ST_WBXXB_ROOTUSER_ID from xx_ST_WBXXB a "
            + "where a.xx_ST_WBXXB_REL_TIME>= unix_timestamp('2015-02-28 00:00:00') "
            + "and a.xx_ST_WBXXB_REL_TIME <= unix_timestamp('2015-02-28 10:00:00') "
            + "and a.xx_ST_WBXXB_SP_TYPE=1 and xx_ST_WBXXB_ROOT_ID in ("
            + "select b.xx_ST_WBXXB_MID from xx_ST_WBXXB b "
            + "where b.xx_ST_WBXXB_REL_TIME >= unix_timestamp('2015-02-28 00:00:00') "
            + "and b.xx_ST_WBXXB_REL_TIME <= unix_timestamp('2015-02-28 10:00:00') "
            + "and b.xx_ST_WBXXB_MESSAGE_TYPE=1 and b.xx_ST_WBXXB_SP_TYPE=1 "
            + "and (b.xx_ST_WBXXB_M_TEXT like '%柴静%'  or b.xx_ST_WBXXB_M_TEXT like '%穹顶之下%')"
            + ") limit 1000";
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
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
