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
import org.moql.DataSetMap;
import org.moql.EntityMap;
import org.moql.metadata.TablesMetadata;

import java.util.Iterator;
/**
 * 
 * @author Tang Tadin
 *
 */
public class JoinTables implements Tables {
	
	protected TablesMetadata tablesMetadata;
	
	protected Join join;

	public JoinTables(TablesMetadata tablesMetadata, Join join) {
		Validate.notNull(tablesMetadata, "Parameter 'tablesMetadata' is null!");
		Validate.notNull(join, "Parameter 'oin' is null!");
		
		this.tablesMetadata = tablesMetadata;
		this.join = join;
	}
	
	@Override
	public Queryable<? extends Object> getQueryable() {
		// TODO Auto-generated method stub
		return join;
	}

	@Override
	public TablesMetadata getTablesMetadata() {
		// TODO Auto-generated method stub
		return tablesMetadata;
	}

	@Override
	public void bind(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		join.bind(dataSetMap);
	}

	@Override
	public Iterator<EntityMap> iterator() {
		// TODO Auto-generated method stub
		return join.iterator();
	}

}
