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
package org.moql.operand.function;

import org.moql.EntityMap;
import org.moql.Operand;

import java.util.*;

/**
 * 
 * @author Tang Tadin
 *
 */
public class Mode extends AggregationFunction {
	
	public static final String FUNCTION_NAME = "mode";
	
	 protected Operand operand;
	
	protected Map<Object, InnerCounter> counters = new HashMap<Object, InnerCounter>(); 
	
	public Mode(List<Operand> parameters) {
		super(FUNCTION_NAME, 1, parameters);
		// TODO Auto-generated constructor stub
		operand = parameters.get(0);
	}

	@Override
	public void increment(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = operand.operate(entityMap);
		if (obj == null) {
			return;
		}
		InnerCounter counter = counters.get(obj);
		if (counter == null) {
			counter = new InnerCounter(0);
			counter.setObj(obj);
			counters.put(obj, counter);
		}
		counter.add(1);
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		if (counters.size() == 0)
			return null;
		List<InnerCounter> counterList = new ArrayList<InnerCounter>(counters.values());
		Collections.sort(counterList);
		Collections.reverse(counterList);
		int size = 0;
		long maxCount = counterList.get(0).getCount();
		for(InnerCounter counter : counterList) {
			if (counter.getCount() != maxCount)
				break;
			size ++;
		}
		Object[] modeArray = new Object[size];
		for(int i = 0; i < size; i++) {
			modeArray[i] = counterList.get(i).getObj();
		}
		return modeArray;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		counters.clear();
	}
	
	class InnerCounter extends Counter {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		protected Object obj;
		
		public InnerCounter(long value) {
			super(value);
			// TODO Auto-generated constructor stub
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}
		
	}

}
