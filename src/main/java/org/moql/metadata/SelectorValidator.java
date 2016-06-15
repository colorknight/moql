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

import org.moql.SelectorDefinition;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class SelectorValidator {

	public static void isValidateNestedSelector(SelectorDefinition nestedSelector) {
		if (nestedSelector instanceof SelectorMetadata) {
			SelectorMetadata nestedSelectorMetadata = (SelectorMetadata)nestedSelector;
			if (nestedSelectorMetadata.getOrderBy() != null 
					&& nestedSelectorMetadata.getOrderBy().size() > 0 ) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has orderby columns!");
			}
		} else {
			SetlectorMetadata nestedSetlectorMetadata = (SetlectorMetadata)nestedSelector;
			if (nestedSetlectorMetadata.getOrderBy() != null 
					&& nestedSetlectorMetadata.getOrderBy().size() > 0 ) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has orderby columns!");
			}
		}
	}
	
	public static void isValidateNestedColumnSelector(SelectorDefinition nestedSelector) {
		if (nestedSelector instanceof SelectorMetadata) {
			SelectorMetadata nestedSelectorMetadata = (SelectorMetadata)nestedSelector;
			if (nestedSelectorMetadata.getOrderBy() != null 
					&& nestedSelectorMetadata.getOrderBy().size() > 0 ) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has orderby columns!");
			}
			if (nestedSelectorMetadata.getColumns().getColumns().size() != 1) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has more columns!");				
			}
		} else {
			SetlectorMetadata nestedSetlectorMetadata = (SetlectorMetadata)nestedSelector;
			if (nestedSetlectorMetadata.getOrderBy() != null 
					&& nestedSetlectorMetadata.getOrderBy().size() > 0 ) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has orderby columns!");
			}
			if (nestedSetlectorMetadata.getColumns().getColumns().size() != 1) {
				throw new IllegalArgumentException("Parameter 'nestedSelector' has more columns!");				
			}
		}
	}
}
