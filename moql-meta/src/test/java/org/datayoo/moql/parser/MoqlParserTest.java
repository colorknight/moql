package org.datayoo.moql.parser;

import junit.framework.TestCase;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.metadata.ConditionMetadata;

/**
 * Created by tangtadin on 17/10/16.
 */
public class MoqlParserTest extends TestCase {

  public void testCommonFilter() {
    try {
      String condition = "'this is a test'";
      ConditionMetadata conditionMetadata = MoqlParser
          .parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
      condition = "this and test";
      conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
      condition = "'192.168.6.1'";
      conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testInFilter() {
    try {
      String condition = "key in (1,2,3) or key in [1,3}";
      ConditionMetadata conditionMetadata = MoqlParser
          .parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testStringExpression() {
    try {
      String condition = "'abc*' and \"abc*\"";
      ConditionMetadata conditionMetadata = MoqlParser
          .parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
      condition = "'abc''*' and \"abc\\\"*\"";
      conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testSelector1() {
    try {
      String sql = "select a.a1, b, c from t";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.translateMetadata2Xml(selectorDefinition));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testSelector2() {
    try {
      String sql = "select a.a1, b, c from t.t1";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.translateMetadata2Xml(selectorDefinition));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testSelector3() {
    try {
      String sql = "select * from t";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.translateMetadata2Xml(selectorDefinition));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testSelector3_1() {
    try {
      String sql = "select * from t";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql, true);
      System.out.println(MoqlParser.translateMetadata2Xml(selectorDefinition));
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public void testSelector4() {
    try {
      String sql = "select t.*, t1.* from t t, t1 tt";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.translateMetadata2Xml(selectorDefinition));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testSelector5() {
    try {
      String sql = "select t.*, t1.* from t t, t1 tt";
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.getRelatedTables(selectorDefinition));
      sql = "select t.*, t1.* from t t left join t1a t1 on t1.a = t.a";
      selectorDefinition = MoqlParser.parseMoql(sql);
      System.out.println(MoqlParser.getRelatedTables(selectorDefinition));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }
}
