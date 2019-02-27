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
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.metadata.ConditionMetadata;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class HavingImpl implements Having {
	
	protected Condition condition;
	
	public HavingImpl(Condition condition) {
		Validate.notNull(condition, "Parameter 'condition' is null!");
		this.condition = condition;
	}

	@Override
	public ConditionMetadata getHavingMetadata() {
		// TODO Auto-generated method stub
		return condition.getConditionMetadata();
	}

	@Override
	public RecordSet decorate(RecordSet recordSet, Columns columns) {
		// TODO Auto-generated method stub
		List<Object[]> resultRecords = new LinkedList<Object[]>();
		List<Object[]> records = recordSet.getRecords();
		for(Object[] record : records) {
			EntityMap entityMap = toEntityMap(recordSet, record);
			if (condition.isMatch(entityMap)) {
				resultRecords.add(record);
			}
		}
		return new RecordSetImpl(recordSet.getRecordSetDefinition(),
				recordSet.getStart(), recordSet.getEnd(), resultRecords);
	}
	
	protected EntityMap toEntityMap(RecordSet recordSet, Object[] record) {
		EntityMap entityMap = new EntityMapImpl();
		int i = 0;
		for(ColumnDefinition column : recordSet.getRecordSetDefinition().getColumns()) {
			entityMap.putEntity(column.getName(), record[i++]);
		}
		return entityMap;
	}

	public Condition getCondition() {
		return condition;
	}

}
