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
package org.datayoo.moql.core.group;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.*;
import org.datayoo.moql.core.*;
import org.datayoo.moql.core.cache.CacheImpl;
import org.datayoo.moql.metadata.CacheMetadata;
import org.datayoo.moql.metadata.GroupMetadata;
import org.datayoo.moql.operand.function.AggregationFunction;
import org.datayoo.moql.util.StringFormater;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class GroupRecordSetOperator implements Group {
	
	protected Cache<GroupKey, GroupRecord> cache;
	
	protected Columns columns;
	
	protected List<GroupMetadata> groupMetadatas;
	
	protected MoqlFactory moqlFactory;
	
	protected Column[] groupColumns;
	
	protected int[] groupColumnIndexes;

	protected Column[] nonGroupColumns;
	
	protected Date start;
	
	protected GroupRecordConverter groupRecordConverter = new GroupRecordConverter();
	
	protected GroupKey tempGroupKey = new GroupKey();
	
	public GroupRecordSetOperator(CacheMetadata cacheMetadata, Columns columns,
			List<GroupMetadata> groupMetadatas, MoqlFactory moqlFactory) throws
      MoqlGrammarException {
		Validate.notNull(cacheMetadata, "Parameter 'cacheMetadata' is null!");
		Validate.notNull(columns, "Parameter 'columns' is null!");
		Validate.notEmpty(groupMetadatas, "Parameter 'groupMetadatas' is empty!");
		Validate.notNull(moqlFactory, "Parameter 'moqlFactory' is null!");
		this.cache = new CacheImpl<GroupKey, GroupRecord>(cacheMetadata);
		this.columns = columns;
		this.groupMetadatas = groupMetadatas;
		this.moqlFactory = moqlFactory;
		initialize();
		start = new Date();
	}
	
	protected void initialize() throws MoqlGrammarException {
		groupColumns = new Column[groupMetadatas.size()];
		groupColumnIndexes = new int[groupMetadatas.size()];
		nonGroupColumns = new Column[columns.getColumns().size()];
		columns.getColumns().toArray(nonGroupColumns);
		int i = 0;
		for(GroupMetadata groupMetadata : groupMetadatas) {
			int index = getGroupColumnIndex(groupMetadata, columns.getColumns());
			if (index > columns.getColumns().size()
					|| index < 1) {
				throw new MoqlGrammarException(StringFormater
            .format("Group column indexed '{}' out of columns' bound!", index));
			}
			index --;
			groupColumns[i] = nonGroupColumns[index];
			groupColumnIndexes[i++] = index;
			nonGroupColumns[index] = null;
		}
		checkNonGroupColumns();
	}
	
	protected void checkNonGroupColumns() throws MoqlGrammarException {
		for(int i = 0; i < nonGroupColumns.length; i++) {
			if (nonGroupColumns[i] == null)
				continue;
			if (nonGroupColumns[i].isJustUsed4Order())
			  continue;
			Operand operand = nonGroupColumns[i].getOperand();
			if (operand instanceof AggregationFunction) {
				continue;
			}
			throw new MoqlGrammarException(StringFormater.format(
					"Nongroup column '{}' is not a function expression!", 
					nonGroupColumns[i].getColumnMetadata().getName()));
		}
	}
	
	protected int getGroupColumnIndex(GroupMetadata groupMetadata, List<Column> columns) throws MoqlGrammarException {
		String columnName = groupMetadata.getColumn();
		int index = 1;
		try {
		  // the index in sql grammar started from 1.
			index = Integer.valueOf(columnName);
			return index;
		} catch(Throwable t) {}
		for(Column column : columns) {
			if (column.getColumnMetadata().getName().equals(columnName))
				return index;
			if (column.getColumnMetadata().getValue().equals(columnName))
				return index;
			index++;
		}
		throw new MoqlGrammarException(StringFormater.format("Group column '{}' doesn't existed in colums!", columnName));
	}

	@Override
	public List<GroupMetadata> getGroupMetadatas() {
		// TODO Auto-generated method stub
		return groupMetadatas;
	}
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	public Cache getCache() {
		// TODO Auto-generated method stub
		return cache;
	}

	@Override
	public Columns getColumns() {
		// TODO Auto-generated method stub
		return columns;
	}
	
	public Column[] getGroupColumns() {
		return groupColumns;
	}

	public Column[] getNonGroupColumns() {
		return nonGroupColumns;
	}

	@Override
	public RecordSet getValue() {
		// TODO Auto-generated method stub
		RecordSetDefinition recordSetDefinition = createRecordSetDefinition();
		return new RecordSetImpl(recordSetDefinition, start, new Date(), cache.values(groupRecordConverter));
	}
	
	protected RecordSetDefinition createRecordSetDefinition() {
		List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
		for(Column column : columns.getColumns()) {
			columnDefinitions.add(column.getColumnMetadata());
		}
		List<ColumnDefinition> groupDefinitions = new ArrayList<ColumnDefinition>();
		for(Column column : groupColumns) {
			groupDefinitions.add(column.getColumnMetadata());
		}
		return new RecordSetMetadata(columnDefinitions, groupDefinitions);
	}

	@Override
	public synchronized void operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		fillTempGroupKey(entityMap);
		GroupRecord record = cache.get(tempGroupKey);
		if (record == null) {
			try {
				record = createGroupRecord(tempGroupKey.groups);
				cache.put(tempGroupKey, record);
				tempGroupKey = new GroupKey();
			} catch (MoqlException e) {
				// TODO Auto-generated catch block
				throw new OperateException(e);
			}
		}
		record.operate(entityMap);
	}
	
	protected void fillTempGroupKey(EntityMap entityMap) {
		Object[] values = new Object[groupColumns.length];
		for(int i = 0; i < groupColumns.length; i++) {
			groupColumns[i].operate(entityMap);
			values[i] = groupColumns[i].getValue();
		}
		tempGroupKey.initialize(values);
	}
	
	protected GroupRecord createGroupRecord(Object[] groupKeys) throws MoqlException {
		Column[] columns = new Column[nonGroupColumns.length];
		for(int i = 0; i < nonGroupColumns.length; i++) {
			if (nonGroupColumns[i] != null) {
				Column column = moqlFactory.createColumn(nonGroupColumns[i].getColumnMetadata(),
						nonGroupColumns[i].isJustUsed4Order());
				columns[i] = column;
			}
		}
		return new GroupRecord(groupKeys, groupColumnIndexes, columns);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		cache.clear();
		for(Column column : groupColumns) {
			column.clear();
		}
		start = new Date();
	}

}
