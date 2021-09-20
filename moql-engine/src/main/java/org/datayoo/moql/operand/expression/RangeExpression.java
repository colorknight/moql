package org.datayoo.moql.operand.expression;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.SelectorConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangtadin on 17/10/17.
 */
public class RangeExpression extends AbstractExpression {

  protected List<Operand> operands = new ArrayList<Operand>(2);

  protected boolean lClosure = false;

  protected boolean rClosure = false;

  public RangeExpression(Operand lRange, Operand rRange, boolean lClosure,
      boolean rClosure) {
    Validate.notNull(lRange, "lRange is null!");
    Validate.notNull(rRange, "rRange is null!");

    this.operands.add(lRange);
    this.operands.add(rRange);
    this.lClosure = lClosure;
    this.rClosure = rClosure;
    expressionType = ExpressionType.RANGE;
    name = buildNameString();
  }

  protected String buildNameString() {
    StringBuffer sbuf = new StringBuffer();
    if (lClosure)
      sbuf.append(SelectorConstants.LBRACKET);
    else
      sbuf.append(SelectorConstants.LBRACE);
    int i = 0;
    for (Operand operand : operands) {
      if (i++ != 0)
        sbuf.append(SelectorConstants.COMMA);
      sbuf.append(operand.toString());
    }
    if (rClosure)
      sbuf.append(SelectorConstants.RBRACKET);
    else
      sbuf.append(SelectorConstants.RBRACE);
    return sbuf.toString();
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return operands;
  }

  public List<Operand> getOperands() {
    return operands;
  }

  public boolean islClosure() {
    return lClosure;
  }

  public boolean isrClosure() {
    return rClosure;
  }

  @Override
  public void bind(String[] entityNames) {
    for (Operand operand : operands) {
      operand.bind(entityNames);
    }
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    return operands;
  }
}
