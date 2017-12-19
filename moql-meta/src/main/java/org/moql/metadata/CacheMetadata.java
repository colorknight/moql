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

import java.io.Serializable;

/**
 * 
 * @author Tang Tadin
 *
 */
public class CacheMetadata implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int INFINITE = 0;
	
	public static final int DEFAULT_CACHE_SIZE = INFINITE;
	
	public static final CacheMetadata DEFAULT_CACHE = new CacheMetadata(INFINITE);
	
	protected int size;
	
	protected WashoutStrategy washoutStrategy = WashoutStrategy.FIFO;
	
	public CacheMetadata(int size) {
		Validate.isTrue(size > -1, "Parameter 'size' less than 0!");
		this.size = size;
	}
	
	public CacheMetadata(int size, WashoutStrategy washoutStrategy) {
		Validate.isTrue(size > -1, "Parameter 'size' less than 0!");
		Validate.notNull(washoutStrategy, "Parameter 'washoutStrategy' is null!");
		this.size = size;
		this.washoutStrategy = washoutStrategy;
	}

	public int getSize() {
		return size;
	}

	public WashoutStrategy getWashoutStrategy() {
		return washoutStrategy;
	}

	public void setWashoutStrategy(WashoutStrategy washoutStrategy) {
		Validate.notNull(washoutStrategy, "Parameter 'washoutStrategy' is null!");
		this.washoutStrategy = washoutStrategy;
	}

}
