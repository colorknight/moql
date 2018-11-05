package org.datayoo.moql.operand.expression.bit;

import org.datayoo.moql.Operand;

public class RightShiftExpression extends AbstractBitwiseExpression {

  public RightShiftExpression(Operand lOperand,
      Operand rOperand) {
    super(BitwiseOperator.RSHIFT, lOperand, rOperand);
  }

  @Override protected Long calc(Long lNumber, Long rNumber) {
    return lNumber >> rNumber;
  }
}
