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
package org.datayoo.moql.operand.expression.arithmetic;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.constant.ConstantType;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.util.StringFormater;

import java.math.BigDecimal;

/**
 * @author Tang Tadin
 */
public abstract class AbstractArithmeticExpression
    extends AbstractOperationExpression {

  protected ConstantType lOperandType;

  protected ConstantType rOperandType;

  protected Object constantReturnValue;

  {
    expressionType = ExpressionType.ARITHMETIC;
  }

  public AbstractArithmeticExpression(ArithmeticOperator operator,
      Operand lOperand, Operand rOperand) {
    super(OperatorType.BINARY, operator, lOperand, rOperand);
    initializeArithmetic();
  }

  protected void initializeArithmetic() {
    Number lNumber = null;
    Number rNumber = null;
    if (lOperand.isConstantReturn()) {
      lNumber = getNumber(lOperand, lOperand.operate((EntityMap) null));
      lOperandType = getConstantType(lNumber);
    }
    if (rOperand.isConstantReturn()) {
      rNumber = getNumber(rOperand, rOperand.operate((EntityMap) null));
      rOperandType = getConstantType(rNumber);
    }
    if (lOperandType != null && rOperandType != null) {
      constantReturn = true;
      constantReturnValue = calc(lNumber, rNumber,
          detemineReturnType(lOperandType, rOperandType));
    }
  }

  protected Number getNumber(Operand operand, Object obj) {
    if (obj == null)
      return null;
    if (obj instanceof Number)
      return (Number) obj;
    if (obj instanceof NumberConvertable) {
      return ((NumberConvertable) obj).toNumber();
    }
    throw new IllegalArgumentException(
        StringFormater.format("Operand '{}' is not a number!",
            operand.toString()));
  }

  protected ConstantType getConstantType(Number number) {
    if (number instanceof Float || number instanceof Double)
      return ConstantType.DOUBLE;

    return ConstantType.LONG;
  }

  protected ConstantType detemineReturnType(ConstantType lOperandType,
      ConstantType rOperandType) {
    if (lOperandType == ConstantType.DOUBLE
        || rOperandType == ConstantType.DOUBLE)
      return ConstantType.DOUBLE;
    return ConstantType.LONG;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (constantReturn)
      return constantReturnValue;
    Object lNum = lOperand.operate(entityMap);
    Object rNum = rOperand.operate(entityMap);
    Number lNumber = getNumber(lOperand, lNum);
    Number rNumber = getNumber(rOperand, rNum);
    if (lNumber == null || rNumber == null)
      return null;
    ConstantType lOperandType = getConstantType(lNumber, this.lOperandType);
    ConstantType rOperandType = getConstantType(rNumber, this.rOperandType);
    return calc(lNumber, rNumber,
        detemineReturnType(lOperandType, rOperandType));
  }

  protected ConstantType getConstantType(Number number,
      ConstantType initializedType) {
    if (initializedType != null)
      return initializedType;
    return getConstantType(number);
  }

  protected abstract Object calc(Number lNumber, Number rNumber,
      ConstantType returnType);

  protected Object convertReturnValue(Number ret, ConstantType returnType) {
    if (returnType == ConstantType.DOUBLE)
      return ret.doubleValue();
    else
      return ret.longValue();
  }

  @Override
  public Object operate(Object[] entityArray) {
    if (constantReturn)
      return constantReturnValue;
    Object lNum = lOperand.operate(entityArray);
    Object rNum = rOperand.operate(entityArray);
    Number lNumber = getNumber(lOperand, lNum);
    Number rNumber = getNumber(rOperand, rNum);
    if (lNumber == null || rNumber == null)
      return null;
    ConstantType lOperandType = getConstantType(lNumber, this.lOperandType);
    ConstantType rOperandType = getConstantType(rNumber, this.rOperandType);
    return calc(lNumber, rNumber,
        detemineReturnType(lOperandType, rOperandType));
  }
}
