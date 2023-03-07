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
package org.datayoo.moql.operand;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.OperandType;

/**
 * @author Tang Tadin
 */
public abstract class AbstractOperand implements Operand, OperandSourceAware {

  protected String name;

  protected Object source;

  protected OperandType operandType = OperandType.UNKNOWN;

  protected boolean constantReturn = false;

  protected EntityMap entityMap;

  protected boolean binded = false;

  protected int operandIndex = -1;

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public Object getSource() {
    // TODO Auto-generated method stub
    return source;
  }

  /* (non-Javadoc)
   * @see org.moql.operand.OperandAware#setSource(java.lang.Object)
   */
  @Override
  public void setSource(Object source) {
    // TODO Auto-generated method stub
    Validate.notNull(source, "source is null!");
    this.source = source;
  }

  @Override
  public OperandType getOperandType() {
    // TODO Auto-generated method stub
    return operandType;
  }

  /* (non-Javadoc)
   * @see org.moql.operand.Operand#isConstantReturn()
   */
  @Override
  public boolean isConstantReturn() {
    // TODO Auto-generated method stub
    return constantReturn;
  }

  /* (non-Javadoc)
   * @see org.moql.operand.Operand#booleanOperate(org.moql.data.EntityMap)
   */
  @Override
  public boolean booleanOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return isTrue(operate(entityMap));
  }

  public static boolean isTrue(Object obj) {
    if (obj == null)
      return false;
    if (obj.getClass() == Boolean.class)
      return ((Boolean) (obj)).booleanValue();
    return true;
  }

  @Override
  public boolean booleanOperate(Object[] entityArray) {
    return isTrue(operate(entityArray));
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Validate.notNull(entityMap, "entityMap is null!");
    this.entityMap = entityMap;
  }

  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    return operate(entityMap);
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    entityMap = null;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public boolean isBinded() {
    return binded;
  }

  @Override
  public Operand setValue(Object[] entityArray, Object value) {
    throw new UnsupportedOperationException("The operand unsupport set value!");
  }

  @Override
  public Operand setValue(EntityMap entityMap, Object value) {
    throw new UnsupportedOperationException("The operand unsupport set value!");
  }
}
