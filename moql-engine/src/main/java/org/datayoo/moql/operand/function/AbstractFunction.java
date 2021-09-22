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

import org.datayoo.moql.*;
import org.datayoo.moql.operand.AbstractOperand;
import org.datayoo.moql.util.StringFormater;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tang Tadin
 */
public abstract class AbstractFunction extends AbstractOperand
    implements Function {

  protected int parameterCount = 0;

  protected List<Operand> parameters = new ArrayList<Operand>();

  protected FunctionType functionType = FunctionType.COMMON;

  protected String functionString;

  protected Object constantReturnValue = this;

  {
    operandType = OperandType.FUNCTION;
  }

  protected AbstractFunction(String name, int parameterCount,
      List<Operand> parameters) {
    if (parameterCount != VARIANT_PARAMETERS) {
      if (parameters == null) {
        if (parameterCount != 0) {
          throw new IllegalArgumentException(StringFormater
              .format("Function '{}' need {} parameters!", name,
                  parameterCount));
        }
      } else if (parameters.size() != parameterCount) {
        throw new IllegalArgumentException(StringFormater
            .format("Function '{}' need {} parameters!", name, parameterCount));
      }
    }
    if (parameters != null)
      this.parameters = parameters;
    this.name = name;
    this.parameterCount = parameterCount;
    initializeFunction();
  }

  protected AbstractFunction(String name, List<Operand> parameters) {
    if (parameters != null) {
      this.parameterCount = parameters.size();
      this.parameters = parameters;
    }
    this.name = name;
    initializeFunction();
  }

  protected void initializeFunction() {
    functionString = buildFunctionString();
    constantReturn = determineConstantsReturn(parameters);
  }

  protected String buildFunctionString() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(name);
    sbuf.append(SelectorConstants.LPAREN);
    int i = 0;
    for (Operand operand : parameters) {
      if (i != 0) {
        sbuf.append(SelectorConstants.COMMA);
      }
      sbuf.append(operand.toString());
      i++;
    }
    sbuf.append(SelectorConstants.RPAREN);
    return sbuf.toString();
  }

  protected boolean determineConstantsReturn(List<Operand> parameters) {
    if (parameters.size() == 0)
      return false;
    for (Operand operand : parameters) {
      if (!operand.isConstantReturn())
        return false;
    }
    return true;
  }

  /* (non-Javadoc)
   * @see org.moql.operand.Operand#operate(org.moql.data.EntityMap)
   */
  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (constantReturn) {
      if (constantReturnValue == this) {
        constantReturnValue = innerOperate(entityMap);
      }
      return constantReturnValue;
    }
    return innerOperate(entityMap);
  }

  protected abstract Object innerOperate(EntityMap entityMap);

  @Override
  public void bind(String[] entityNames) {
    for (Operand parameter : this.parameters) {
      parameter.bind(entityNames);
    }
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    if (constantReturn) {
      if (constantReturnValue == this) {
        constantReturnValue = innerOperate(entityArray);
      }
      return constantReturnValue;
    }
    return innerOperate(entityArray);
  }

  protected abstract Object innerOperate(Object[] entityArray);

  @Override
  public FunctionType getFunctionType() {
    // TODO Auto-generated method stub
    return functionType;
  }

  @Override
  public int getParameterCount() {
    // TODO Auto-generated method stub
    return parameterCount;
  }

  @Override
  public List<Operand> getParameters() {
    // TODO Auto-generated method stub
    return new ArrayList<Operand>(parameters);
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return functionString;
  }

  @Override
  public int hashCode() {
    return functionString.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Function) {
      return functionString.equals(obj.toString());
    }
    return false;
  }
}
