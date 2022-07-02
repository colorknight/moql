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
package org.datayoo.moql.operand.function;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.MoqlRuntimeException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.env.MoqlEnv;
import org.datayoo.moql.operand.OperandFactory;
import org.datayoo.tripod.FieldMetadata;
import org.datayoo.tripod.IdfCounter;
import org.datayoo.tripod.IdfCounterImpl;
import org.datayoo.tripod.TermEntity;
import org.datayoo.tripod.engine.Tripod;
import org.datayoo.tripod.parser.TripodExpressionParser;

import java.util.*;

/**
 * @author Tang Tadin
 */
public class Lu extends AbstractFunction {

  public static final String FUNCTION_NAME = "lu";

  protected static FieldMetadata defaultFieldMetadata = new FieldMetadata("lu",
      1);
  protected Tripod tripod;

  protected Operand[] matchedFields;

  public Lu(List<Operand> parameters) {
    super(FUNCTION_NAME, -1, parameters);
    if (parameters.size() == 0 || parameters.size() > 3) {
      throw new IllegalArgumentException(
          "Invalid format! The format is 'lu(lucene [, idfCounterEnvName])'");
    }
    String lucene = parameters.get(0).operate((EntityMap) null).toString();
    IdfCounter idfCounter = null;
    if (parameters.size() == 2) {
      String idfCounterName = parameters.get(1).operate((EntityMap) null)
          .toString();
      idfCounter = (IdfCounter) MoqlEnv.getEnvProp(idfCounterName);
    }
    if (idfCounter == null)
      idfCounter = new IdfCounterImpl();
    Set<String> fieldSet = TripodExpressionParser.extractSegmentFields(lucene);
    fieldSet.add("lu");
    OperandFactory operandFactory = (OperandFactory) MoqlEnv.getEnvProp(
        MoqlEnv.ENV_OPERAND_FACTORY);
    matchedFields = new Operand[fieldSet.size()];
    int i = 0;
    for (String field : fieldSet) {
      try {
        matchedFields[i++] = operandFactory.createOperand(field);
      } catch (MoqlException e) {
        throw new MoqlRuntimeException(
            String.format("Create operand '%s' failed!", field), e);
      }
    }
    List<FieldMetadata> allFields = new LinkedList<>();
    allFields.add(defaultFieldMetadata);
    tripod = new Tripod("lu", allFields, defaultFieldMetadata, idfCounter,
        lucene);
  }

  /* (non-Javadoc)
   * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
   */
  @Override
  protected Object innerOperate(EntityMap entityMap) {
    Map<String, TermEntity[]> dataMap = new HashMap<>();
    for (Operand field : matchedFields) {
      Object v = field.operate(entityMap);
      if (v == null)
        continue;
      if (!(v instanceof TermEntity[])) {
        throw new IllegalArgumentException(
            String.format("The field %s's type is invalid!", field));
      }
      dataMap.put(field.toString(), (TermEntity[]) v);
    }
    return tripod.match(dataMap);
  }

  public void bind(String[] entityNames) {
    int i = 0;
    for (Operand field : matchedFields) {
      field.bind(entityNames);
    }
    this.binded = true;
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    Map<String, TermEntity[]> dataMap = new HashMap<>();
    for (Operand field : matchedFields) {
      Object v = field.operate(entityArray);
      if (v == null)
        continue;
      if (!(v instanceof TermEntity[])) {
        throw new IllegalArgumentException(
            String.format("The field %s's type is invalid!", field.toString()));
      }
      dataMap.put(field.toString(), (TermEntity[]) v);
    }
    return tripod.match(dataMap);
  }

}
