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
package org.moql.core;

import org.apache.commons.lang.Validate;
import org.moql.EntityMap;
import org.moql.metadata.ColumnsMetadata;

import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ColumnsImpl implements Columns {
	
	protected ColumnsMetadata columnsMetadata;
	
	protected List<Column> columns;

	public ColumnsImpl(ColumnsMetadata columnsMetadata, List<Column> columns) {
		Validate.notNull(columnsMetadata, "Paraemter 'columnsMetadata' is null!");
		Validate.notEmpty(columns, "Paraemter 'columns' is empty!");
		this.columnsMetadata = columnsMetadata;
		this.columns = columns;
	}
	
	@Override
	public List<Column> getColumns() {
		// TODO Auto-generated method stub
		return columns;
	}

	@Override
	public ColumnsMetadata getColumnsMetadata() {
		// TODO Auto-generated method stub
		return columnsMetadata;
	}

	@Override
	public Object[] getValue() {
		// TODO Auto-generated method stub
		Object[] values= new Object[columns.size()];
		int i = 0;
		for(Column column : columns) {
			values[i++] = column.getValue();
		}
		return values;
	}

	@Override
	public void operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		for(Column column : columns) {
			column.operate(entityMap);
		}
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		for(Column column : columns)
			column.clear();
	}

}
