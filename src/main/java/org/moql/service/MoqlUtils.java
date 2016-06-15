/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moql.service;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.commons.lang.Validate;
import org.moql.*;
import org.moql.antlr.FilterLexer;
import org.moql.antlr.FilterParser;
import org.moql.antlr.SelectorLexer;
import org.moql.antlr.SelectorParser;
import org.moql.core.MoqlFactory;
import org.moql.core.factory.MoqlFactoryImpl;
import org.moql.metadata.ConditionMetadata;
import org.moql.metadata.xml.FilterFormater;
import org.moql.metadata.xml.SelectorFormater;
import org.moql.operand.factory.OperandFactoryImpl;
import org.moql.sql.SqlDialectType;
import org.moql.sql.SqlTranslatorHelper;
import org.moql.util.StringFormater;
import org.moql.xml.DefaultDocumentFormater;
import org.moql.xml.XmlAccessException;

import java.io.ByteArrayInputStream;

/**
 * 
 * @author Tang Tadin
 * 
 */
public abstract class MoqlUtils {

  protected static MoqlFactory moqlFactory = MoqlFactoryImpl
      .createSelectorFactory();

  /**
   * 
   * @param moql
   * @return SelectorMetadata or SetlectorMetadata
   * @throws MoqlException
   */
  public static SelectorDefinition parseMoql(String moql) throws MoqlException {
    Validate.notEmpty(moql, "Parameter 'moql' is empty!");
    try {
      ANTLRInputStream is = new ANTLRInputStream(new ByteArrayInputStream(
          moql.getBytes()));
      SelectorLexer lexer = new SelectorLexer(is);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      SelectorParser parser = new SelectorParser(tokens);
      return parser.selector();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater.format("Parse moql '{}' failed!",
          moql), e);
    }
  }

  public static Selector createSelector(SelectorDefinition selectorDefinition)
      throws MoqlException {
    Validate.notNull(selectorDefinition,
        "Parameter 'selectorDefinition' is null!");
    try {
      return moqlFactory.createSelector(selectorDefinition);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException("Create selector failed!", e);
    }
  }

  public static Selector createSelector(String moql) throws MoqlException {
    SelectorDefinition selectorDefinition = parseMoql(moql);
    try {
      return moqlFactory.createSelector(selectorDefinition);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater.format(
          "Create selector by moql '{}' failed!", moql), e);
    }
  }

  public static String translateMoql2Xml(String moql) throws MoqlException {
    SelectorDefinition definition = parseMoql(moql);
    return translateMetadata2Xml(definition);
  }

  public static String translateMetadata2Xml(SelectorDefinition definition)
      throws XmlAccessException {
    Validate.notNull(definition, "Parameter 'definition' is null!");
    DefaultDocumentFormater<SelectorDefinition> documentFormater = new DefaultDocumentFormater<SelectorDefinition>();
    SelectorFormater selectorFormater = new SelectorFormater();
    documentFormater.setFormater(selectorFormater);
    return documentFormater.exportObjectToString(definition);
  }

  public static String translateMetadata2Sql(SelectorDefinition definition,
      SqlDialectType dialectType) throws MoqlException {
    Selector selector = createSelector(definition);
    return SqlTranslatorHelper.translate2Sql(selector, dialectType);
  }

  public static String translateMoql2Dialect(String sql,
      SqlDialectType dialectType) throws MoqlException {
    SelectorDefinition definition = parseMoql(sql);
    Selector selector = createSelector(definition);
    return SqlTranslatorHelper.translate2Sql(selector, dialectType);
  }

  public static ConditionMetadata parseCondition(String condition)
      throws MoqlException {
    Validate.notEmpty(condition, "Parameter 'condition' is empty!");
    try {
      ANTLRInputStream is = new ANTLRInputStream(new ByteArrayInputStream(
          condition.getBytes()));
      FilterLexer lexer = new FilterLexer(is);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      FilterParser parser = new FilterParser(tokens);
      return new ConditionMetadata(parser.searchCondition());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater.format(
          "Parse condition '{}' failed!", condition), e);
    }
  }

  public static Filter createFilter(String condition) throws MoqlException {
    ConditionMetadata conditionMetadata = parseCondition(condition);
    try {
      return moqlFactory.createFilter(conditionMetadata);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater.format(
          "Create filter by condition '{}' failed!", condition), e);
    }
  }

  public static Operand createOperand(String operand) throws MoqlException {
    Validate.notEmpty(operand, "Parameter 'operand' is empty!");
    try {
      return OperandFactoryImpl.createOperandFactory().createOperand(operand);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater.format(
          "Create operand '{}' failed!", operand), e);
    }
  }

  public static String translateCondition2Xml(String condition)
      throws MoqlException {
    ConditionMetadata conditioneMetadata = parseCondition(condition);
    return translateMetadata2Xml(conditioneMetadata);
  }

  public static String translateMetadata2Xml(ConditionMetadata conditionMetadata)
      throws XmlAccessException {
    Validate.notNull(conditionMetadata, "conditionMetadata is null!");
    DefaultDocumentFormater<ConditionMetadata> documentFormater = new DefaultDocumentFormater<ConditionMetadata>();
    FilterFormater filterFormater = new FilterFormater();
    documentFormater.setFormater(filterFormater);
    return documentFormater.exportObjectToString(conditionMetadata);
  }

  public static String translateMetadata2Condition(
      ConditionMetadata conditionMetadata, SqlDialectType dialectType)
      throws MoqlException {
    Filter filter = moqlFactory.createFilter(conditionMetadata);
    return SqlTranslatorHelper.translate2Condition(filter, dialectType);
  }

  public static String translateXml2Sql(String xmlData, SqlDialectType type)
      throws MoqlException {
    SelectorDefinition selectorDefinition = translateXml2SelectorDefinition(xmlData);
    return translateMetadata2Sql(selectorDefinition, type);
  }

  public static String translateXml2Condition(String xmlData,
      SqlDialectType type) throws MoqlException {
    ConditionMetadata conditionMetadata = translateXml2ConditionMetadata(xmlData);
    return translateMetadata2Condition(conditionMetadata, type);
  }

  public static SelectorDefinition translateXml2SelectorDefinition(
      String xmlData) throws MoqlException {
    Validate.notEmpty(xmlData, "xmlData is empty!");
    DefaultDocumentFormater<SelectorDefinition> documentFormater = new DefaultDocumentFormater<SelectorDefinition>();
    SelectorFormater selectorFormater = new SelectorFormater();
    documentFormater.setFormater(selectorFormater);
    return documentFormater.importObjectFromString(xmlData);
  }

  public static ConditionMetadata translateXml2ConditionMetadata(String xmlData)
      throws MoqlException {
    Validate.notEmpty(xmlData, "xmlData is empty!");
    DefaultDocumentFormater<ConditionMetadata> documentFormater = new DefaultDocumentFormater<ConditionMetadata>();
    FilterFormater filterFormater = new FilterFormater();
    documentFormater.setFormater(filterFormater);
    return documentFormater.importObjectFromString(xmlData);
  }

  public static String registFunction(String name, String className) {
    return OperandFactoryImpl.registFunction(name, className);
  }

  public static String unregistFunction(String name) {
    return OperandFactoryImpl.unregistFunction(name);
  }
}
