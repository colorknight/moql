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
package org.moql.core.combination;

import org.apache.commons.lang.Validate;
import org.moql.ColumnDefinition;
import org.moql.RecordSet;
import org.moql.RecordSetDefinition;
import org.moql.core.RecordSetCombination;
import org.moql.core.RecordSetMetadata;
import org.moql.metadata.ColumnMetadata;
import org.moql.metadata.ColumnsMetadata;
import org.moql.util.StringFormater;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class AbstractRecordSetCombination implements RecordSetCombination {
	
	protected ColumnsMetadata columnsMetadata;
	
	public AbstractRecordSetCombination(ColumnsMetadata columnsMetadata) {
		Validate.notNull(columnsMetadata, "Parameter 'columnsMetadata is null!");
		this.columnsMetadata = columnsMetadata;
	}
	
	@Override
	public RecordSet combine(RecordSet recordSet, RecordSet recordSet2) {
		// TODO Auto-generated method stub
		boolean all = !columnsMetadata.isDistinct();
		if (columnsMetadata.getColumns().size() == 0) {
			return combine(all, recordSet, recordSet2);
		}
		return combine(all, columnsMetadata.getColumns(), recordSet, recordSet2);
	}
	
	protected abstract RecordSet combine(boolean all,
			RecordSet lRecordSet,
			RecordSet rRecordSet);
	
	protected abstract RecordSet combine(boolean all, List<ColumnMetadata> columns,
			RecordSet lRecordSet,
			RecordSet rRecordSet);
	
	protected boolean existRecord(Object[] record, List<Object[]> records,
			Comparator<Object[]> comparator) {
		for(Object[] rec : records) {
			if (comparator.compare(rec, record) == 0)
				return true;
		}
		return false;
	}
	
	protected int[] createSequenceIndexes(int length) {
		int[] indexes = new int[length];
		for(int i = 0; i < length; i++) {
			indexes[i] = i;
		}
		return indexes;
	}
	
	protected int[] getColumnsMapping(List<? extends ColumnDefinition> lColumns,
			List<ColumnDefinition> rColumns) {
		Validate.notEmpty(lColumns, "Parameter 'lColumns' is empty!");
		Validate.notEmpty(rColumns, "Parameter 'rColumns' is empty!");
		
		if (lColumns.size() > rColumns.size()) {
			throw new IllegalArgumentException("The right column list's size is less than the left one!");
		}
		int[] mappingIndexes = new int[lColumns.size()];
		int index = 0;
		int i = 0;
		for(ColumnDefinition column : lColumns) {
			index = getColumnIndex(column.getName(), rColumns);
			if (index == -1) {
				throw new IllegalArgumentException(
						StringFormater.format("The right column list has no column '{}'!", column.getName()));
			}
			mappingIndexes[i++] = index;
		}
		return mappingIndexes;
	}
	
	protected int getColumnIndex(String columnName, List<ColumnDefinition> columns) {
		int index = 0;
		for(ColumnDefinition column : columns) {
			if (column.getName().equals(columnName)) {
				return index;
			}
			index ++;
		}
		return -1;
	}
	
	protected RecordSetDefinition createRecordSetDefinition() {
		List<ColumnDefinition> columnDefinitions = 
			new ArrayList<ColumnDefinition>(columnsMetadata.getColumns().size());
		for(ColumnMetadata columnMetadata : columnsMetadata.getColumns()) {
			columnDefinitions.add(columnMetadata);
		}
		return new RecordSetMetadata(columnDefinitions, null);
	}
	
	protected Date getStart(Date lStart, Date rStart) {
		if (lStart.getTime() > rStart.getTime())
			return rStart;
		return lStart;
	}
	
	protected Date getEnd(Date lEnd, Date rEnd) {
		if (lEnd.getTime() < rEnd.getTime())
			return rEnd;
		return lEnd;
	}
}
