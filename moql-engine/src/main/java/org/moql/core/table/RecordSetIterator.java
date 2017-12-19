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
import org.moql.RecordSet;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class RecordSetIterator implements Iterator<Object> {
	
	protected RecordSet recordSet;
	
	protected Iterator<Map<String,Object>> it;
	
	
	public RecordSetIterator(RecordSet recordSet) {
		Validate.notNull(recordSet, "Parameter 'recordSet' is null!");
		this.recordSet = recordSet;
		it = recordSet.getRecordsAsMaps().iterator();
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return it.hasNext();
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		return it.next();
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("");
	}

}
