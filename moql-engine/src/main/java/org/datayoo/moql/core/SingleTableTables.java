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

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.metadata.TablesMetadata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SingleTableTables implements Tables {
	
	protected TablesMetadata tablesMetadata;
	
	protected Table table;
	
	protected List<EntityMap> result;

	
	public SingleTableTables(TablesMetadata tablesMetadata, Table table) {
		Validate.notNull(tablesMetadata, "Parameter 'tablesMetadata' is null!");
		Validate.notNull(table, "Parameter 'table' is null!");
		
		this.tablesMetadata = tablesMetadata;
		this.table = table;
	}

	/* (non-Javadoc)
	 * @see org.moql.core.Tables#getQueryable()
	 */
	@Override
	public Queryable<? extends Object> getQueryable() {
		// TODO Auto-generated method stub
		return table;
	}

	@Override
	public TablesMetadata getTablesMetadata() {
		// TODO Auto-generated method stub
		return tablesMetadata;
	}

	/* (non-Javadoc)
	 * @see org.moql.core.Queryable#bind(org.moql.DataSetMap)
	 */
	@Override
	public void bind(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		table.bind(dataSetMap);
		result = new LinkedList<EntityMap>();
		String tableName = table.getTableMetadata().getName();
		for(Iterator<Object> it = table.iterator(); it.hasNext();) {
			EntityMap entityMap = new EntityMapImpl();
			entityMap.putEntity(tableName, it.next());
			result.add(entityMap);
		}
	}

	@Override
	public Iterator<EntityMap> iterator() {
		// TODO Auto-generated method stub
		return result.iterator();
	}

}
