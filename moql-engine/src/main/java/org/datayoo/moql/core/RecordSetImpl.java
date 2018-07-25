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
package org.datayoo.moql.core;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.RecordNode;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;
import org.datayoo.moql.util.StringFormater;

import java.util.*;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class RecordSetImpl implements RecordSet {

	public static final String COLUMN_SEPARATOR = ",";

	protected RecordSetDefinition recordSetDefinition;

	protected Date start;

	protected Date end;

	protected List<Object[]> records;

	public RecordSetImpl(RecordSetDefinition recordSetDefinition, Date start,
			Date end, List<Object[]> records) {
		Validate.notNull(recordSetDefinition,
				"Parameter 'recordSetDefinition' is null!");
		Validate.notNull(start, "Parameter 'start' is null!");
		Validate.notNull(end, "Parameter 'end' is null!");
		Validate.notNull(records, "Parameter 'records' is null!");

		this.recordSetDefinition = recordSetDefinition;
		this.start = start;
		this.end = end;
		this.records = records;
	}

	@Override
	public Date getEnd() {
		// TODO Auto-generated method stub
		return end;
	}

	@Override
	public Map<String, Object> getRecordAsMap(int index) {
		// TODO Auto-generated method stub
		Object[] record = records.get(index);
		return toMap(record);
	}

	@Override
	public List<Map<String, Object>> getRecordsAsMaps() {
		// TODO Auto-generated method stub
		List<Map<String, Object>> recordMapList = new ArrayList<Map<String, Object>>(
				records.size());
		for (Object[] record : records) {
			recordMapList.add(toMap(record));
		}
		return recordMapList;
	}

	@Override
	public List<RecordNode> getRecordsAsNodes() {
		// TODO Auto-generated method stub
		String[] columns = new String[recordSetDefinition.getGroupColumns().size()];
		int i = 0;
		for(ColumnDefinition cd : recordSetDefinition.getGroupColumns()) {
			columns[i++] = cd.getName();
		}
		return getRecordNodesByColumns(columns);
	}

	@Override
	public List<RecordNode> getRecordNodesByColumns(String[] columns) {
		// TODO Auto-generated method stub
		int[][] columnIndexes = translateColumns2Indexes(columns);
		RecordNodeImpl root = new RecordNodeImpl();
		for(Object[] record : records) {
			buildRecordNode(root, columnIndexes, record);
		}
		return root.getChildren();
	}
	
	protected void buildRecordNode(RecordNodeImpl root, int[][]columnIndexes, Object[] record) {
		RecordNodeImpl node = root;
		 
		for(int i = 0; i < columnIndexes.length; i++) {
			Object[] columns = new Object[columnIndexes[i].length];
			for(int j = 0; j < columns.length; j++) {
				columns[j] = record[columnIndexes[i][j]];
			}
			//	the index of columnIndexes[columnIndexes.length - 1] is remain columns' index
			if (i != columnIndexes.length - 1) {
				RecordNodeImpl rn = (RecordNodeImpl)findRecordNode(node.getChildren(), columns);
				if (rn == null) {
					rn = new RecordNodeImpl(columns);
					node.addChild(rn);
				}
				node = rn;
			} else {
				RecordNodeImpl rn = new RecordNodeImpl(columns);
				node.addChild(rn);
			}
		}
	}
	
	protected RecordNode findRecordNode(List<RecordNode> nodes, Object[] columns) {
		for(RecordNode node : nodes) {
			if (ArrayUtils.isEquals(node.getColumns(), columns))
				return node;
		}
		return null;
	}

	protected int[][] translateColumns2Indexes(String[] columns) {
		int[][] indexes = new int[columns.length+1][];
		int[] remainColumns = new int[recordSetDefinition.getColumns().size()];
		int remainSize = remainColumns.length;
		for(int i = 0; i < remainColumns.length; i++) {
			remainColumns[i] = i;
		}
		//	fill the node column's index
		for (int i = 0; i < columns.length; i++) {
			String tokens[] = columns[i].split(COLUMN_SEPARATOR);
			int[] tIndexes = new int[tokens.length];
			for (int j = 0; j < tokens.length; j++) {
				tIndexes[j] = recordSetDefinition.getColumnIndex(tokens[j]);
        if (tIndexes[j] == -1) {
          throw new IllegalArgumentException(StringFormater.format(
              "There is no column named '{}'!", tokens[j]));
        }
				if (remainColumns[tIndexes[j]] != -1) {
					remainColumns[tIndexes[j]] = -1;
					remainSize --;
				}
			}
			indexes[i] = tIndexes;
		}
		//	fill the remain columns' index
		int[] remainIndexes = new int[remainSize];
		int j = 0;
		for(int i = 0; i < remainColumns.length; i++) {
			if (remainColumns[i] != -1) {
				remainIndexes[j++] = remainColumns[i];
			}
		}
		indexes[columns.length] = remainIndexes;
		return indexes;
	}

	@Override
	public Object[] getRecord(int index) {
		// TODO Auto-generated method stub
		return records.get(index);
	}

	@Override
	public RecordSetDefinition getRecordSetDefinition() {
		// TODO Auto-generated method stub
		return recordSetDefinition;
	}

	@Override
	public List<Object[]> getRecords() {
		// TODO Auto-generated method stub
		return records;
	}

	@Override
	public int getRecordsCount() {
		// TODO Auto-generated method stub
		return records.size();
	}

	@Override
	public Date getStart() {
		// TODO Auto-generated method stub
		return start;
	}

	@Override
	public Map<String, Object> toMap(Object[] record) {
		// TODO Auto-generated method stub
		Map<String, Object> recordMap = new HashMap<String, Object>();
		int i = 0;
		for (ColumnDefinition column : recordSetDefinition.getColumns()) {
			recordMap.put(column.getName(), record[i++]);
		}
		return recordMap;
	}

}
