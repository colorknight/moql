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
package org.datayoo.moql.core.table;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.Selector;
import org.datayoo.moql.core.Table;
import org.datayoo.moql.metadata.TableMetadata;

import java.util.Iterator;

/**
 * @author Tang Tadin
 */
public class SelectorTable implements Table {
	
	protected TableMetadata tableMetadata;
	
	protected Selector selector;

	public SelectorTable(TableMetadata tableMetadata, Selector selector) {
		Validate.notNull(tableMetadata, "Parameter 'tableMetadata' is null!");
		Validate.notNull(selector, "Parameter 'selector' is null!");
		this.tableMetadata = tableMetadata;
		this.selector = selector;
	}

	@Override
	public TableMetadata getTableMetadata() {
		// TODO Auto-generated method stub
		return tableMetadata;
	}

	@Override
	public void bind(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Iterator<Object> iterator() {
		// TODO Auto-generated method stub
		RecordSet recordSet = selector.getRecordSet();
		return (Iterator)recordSet.getRecordsAsMaps().iterator();
	}
	
	
	public Selector getSelector() {
		return selector;
	}
}
