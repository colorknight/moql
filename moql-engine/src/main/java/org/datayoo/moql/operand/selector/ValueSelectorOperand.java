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
package org.datayoo.moql.operand.selector;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.OperandType;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.Selector;
import org.datayoo.moql.operand.AbstractOperand;

/**
 * @author Tang Tadin
 */
public class ValueSelectorOperand extends AbstractOperand {

  {
    operandType = OperandType.COLUMNSELECTOR;
  }

  protected Selector valueSelector;

  public ValueSelectorOperand(Selector valueSelector) {
    Validate.notNull(valueSelector, "Parameter 'valueSelector' is null!");
    this.valueSelector = valueSelector;
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    RecordSet recordSet = valueSelector.getRecordSet();
    if (recordSet.getRecordsCount() == 0)
      return null;
    Object[] record = recordSet.getRecord(0);
    return record[0];
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    valueSelector.clear();
  }

  public Selector getValueSelector() {
    return valueSelector;
  }

  @Override
  public Object operate(Object[] entityArray) {
    throw new UnsupportedOperationException("");
  }

  @Override
  public void bind(String[] entityNames) {
    throw new UnsupportedOperationException("");
  }
}
