package org.datayoo.moql.operand.nativeFunc;

import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.AbstractFunction;

import java.util.List;

public abstract class AbstractNativeFunction extends AbstractFunction {

  protected Object target;

  protected AbstractNativeFunction(String name, int parameterCount,
      List<Operand> parameters) {
    super(name, parameterCount, parameters);
  }

  protected AbstractNativeFunction(String name, List<Operand> parameters) {
    super(name, parameters);
  }

  public Object getTarget() {
    return target;
  }

  public void setTarget(Object target) {
    this.target = target;
  }
}
