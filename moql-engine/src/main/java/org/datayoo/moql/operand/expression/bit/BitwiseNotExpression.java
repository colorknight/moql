package org.datayoo.moql.operand.expression.bit;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.util.StringFormater;

public class BitwiseNotExpression extends AbstractOperationExpression {

  protected Object constantReturnValue;

  public BitwiseNotExpression(Operand operand) {
    super(OperatorType.UNARY, BitwiseOperator.BITWISENOT, null, operand);
    // TODO Auto-generated constructor stub
    initializeBitwise();
  }

  protected void initializeBitwise() {
    Long rNumber = null;
    if (rOperand.isConstantReturn()) {
      Object num = rOperand.operate(entityMap);
      rNumber = getNumber(rOperand, num);
    }
    if (rNumber != null) {
      constantReturn = true;
      constantReturnValue = ~rNumber;
    }
  }

  protected Long getNumber(Operand operand, Object obj) {
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

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (constantReturn)
      return constantReturnValue;
    Object num = rOperand.operate(entityMap);
    Long rNumber = getNumber(rOperand, num);
    if (rNumber == null)
      return null;
    return ~rNumber;
  }

  @Override
  public Object operate(Object[] entityArray) {
    if (constantReturn)
      return constantReturnValue;
    Object num = rOperand.operate(entityArray);
    Long rNumber = getNumber(rOperand, num);
    if (rNumber == null)
      return null;
    return ~rNumber;
  }
}
