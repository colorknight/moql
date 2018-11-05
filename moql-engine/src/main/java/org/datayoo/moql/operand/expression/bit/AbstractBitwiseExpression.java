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
package org.datayoo.moql.operand.expression.bit;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.constant.ConstantType;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.expression.arithmetic.ArithmeticOperator;
import org.datayoo.moql.util.StringFormater;

import java.io.ByteArrayInputStream;

/**
 * @author Tang Tadin
 */
public abstract class AbstractBitwiseExpression
    extends AbstractOperationExpression {

  protected Object constantReturnValue;

  {
    expressionType = ExpressionType.BITWISE;
  }

  public AbstractBitwiseExpression(BitwiseOperator operator, Operand lOperand,
      Operand rOperand) {
    super(OperatorType.BINARY, operator, lOperand, rOperand);
    initializeBitwise();
  }

  protected void initializeBitwise() {
    Long lNumber = null;
    Long rNumber = null;
    if (lOperand.isConstantReturn()) {
      lNumber = getNumber(lOperand, null);
    }
    if (rOperand.isConstantReturn()) {
      rNumber = getNumber(rOperand, null);
    }
    if (lNumber != null && rNumber != null) {
      constantReturn = true;
      constantReturnValue = calc(lNumber, rNumber);
    }
  }

  protected Long getNumber(Operand operand, EntityMap entityMap) {
    Object obj = operand.operate(entityMap);
    if (obj == null)
      return null;
    if (obj instanceof Long || obj instanceof Integer || obj instanceof Short
        || obj instanceof Byte)
      return ((Number) obj).longValue();
    if (obj instanceof NumberConvertable) {
      Number num = ((NumberConvertable) obj).toNumber();
      if (num != null) {
        if (num instanceof Long || num instanceof Integer
            || num instanceof Short || num instanceof Byte)
          return num.longValue();
      }
    }
    throw new IllegalArgumentException(StringFormater
        .format("Operand '{}' is not a number!", operand.toString()));
  }

  @Override public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (constantReturn)
      return constantReturnValue;
    Long lNumber = getNumber(lOperand, entityMap);
    Long rNumber = getNumber(rOperand, entityMap);
    if (lNumber == null || rNumber == null)
      return null;
    return calc(lNumber, rNumber);
  }

  protected abstract Long calc(Long lNumber, Long rNumber);

}
