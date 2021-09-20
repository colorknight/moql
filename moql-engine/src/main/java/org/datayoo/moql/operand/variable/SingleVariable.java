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
package org.datayoo.moql.operand.variable;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.OperandType;
import org.datayoo.moql.operand.AbstractOperand;

/**
 * @author Tang Tadin
 */
public class SingleVariable extends AbstractOperand implements Variable {

  {
    operandType = OperandType.VARIABLE;
  }

  public SingleVariable(String name) {
    Validate.notEmpty(name, "name is empty!");
    this.name = name;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return entityMap.getEntity(name);
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub

  }

  @Override
  public void bind(String[] entityNames) {
    for (int i = 0; i < entityNames.length; i++) {
      if (entityNames[i].equals(name)) {
        operandIndex = i;
        binded = true;
        return;
      }
    }
    throw new IllegalArgumentException(
        String.format("There is no entity named '%s'!", name));
  }

  @Override
  public Object operate(Object[] entityArray) {
    return entityArray[operandIndex];
  }
}
