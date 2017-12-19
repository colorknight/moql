package org.moql.translator;

import junit.framework.TestCase;
import org.moql.MoqlException;
import org.moql.parser.MoqlParser;
import org.moql.sql.SqlDialectType;

public class TestTranslator extends TestCase {

  public void test2Xml() {
    String sql = "select count(a.c.id) cnt, sum(a.num) sum, a.num%50 mod from BeanA a where a.c.id=1 or a.c.id=2 and a.name='23' and a.time between 3 and 5 or cast(a.alias as String) = '45' group by 3 having mod > 10 order by 1 limit 10,3";
    try {
      String xml = MoqlParser.translateMoql2Xml(sql);
      System.out.println(xml);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MOQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.ORACLE);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.SQLSERVER);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MYSQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.POSTGRESQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.DB2);
      System.out.println(sql);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void test2Xml2() {
    String sql = "select a.c.id, a.num, b.* from BeanA a, BeanB b where a.c.id=1 or a.c.id=2 and a.name='23' and a.time between 3 and 5 or cast(a.alias as String) = '45' order by 1 limit 3";
    try {
      String xml = MoqlParser.translateMoql2Xml(sql);
      System.out.println(xml);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MOQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.ORACLE);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.SQLSERVER);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.MYSQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.POSTGRESQL);
      System.out.println(sql);
      sql = MoqlTranslator.translateXml2Sql(xml, SqlDialectType.DB2);
      System.out.println(sql);
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
