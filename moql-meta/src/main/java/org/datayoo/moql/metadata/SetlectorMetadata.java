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
package org.datayoo.moql.metadata;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.SelectorDefinition;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SetlectorMetadata implements SelectorDefinition, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected CombinationType combinationType = CombinationType.UNION;
	
	protected ColumnsMetadata columns;
	
	protected List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
	
	protected List<OrderMetadata> orderBy;
	
	protected LimitMetadata limit;
	
	protected List<DecorateMetadata> decorateBy;

	/**
	 * @return the combinationType
	 */
	public CombinationType getCombinationType() {
		return combinationType;
	}

	/**
	 * @param combinationType the combinationType to set
	 */
	public void setCombinationType(CombinationType combinationType) {
		if (combinationType == null)
			combinationType = CombinationType.UNION;
		this.combinationType = combinationType;
	}

	/**
	 * @return the columns
	 */
	public ColumnsMetadata getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(ColumnsMetadata columns) {
		Validate.notNull(columns, "Parameter 'columns' is null!");
		this.columns = columns;
	}

	/**
	 * @return the sets
	 */
	public List<SelectorDefinition> getSets() {
		return sets;
	}

	/**
	 * @param sets the sets to set
	 */
	public void setSets(List<SelectorDefinition> sets) {
		Validate.notEmpty(sets, "Parameter 'sets' is empty!");
		if (sets.size() != 2) {
			throw new IllegalArgumentException("Selector's count is not 2!");
		}
		this.sets = sets;
	}

	public List<OrderMetadata> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(List<OrderMetadata> orderBy) {
		this.orderBy = orderBy;
	}

	public LimitMetadata getLimit() {
		return limit;
	}

	public void setLimit(LimitMetadata limit) {
		this.limit = limit;
	}
	/**
	 * 
	 * @return the decorateBy
	 */
	public List<DecorateMetadata> getDecorateBy() {
		return decorateBy;
	}
	/**
	 * 
	 * @param decorateBy the decorator of the result set
	 */
	public void setDecorateBy(List<DecorateMetadata> decorateBy) {
		this.decorateBy = decorateBy;
	}
	
}
