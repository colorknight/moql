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
package org.datayoo.moql.operand.cond;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.*;
import org.datayoo.moql.operand.AbstractOperand;
import org.datayoo.moql.operand.OperandContextArrayList;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class CaseOperand extends AbstractOperand {

  {
    operandType = OperandType.CASE;
  }

  protected List<WhenOperand> whenOperands;

  protected Operand elseOperand;

  public CaseOperand(List<WhenOperand> whenOperands, Operand elseOperand) {
    Validate.notEmpty(whenOperands, "Parameter 'whenOperands' is empty!");
    Validate.notNull(elseOperand, "Parameter 'elseOperand' is empty!");
    this.whenOperands = whenOperands;
    this.elseOperand = elseOperand;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    for (WhenOperand whenOperand : whenOperands) {
      if (whenOperand.isMatch(entityMap))
        return whenOperand.operate(entityMap);
    }
    return elseOperand.operate(entityMap);
  }

  @Override
  public void clear() {
    for (WhenOperand whenOperand : whenOperands)
      whenOperand.clear();
    elseOperand.clear();
  }

  public List<WhenOperand> getWhenOperands() {
    return whenOperands;
  }

  public Operand getElseOperand() {
    return elseOperand;
  }

  @Override
  public Object operate(Object[] entityArray) {
    for (WhenOperand whenOperand : whenOperands) {
      if (whenOperand.isMatch(entityArray))
        return whenOperand.operate(entityArray);
    }
    return elseOperand.operate(entityArray);
  }

  @Override
  public void bind(String[] entityNames) {
    for (WhenOperand whenOperand : whenOperands) {
      whenOperand.bind(entityNames);
    }
    elseOperand.bind(entityNames);
  }
}
