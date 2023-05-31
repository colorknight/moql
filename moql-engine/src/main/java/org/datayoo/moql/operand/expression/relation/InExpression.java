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
import org.datayoo.moql.operand.expression.OperandsExpression;
import org.datayoo.moql.operand.expression.ParenExpression;
import org.datayoo.moql.operand.expression.array.ArrayExpressionUtils;
import org.datayoo.moql.operand.selector.ColumnSelectorOperand;
import org.datayoo.moql.util.CompareHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class InExpression extends AbstractRelationExpression {

  protected List<Operand> rOperands;

  protected Set<Object> rValues;

  public InExpression(Operand lOperand, Operand rOperand) {
    super(OperatorType.BINARY, RelationOperator.IN, lOperand, rOperand);
    // TODO Auto-generated constructor stub
    /*
     * The rOperand will be parsed as a parenExpression when the expression list in
     * parenthese like "in ('a')" has only one expression.
     */
    if (rOperand instanceof ParenExpression) {
      rOperands = new ArrayList<Operand>();
      rOperands.add(((ParenExpression) rOperand).getOperand());
    } else if (rOperand instanceof OperandsExpression) {
      rOperands = ((OperandsExpression) rOperand).getOperands();
      if (rOperands.size() == 0) {
        throw new IllegalArgumentException(
            "Parameter 'rOperand' has no operand!");
      }
      // prepare the right operand when they are all constant
      rValues = new HashSet<Object>();
      for (Operand operand : rOperands) {
        if (operand.isConstantReturn()) {
          rValues.add(operand.getValue());
        } else {
          rValues = null;
          return;
        }
      }
    } else if (rOperand instanceof ColumnSelectorOperand) {
      rOperands = new ArrayList<Operand>();
      rOperands.add(rOperand);
    } else {
      throw new IllegalArgumentException(
          "Parameter 'rOperand' is not an OperandsExpression class!");
    }
  }

  @Override
  public boolean booleanOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object lValue = lOperand.operate(entityMap);
    if (lValue == null)
      return false;
    if (rValues != null) {
      for (Object v : rValues) {
        if (CompareHelper.compare(lValue, v) == 0)
          return true;
      }
      return rValues.contains(lValue);
    } else {
      for (Operand rOperand : rOperands) {
        int ret = 0;
        Object rValue = rOperand.operate(entityMap);
        if (rValue == null)
          continue;
        if (ArrayExpressionUtils.isArray(rValue)) {
          for (Object obj : ArrayExpressionUtils.toOperandContextList(rValue)) {
            ret = CompareHelper.compare(lValue, obj);
            if (ret == 0)
              return true;
          }
        } else {
          ret = CompareHelper.compare(lValue, rValue);
          if (ret == 0)
            return true;
        }
      }
    }
    return false;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return booleanOperate(entityMap);
  }

  public List<Operand> getrOperands() {
    return rOperands;
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object lValue = lOperand.operate(entityArray);
    if (lValue == null)
      return false;
    if (rValues != null) {
      return rValues.contains(lValue);
    } else {
      for (Operand rOperand : rOperands) {
        int ret = 0;
        Object rValue = rOperand.operate(entityArray);
        if (rValue == null)
          continue;
        if (ArrayExpressionUtils.isArray(rValue)) {
          for (Object obj : ArrayExpressionUtils.toOperandContextList(rValue)) {
            ret = CompareHelper.compare(lValue, obj);
            if (ret == 0)
              return true;
          }
        } else {
          ret = CompareHelper.compare(lValue, rValue);
          if (ret == 0)
            return true;
        }
      }
    }
    return false;
  }
}
