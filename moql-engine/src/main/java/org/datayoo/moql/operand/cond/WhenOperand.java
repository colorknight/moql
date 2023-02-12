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
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.OperandType;
import org.datayoo.moql.core.Condition;
import org.datayoo.moql.operand.AbstractOperand;

/**
 * @author Tang Tadin
 */
public class WhenOperand extends AbstractOperand {

  {
    operandType = OperandType.WHEN;
  }

  protected Operand condition;

  protected Operand operand;

  public WhenOperand(Operand condition, Operand operand) {
    Validate.notNull(condition, "Parameter 'condition' is null!");
    Validate.notNull(operand, "Parameter 'operand' is null!");
    this.condition = condition;
    this.operand = operand;
  }

  public boolean isMatch(EntityMap entityMap) {
    return condition.booleanOperate(entityMap);
  }

  public boolean isMatch(Object[] entityArray) {
    return condition.booleanOperate(entityArray);
  }

  @Override
  public Object operate(EntityMap entityMap) {
    return operand.operate(entityMap);
  }

  @Override
  public void clear() {
    operand.clear();
  }

  public Operand getCondition() {
    return condition;
  }

  public Operand getOperand() {
    return operand;
  }

  @Override
  public Object operate(Object[] entityArray) {
    return operand.operate(entityArray);
  }

  @Override
  public void bind(String[] entityNames) {
    condition.bind(entityNames);
    operand.bind(entityNames);
  }
}
