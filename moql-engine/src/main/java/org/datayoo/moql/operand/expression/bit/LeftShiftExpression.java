package org.datayoo.moql.operand.expression.bit;

import org.datayoo.moql.Operand;

public class LeftShiftExpression extends AbstractBitwiseExpression {

  public LeftShiftExpression(Operand lOperand,
      Operand rOperand) {
    super(BitwiseOperator.LSHIFT, lOperand, rOperand);
  }

  @Override protected Long calc(Long lNumber, Long rNumber) {
    return lNumber << rNumber;
  }
}
