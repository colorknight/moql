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

import org.apache.commons.lang.Validate;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.MoqlGrammarException;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;
import org.datayoo.moql.metadata.OrderMetadata;
import org.datayoo.moql.metadata.OrderType;
import org.datayoo.moql.util.StringFormater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class OrderImpl implements Order {
	
	protected List<OrderMetadata> orderMetadatas;
	
	protected Columns columns;
	
	protected int[] orderColumnIndexs;
	
	protected OrderType[] orderTypes;
	
	protected List<ColumnDefinition> columnDefinitions;
	
	protected int[] justOrderColumnIndexes;
	
	protected int justOrderCount = 0;
	
	protected RecordComparator recordComparator = new RecordComparator();
	
	public OrderImpl(Columns columns, List<OrderMetadata> orderMetadatas) throws
      MoqlGrammarException {
		Validate.notEmpty(orderMetadatas, "Parameter 'orderMetadatas' is empty!");
		this.columns = columns;
		this.orderMetadatas = orderMetadatas;
		initialize();
	}
	
	protected void initialize() throws MoqlGrammarException {
		initializeOrderColumnIndexes();
		initializeColumnDefinitions();
	}
	
	protected void initializeOrderColumnIndexes() throws MoqlGrammarException {
		orderColumnIndexs = new int[orderMetadatas.size()];
		orderTypes = new OrderType[orderMetadatas.size()];
		int i = 0;
		for(OrderMetadata orderMetadata : orderMetadatas) {
			int index = getOrderColumnIndex(orderMetadata, columns.getColumns());
			if (index > columns.getColumns().size()
					|| index < 1) {
				throw new MoqlGrammarException(StringFormater
            .format("Order column indexed '{}' out of columns' bound!", index));
			}
			orderColumnIndexs[i] = index - 1;
			orderTypes[i++] = orderMetadata.getOrderType();
		}
	}
	
	protected void initializeColumnDefinitions() {
		columnDefinitions = new ArrayList<ColumnDefinition>(columns.getColumns().size());
		justOrderColumnIndexes = new int[columns.getColumns().size()];
		int i = 0;
		for(Column column : columns.getColumns()) {
			if (column.isJustUsed4Order()) {
				justOrderColumnIndexes[justOrderCount++] = i;
			} else {
				columnDefinitions.add(column.getColumnMetadata());
			}
			i++;
		}
		if (justOrderCount == 0) {
			columnDefinitions = null;
			justOrderColumnIndexes = null;
		}
			
	}
	
	protected int getOrderColumnIndex(OrderMetadata orderMetadata, List<Column> columns) throws MoqlGrammarException {
		String columnName = orderMetadata.getColumn();
		int index = 1;
		try {
			index = Integer.valueOf(columnName);
			return index;
		} catch(Throwable t) {}
		for(Column column : columns) {
			if (column.getColumnMetadata().getName().equals(columnName))
				return index;
			index++;
		}
		throw new MoqlGrammarException(StringFormater.format("Order column '{}' doesn't existed in colums!", columnName));
	}

	@Override
	public List<OrderMetadata> getOrderMetadatas() {
		// TODO Auto-generated method stub
		return orderMetadatas;
	}
	
	public Column[] getOrderColumns() {
		Column[] orderColumns = new Column[orderColumnIndexs.length];
		List<Column> columnList = columns.getColumns();
		for(int i = 0; i < orderColumnIndexs.length; i++) {
			orderColumns[i] =columnList.get(orderColumnIndexs[i]); 
		}
		return orderColumns;
	}

	public OrderType[] getOrderTypes() {
		return orderTypes;
	}

	@Override
	public RecordSet decorate(RecordSet recordSet, Columns columns) {
		// TODO Auto-generated method stub
		List<Object[]> records = recordSet.getRecords();
		Collections.sort(records, recordComparator);
		if (columnDefinitions == null) {
			return new RecordSetImpl(recordSet.getRecordSetDefinition(),
					recordSet.getStart(), recordSet.getEnd(), records);
		}
		records = clearOrderColumns(records);
		return new RecordSetImpl(reCreateRecordSetDefinition(recordSet.getRecordSetDefinition()),
				recordSet.getStart(), recordSet.getEnd(), records);
	}
	
	protected RecordSetDefinition reCreateRecordSetDefinition(RecordSetDefinition recordSetDefinition) {
		return new RecordSetMetadata(columnDefinitions, recordSetDefinition.getGroupColumns());
	}
	
	protected List<Object[]> clearOrderColumns(List<Object[]> records) {
		List<Object[]> newRecords = new ArrayList<Object[]>(records.size());
		for(Object[] record : records) {
			newRecords.add(clearOrderColumns(record));
		}
		return newRecords;
	}
	
	protected Object[] clearOrderColumns(Object[] record) {
		Object[] newRecord = new Object[record.length - justOrderCount];
		for(int i = 0, j = 0; i < record.length; i++) {
			if (isJustOrderColumn(i))
				continue;
			newRecord[j++] = record[i];
		}
		return newRecord;
	}
	
	protected boolean isJustOrderColumn(int index) {
		for(int i = 0; i < justOrderCount; i++) {
			if (justOrderColumnIndexes[i] == index)
				return true;
		}
		return false;
	}
	
	class RecordComparator implements Comparator<Object[]> {
        
		@Override
		@SuppressWarnings({ "rawtypes" })
		public int compare(Object[] o1, Object[] o2) {
			// TODO Auto-generated method stub
			int ret = 0;
			for(int i = 0; i < orderColumnIndexs.length; i++) {
				int index = orderColumnIndexs[i];
				ret = compare((Comparable)o1[index], (Comparable)o2[index], orderTypes[i]);
                if (ret != 0)
                    break;
			}
			return ret;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
        private int compare(Comparable cmp1, Comparable cmp2, OrderType order) {
            int ret = 0;
            if (cmp1 != null && cmp2 != null) {
                ret = cmp1.compareTo(cmp2);
            } else if (cmp1 == null && cmp2 != null) {
                ret = -1;
            } else if (cmp1 != null && cmp2 == null) {
                ret = 1;
            }
            //  反序
            if (order == OrderType.DESC) {
                ret = -ret;
            }
            return ret;
        }


    }

}
