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
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.ColumnMetadata;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ColumnImpl implements Column {
	
	protected ColumnMetadata columnMetadata;
	
	protected Operand operand;
	
	protected boolean justUsed4Order = false;
	
	public ColumnImpl(ColumnMetadata columnMetadata, Operand operand) {
		Validate.notNull(columnMetadata, "Parameter 'columnMetadata' is null!");
		Validate.notNull(operand, "Parameter 'operand' is null!");
		this.columnMetadata = columnMetadata;
		this.operand = operand;
		clear();
	}
	
	public ColumnImpl(ColumnMetadata column, Operand operand, boolean justUsed4Order) {
		this(column, operand);
		this.justUsed4Order = justUsed4Order;
	}

	@Override
	public void operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		operand.increment(entityMap);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		operand.clear();
	}

	/**
	 * @return the operand
	 */
	public Operand getOperand() {
		return operand;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return operand.getValue();
	}

	/**
	 * @return the column
	 */
	public ColumnMetadata getColumnMetadata() {
		return columnMetadata;
	}

	/* (non-Javadoc)
	 * @see org.moql.core.Column#isJustUsed4Order()
	 */
	@Override
	public boolean isJustUsed4Order() {
		// TODO Auto-generated method stub
		return justUsed4Order;
	}	
	
	
}
