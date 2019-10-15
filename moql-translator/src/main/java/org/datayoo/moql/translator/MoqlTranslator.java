package org.datayoo.moql.translator;

import org.datayoo.moql.Filter;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Selector;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.metadata.ConditionMetadata;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.sql.SqlDialectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangtadin on 17/10/16.
 */
public abstract class MoqlTranslator {

  public static String translateMetadata2Sql(SelectorDefinition definition,
      SqlDialectType dialectType) throws MoqlException {
    return translateMetadata2Sql(definition, dialectType,
        new HashMap<String, Object>());
  }

  public static String translateMoql2Dialect(String sql,
      SqlDialectType dialectType) throws MoqlException {
    return translateMoql2Dialect(sql, dialectType,
        new HashMap<String, Object>());
  }

  public static String translateMetadata2Condition(
      ConditionMetadata conditionMetadata, SqlDialectType dialectType)
      throws MoqlException {
    return translateMetadata2Condition(conditionMetadata, dialectType,
        new HashMap<String, Object>());
  }

  public static String translateXml2Sql(String xmlData, SqlDialectType type)
      throws MoqlException {
    return translateXml2Sql(xmlData, type, new HashMap<String, Object>());
  }

  public static String translateXml2Condition(String xmlData,
      SqlDialectType type) throws MoqlException {
    return translateXml2Condition(xmlData, type, new HashMap<String, Object>());
  }

  public static String translateMetadata2Sql(SelectorDefinition definition,
      SqlDialectType dialectType, Map<String, Object> translationContext)
      throws MoqlException {
    Selector selector = MoqlEngine.createSelector(definition);
    return SqlTranslatorHelper
        .translate2Sql(selector, dialectType, translationContext);
  }

  public static String translateMoql2Dialect(String sql,
      SqlDialectType dialectType, Map<String, Object> translationContext)
      throws MoqlException {
    SelectorDefinition definition = MoqlParser.parseMoql(sql);
    Selector selector = MoqlEngine.createSelector(definition);
    return SqlTranslatorHelper
        .translate2Sql(selector, dialectType, translationContext);
  }

  public static String translateMetadata2Condition(
      ConditionMetadata conditionMetadata, SqlDialectType dialectType,
      Map<String, Object> translationContext) throws MoqlException {
    Filter filter = MoqlEngine.createFilter(conditionMetadata);
    return SqlTranslatorHelper
        .translate2Condition(filter, dialectType, translationContext);
  }

  public static String translateXml2Sql(String xmlData, SqlDialectType type,
      Map<String, Object> translationContext) throws MoqlException {
    SelectorDefinition selectorDefinition = MoqlParser
        .translateXml2SelectorDefinition(xmlData);
    return translateMetadata2Sql(selectorDefinition, type, translationContext);
  }

  public static String translateXml2Condition(String xmlData,
      SqlDialectType type, Map<String, Object> translationContext)
      throws MoqlException {
    ConditionMetadata conditionMetadata = MoqlParser
        .translateXml2ConditionMetadata(xmlData);
    return translateMetadata2Condition(conditionMetadata, type,
        translationContext);
  }
}
