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

import org.apache.commons.lang3.Validate;

import java.io.Serializable;

/**
 * 
 * @author Tang Tadin
 *
 */
public class JoinMetadata implements QueryableMetadata, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected JoinType joinType;
	
	protected QueryableMetadata lQueryable;
	
	protected QueryableMetadata rQueryable;

	protected ConditionMetadata on;

	public JoinMetadata(JoinType joinType, QueryableMetadata lQueryable,
			QueryableMetadata rQueryable) {
		// TODO Auto-generated constructor stub
		Validate.notNull(joinType, "Parameter 'joinType' is null!");
		Validate.notNull(lQueryable, "Parameter 'lQueryable' is null!");
		Validate.notNull(rQueryable, "Parameter 'rQueryable' is null!");
		this.joinType = joinType;
		this.lQueryable = lQueryable;
		this.rQueryable = rQueryable;
	}
	
	

	public QueryableMetadata getLQueryable() {
		return lQueryable;
	}



	public QueryableMetadata getRQueryable() {
		return rQueryable;
	}



	/**
	 * @return the on
	 */
	public ConditionMetadata getOn() {
		return on;
	}

	/**
	 * @param on the on to set
	 */
	public void setOn(ConditionMetadata on) {
		this.on = on;
	}

	/**
	 * @return the joinType
	 */
	public JoinType getJoinType() {
		return joinType;
	}
	
}
