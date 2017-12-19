package org.moql.parser;

import junit.framework.TestCase;
import org.moql.MoqlException;
import org.moql.metadata.ConditionMetadata;

/**
 * Created by tangtadin on 17/10/16.
 */
public class MoqlParserTest extends TestCase {

  public void testCommonFilter() {
    try {
      String condition = "'this is a test'";
      ConditionMetadata conditionMetadata = MoqlParser.parseCondition(condition);
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
      ConditionMetadata conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testStringExpression() {
    try {
      String condition = "'abc*' and \"abc*\"";
      ConditionMetadata conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
      condition = "'abc''*' and \"abc\\\"*\"";
      conditionMetadata = MoqlParser.parseCondition(condition);
      System.out.println(MoqlParser.translateMetadata2Xml(conditionMetadata));
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

}
