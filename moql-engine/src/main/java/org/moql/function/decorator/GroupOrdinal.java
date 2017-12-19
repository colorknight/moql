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

package org.moql.function.decorator;

import org.moql.Operand;
import org.moql.RecordSet;
import org.moql.RecordSetDefinition;
import org.moql.core.Columns;
import org.moql.core.group.GroupKey;
import org.moql.operand.function.decorator.DecorateFunction;
import org.moql.util.StringFormater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class GroupOrdinal extends DecorateFunction {
	
	public static final String FUNCTION_NAME = "groupOrdinal";
	
	protected String[] groupColumns;
	
	protected int[] groupColumnIndexes;
	
	protected String valueColumn;
	
	protected int valueColumnIndex;
	
	protected Map<GroupKey, RowNumberSequence> groupNumberSequences = new HashMap<GroupKey, RowNumberSequence>();
	
	protected GroupKey tempGroupKey = new GroupKey();

	public GroupOrdinal(List<Operand> parameters) {
		super(FUNCTION_NAME, 2, parameters);
		// TODO Auto-generated constructor stub
		String groupColumnsString = (String)parameters.get(0).operate(null);
		if (groupColumnsString == null || groupColumnsString.isEmpty())
			throw new IllegalArgumentException("groupColumns is empty!");
		groupColumns = groupColumnsString.split(",");
		valueColumn = (String)parameters.get(1).operate(null);
		if (valueColumn == null || valueColumn.isEmpty()) {
			throw new IllegalArgumentException("valueColumn is empty!");
		}
	}

	@Override
	public RecordSet decorate(RecordSet recordSet, Columns columns) {
		// TODO Auto-generated method stub
		if (recordSet == null)
			return null;
		initialize(recordSet);
		List<Object[]> records = recordSet.getRecords();
		for(Object[] record : records) {
			RowNumberSequence rowNumberSequence = getRowNumberSequence(record);
			record[valueColumnIndex] = ++rowNumberSequence.sequence;
		}
		return recordSet;
	}
	
	protected void initialize(RecordSet recordSet) {
		RecordSetDefinition recordSetDefinition = recordSet.getRecordSetDefinition();
		groupColumnIndexes = new int[groupColumns.length];
		for(int i = 0; i < groupColumns.length; i++) {
			groupColumnIndexes[i] = recordSetDefinition.getColumnIndex(groupColumns[i]);
			if (groupColumnIndexes[i] == -1) {
				throw new IllegalArgumentException(StringFormater.format(
						"Record set has no column named '{}'!", groupColumns[i]));
			}
		}
		valueColumnIndex = recordSetDefinition.getColumnIndex(valueColumn);
		if (valueColumnIndex == -1) {
			throw new IllegalArgumentException(StringFormater.format(
					"Record set has no column named '{}'!", valueColumn));
		}
	}
	
	protected RowNumberSequence getRowNumberSequence(Object[] record) {
		fillTempGroupKey(record);
		RowNumberSequence rowNumberSequence = groupNumberSequences.get(tempGroupKey);
		if (rowNumberSequence == null) {
			rowNumberSequence = new RowNumberSequence();
			groupNumberSequences.put(tempGroupKey, rowNumberSequence);
			tempGroupKey = new GroupKey();
		}
		return rowNumberSequence;
	}
	
	protected void fillTempGroupKey(Object[] record) {
		Object[] values = new Object[groupColumns.length];
		for(int i = 0; i < groupColumns.length; i++) {
			values[i] = record[groupColumnIndexes[i]];
		}
		tempGroupKey.initialize(values);
	}

	protected class RowNumberSequence {
		
		public int sequence = 0;
		
		public RowNumberSequence() {}
		
	}

}
