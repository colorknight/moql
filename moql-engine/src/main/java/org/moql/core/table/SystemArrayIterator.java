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
package org.moql.core.table;

import org.apache.commons.lang.Validate;

import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * @author Tang Tadin
 */
public class SystemArrayIterator implements Iterator<Object>{
	
	protected Object array;
	
	protected int length = 0;
	
	protected int index = 0;
	
	public SystemArrayIterator(Object array) {
		Validate.notNull(array, "Parameter 'array' is null!");
		this.array = array;
		length = Array.getLength(array);
	}
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return index < length? true:false;
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return Array.get(array, index);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
