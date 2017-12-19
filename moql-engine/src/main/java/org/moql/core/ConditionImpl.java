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
import org.moql.Operand;
import org.moql.metadata.ConditionMetadata;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ConditionImpl implements Condition {
	
	protected ConditionMetadata conditionMetadata;
	
	protected Operand operand;
	
	public ConditionImpl(ConditionMetadata conditionMetadata, Operand operand) {
		Validate.notNull(conditionMetadata, "Parameter 'conditionMetadata' is null!");
		Validate.notNull(operand, "Parameter 'operand' is null!");
		this.conditionMetadata = conditionMetadata;
		this.operand = operand;
	}

	@Override
	public boolean isMatch(EntityMap entityMap) {
		// TODO Auto-generated method stub
		return operand.booleanOperate(entityMap);
	}

	@Override
	public List<EntityMap> match(List<EntityMap> entityMaps) {
		// TODO Auto-generated method stub
		List<EntityMap> matches = new LinkedList<EntityMap>();
		for(EntityMap entityMap : entityMaps) {
			if (operand.booleanOperate(entityMap)) {
				matches.add(entityMap);
			}
		}
		return matches;
	}

	@Override
	public ConditionMetadata getConditionMetadata() {
		// TODO Auto-generated method stub
		return conditionMetadata;
	}

	public Operand getOperand() {
		return operand;
	}
	
	

}
