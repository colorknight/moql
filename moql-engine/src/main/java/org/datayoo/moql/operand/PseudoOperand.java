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
public class PseudoOperand implements Operand, OperandSourceAware {

  protected String name;

  public PseudoOperand(String name) {
    Validate.notEmpty(name, "name is empty!");
    this.name = name;
  }

  @Override
  public void setSource(Object source) {
    // TODO Auto-generated method stub
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return name;
  }

  @Override
  public Object getSource() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public boolean booleanOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public boolean booleanOperate(Object[] entityArray) {
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public OperandType getOperandType() {
    // TODO Auto-generated method stub
    return OperandType.PESUDO;
  }

  @Override
  public boolean isConstantReturn() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Pesudo operator is just used for sql translation!");
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
  }

  @Override
  public void bind(String[] entityNames) {

  }

  @Override
  public boolean isBinded() {
    return false;
  }

  @Override
  public Object operate(Object[] entityArray) {
    return null;
  }
}
