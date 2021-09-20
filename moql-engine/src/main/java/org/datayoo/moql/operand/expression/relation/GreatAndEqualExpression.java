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
package org.datayoo.moql.operand.expression.relation;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.util.CompareHelper;

/**
 *
 * @author Tang Tadin
 *
 */
public class GreatAndEqualExpression extends AbstractRelationExpression {

  public GreatAndEqualExpression(Operand lOperand, Operand rOperand) {
    super(OperatorType.BINARY, RelationOperator.GE, lOperand, rOperand);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean booleanOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object lValue = lOperand.operate(entityMap);
    Object rValue = rOperand.operate(entityMap);
    if (lValue == null || rValue == null)
      return false;
    int ret = CompareHelper.compare(lValue, rValue);
    return ret >= 0 ? true : false;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return booleanOperate(entityMap);
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object lValue = lOperand.operate(entityArray);
    Object rValue = rOperand.operate(entityArray);
    if (lValue == null || rValue == null)
      return false;
    int ret = CompareHelper.compare(lValue, rValue);
    return ret >= 0 ? true : false;
  }
}
