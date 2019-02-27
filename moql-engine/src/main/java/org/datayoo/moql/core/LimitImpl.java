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

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.metadata.LimitMetadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class LimitImpl implements Limit {

	protected LimitMetadata limitMetadata;
	
	public LimitImpl(LimitMetadata limitMetadata) {
		Validate.notNull(limitMetadata, "Parameter 'limitMetadata' is null!");
		this.limitMetadata = limitMetadata;
	}
	
	@Override
	public RecordSet decorate(RecordSet recordSet, Columns columns) {
		// TODO Auto-generated method stub
		if (limitMetadata.getValue() == LimitMetadata.INFINITE)
			return recordSet;
		List<Object[]> records = recordSet.getRecords();
		int value = limitMetadata.getValue();
		if (limitMetadata.isPercent()) {
			double percent = value/100.00;
			value = (int)(records.size() * percent);
		}
		List<Object[]> resultRecords = new ArrayList<Object[]>(value);
		int offset = limitMetadata.getOffset();
		for(Iterator<Object[]> it = records.iterator(); it.hasNext();) {
			if (offset > 0) {
				it.next();
				offset--;
			} else {
				if (value == 0) {
					break;
				}
				resultRecords.add(it.next());
				value--;
			}
		}
		return new RecordSetImpl(recordSet.getRecordSetDefinition(),
				recordSet.getStart(), recordSet.getEnd(), resultRecords);
	}

	@Override
	public LimitMetadata getLimitMetadata() {
		// TODO Auto-generated method stub
		return limitMetadata;
	}

}
