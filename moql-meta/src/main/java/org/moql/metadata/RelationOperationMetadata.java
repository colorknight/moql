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
package org.moql.metadata;

import org.apache.commons.lang.Validate;
import org.moql.SelectorDefinition;

import java.io.Serializable;

/**
 * 
 * @author Tang Tadin
 *
 */
public class RelationOperationMetadata extends OperationMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String lOperand;	
	
	protected String rOperand;
	
	protected SelectorDefinition nestedSelector;
	
	public RelationOperationMetadata(String operator, String lOperand, String rOperand) {
		super(OperatorType.BINARY, operator);
		Validate.notEmpty(lOperand, "Parameter 'lOperand' is null!");
		Validate.notEmpty(rOperand, "Parameter 'rOperand' is null!");
		
		this.operator = operator;
		this.lOperand = lOperand;
		this.rOperand = rOperand;
	}
	
	public RelationOperationMetadata(String operator, String lOperand, SelectorDefinition nestedSelector) {
		super(OperatorType.BINARY, operator);
		Validate.notEmpty(lOperand, "Parameter 'lOperand' is null!");
		Validate.notNull(nestedSelector, "Parameter 'nestedSelector' is null!");
		SelectorValidator.isValidateNestedColumnSelector(nestedSelector);
		
		this.operator = operator;
		this.lOperand = lOperand;
		this.nestedSelector = nestedSelector;
	}
	
	public RelationOperationMetadata(String operator, String operand) {
		super(OperatorType.UNARY, operator);
		Validate.notEmpty(operand, "Parameter 'operand' is null!");
		
		this.operator = operator;
		this.rOperand = operand;
	}
	
	public RelationOperationMetadata(String operator, SelectorDefinition nestedSelector) {
		super(OperatorType.UNARY, operator);
		Validate.notNull(nestedSelector, "Parameter 'nestedSelector' is null!");
		SelectorValidator.isValidateNestedColumnSelector(nestedSelector);
		
		this.operator = operator;
		this.nestedSelector = nestedSelector;
	}

	public String getLeftOperand() {
		return lOperand;
	}

	public String getRightOperand() {
		return rOperand;
	}

	/**
	 * @return the nestedColumnSelector
	 */
	public SelectorDefinition getNestedSelector() {
		return nestedSelector;
	}
	
}
