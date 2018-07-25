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
public class LimitMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int INFINITE = 0;
	
	protected int offset = 0;
	
	protected int value;
	
	protected boolean percent = false;
	
	public LimitMetadata(int offset, int value, boolean percent) {
		Validate.isTrue(offset > -1, "Parameter 'offset' is less than 0!");
		Validate.isTrue(value >= 1, "Parameter 'value' is less than 1!");
		if (percent) {
		  Validate.isTrue(value <= 100, "Parameter 'value' is bigger than 100!");
		}
		this.offset = offset;
		this.value = value;
		this.percent = percent;
	}

	
	public int getOffset() {
		return offset;
	}

	public int getValue() {
		return value;
	}

	public boolean isPercent() {
		return percent;
	}

}
