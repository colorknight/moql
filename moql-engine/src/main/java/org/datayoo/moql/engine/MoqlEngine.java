/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.engine;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.*;
import org.datayoo.moql.core.MoqlFactory;
import org.datayoo.moql.core.factory.MoqlFactoryImpl;
import org.datayoo.moql.metadata.ConditionMetadata;
import org.datayoo.moql.operand.factory.OperandFactoryImpl;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.util.StringFormater;

/**
 *
 * @author Tang Tadin
 *
 */
public abstract class MoqlEngine {

  protected static MoqlFactory moqlFactory = MoqlFactoryImpl
      .createSelectorFactory();

  public static Selector createSelector(SelectorDefinition selectorDefinition)
      throws MoqlException {
    Validate
        .notNull(selectorDefinition, "Parameter 'selectorDefinition' is null!");
    try {
      return moqlFactory.createSelector(selectorDefinition);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException("Create selector failed!", e);
    }
  }

  public static Selector createSelector(String moql) throws MoqlException {
    SelectorDefinition selectorDefinition = MoqlParser.parseMoql(moql);
    try {
      return moqlFactory.createSelector(selectorDefinition);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(
          StringFormater.format("Create selector by moql '{}' failed!", moql),
          e);
    }
  }

  public static Filter createFilter(String condition) throws MoqlException {
    ConditionMetadata conditionMetadata = MoqlParser.parseCondition(condition);
    try {
      return moqlFactory.createFilter(conditionMetadata);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(StringFormater
          .format("Create filter by condition '{}' failed!", condition), e);
    }
  }

  public static Filter createFilter(ConditionMetadata conditionMetadata)
      throws MoqlException {
    try {
      return moqlFactory.createFilter(conditionMetadata);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(e.getMessage(), e);
    }
  }

  public static Operand createOperand(String operand) throws MoqlException {
    Validate.notEmpty(operand, "Parameter 'operand' is empty!");
    try {
      return OperandFactoryImpl.createOperandFactory().createOperand(operand);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(
          StringFormater.format("Create operand '{}' failed!", operand), e);
    }
  }

  public static String registFunction(String name, String className) {
    return OperandFactoryImpl.registFunction(name, className);
  }

  public static String unregistFunction(String name) {
    return OperandFactoryImpl.unregistFunction(name);
  }
}
