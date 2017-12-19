/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moql.operand.selector;

import org.apache.commons.lang.Validate;
import org.moql.EntityMap;
import org.moql.OperandType;
import org.moql.RecordSet;
import org.moql.Selector;
import org.moql.operand.AbstractOperand;
import org.moql.operand.OperandContextArrayList;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ColumnSelectorOperand extends AbstractOperand {

	{
		operandType = OperandType.COLUMNSELECTOR;
	}
	
	protected Selector columnSelector;
	
	public ColumnSelectorOperand(Selector columnSelector) {
		Validate.notNull(columnSelector, "Parameter 'columnSelector' is null!");
		this.columnSelector = columnSelector;
	}
	
	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		RecordSet recordSet = columnSelector.getRecordSet();
		return new OperandContextArrayList(recordSet.getRecordsAsMaps());
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		columnSelector.clear();
	}

	public Selector getColumnSelector() {
		return columnSelector;
	}

}
