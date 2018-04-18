package org.moql.translator;

import org.moql.Filter;
import org.moql.MoqlException;
import org.moql.Selector;
import org.moql.SelectorDefinition;
import org.moql.engine.MoqlEngine;
import org.moql.metadata.ConditionMetadata;
import org.moql.parser.MoqlParser;
import org.moql.sql.SqlDialectType;

/**
 * Created by tangtadin on 17/10/16.
 */
public abstract class MoqlTranslator {

  public static String translateMetadata2Sql(SelectorDefinition definition,
      SqlDialectType dialectType) throws MoqlException {
    Selector selector = MoqlEngine.createSelector(definition);
    return SqlTranslatorHelper.translate2Sql(selector, dialectType);
  }

  public static String translateMoql2Dialect(String sql,
      SqlDialectType dialectType) throws MoqlException {
    SelectorDefinition definition = MoqlParser.parseMoql(sql);
    Selector selector = MoqlEngine.createSelector(definition);
    return SqlTranslatorHelper.translate2Sql(selector, dialectType);
  }

  public static String translateMetadata2Condition(
      ConditionMetadata conditionMetadata, SqlDialectType dialectType)
      throws MoqlException {
    Filter filter = MoqlEngine.createFilter(conditionMetadata);
    return SqlTranslatorHelper.translate2Condition(filter, dialectType);
  }

  public static String translateXml2Sql(String xmlData, SqlDialectType type)
      throws MoqlException {
    SelectorDefinition selectorDefinition = MoqlParser
        .translateXml2SelectorDefinition(xmlData);
    return translateMetadata2Sql(selectorDefinition, type);
  }

  public static String translateXml2Condition(String xmlData,
      SqlDialectType type) throws MoqlException {
    ConditionMetadata conditionMetadata = MoqlParser
        .translateXml2ConditionMetadata(xmlData);
    return translateMetadata2Condition(conditionMetadata, type);
  }
}
