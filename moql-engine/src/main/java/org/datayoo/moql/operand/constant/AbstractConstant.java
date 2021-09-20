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
package org.datayoo.moql.operand.constant;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.OperandType;
import org.datayoo.moql.operand.AbstractOperand;

/**
 * @author Tang Tadin
 */
public abstract class AbstractConstant extends AbstractOperand
    implements Constant {

  protected Object data;

  protected ConstantType constantType;

  {
    operandType = OperandType.CONSTANT;
    constantReturn = true;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return data;
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub

  }

  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    return data;
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub

  }

  @Override
  public ConstantType getConstantType() {
    // TODO Auto-generated method stub
    return constantType;
  }

  @Override
  public void bind(String[] entityNames) {
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    return data;
  }
}
