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

import java.io.Serializable;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class OperationMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected OperatorType operatorType;

	protected String operator;
	
	public OperationMetadata(OperatorType operatorType, String operator) {
		Validate.notNull(operatorType, "Parameter 'operatorType' is null!");
		Validate.notEmpty(operator, "Parameter 'operator' is null!");
		this.operatorType = operatorType;
		this.operator = operator.toLowerCase();
	}

	/**
	 * @return the operatorType
	 */
	public OperatorType getOperatorType() {
		return operatorType;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}
	
	

}
