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
public class TableMetadata implements QueryableMetadata, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String name;
	
	protected String value;
	
	protected SelectorDefinition nestedSelector;
	
	public TableMetadata(String name, String value) {
		Validate.notEmpty(name, "Parameter 'name' is empty!");
		Validate.notEmpty(value, "Parameter 'value' is empty!");
		this.name = name;
		this.value = value;
	}
	
	public TableMetadata(String name, SelectorDefinition nestedSelector) {
		Validate.notEmpty(name, "Parameter 'name' is empty!");
		Validate.notNull(nestedSelector, "Parameter 'nestedSelector' is empty!");
		SelectorValidator.isValidateNestedSelector(nestedSelector);
		
		this.name = name;
		this.nestedSelector = nestedSelector;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return the nestedSelector
	 */
	public SelectorDefinition getNestedSelector() {
		return nestedSelector;
	}

}
