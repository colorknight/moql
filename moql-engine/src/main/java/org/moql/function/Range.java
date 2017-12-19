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
package org.moql.function;

import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.operand.constant.ConstantType;
import org.moql.operand.function.AggregationFunction;
import org.moql.util.CompareHelper;

import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class Range extends AggregationFunction {

	public static final String FUNCTION_NAME = "range";
	
	protected Number max;
	
	protected ConstantType maxType;
	
	protected Number min;
	
	protected ConstantType minType;
	
	public Range(List<Operand> parameters) {
		super(FUNCTION_NAME, 1, parameters);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void increment(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = parameters.get(0).operate(entityMap);
		if (obj == null) {
			return;
		}
		Number num = toNumber(obj);
		ConstantType numType = getConstantType(num);
		if (max == null) {
			max = num;
			maxType = numType;
			min = num;
			minType = numType;
		} else {
			if (CompareHelper.compare(max, num) < 0) {
				max = num;
				maxType = numType;
			}
			else {
				if (CompareHelper.compare(min, num) > 0) {
					min = num;
					minType = numType;
				}
			}
		}
	}
	
	protected ConstantType getConstantType(Number number) {
		if (number instanceof Float 
				|| number instanceof Double)
			return ConstantType.DOUBLE;
		
		return ConstantType.LONG;
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		if (maxType == ConstantType.LONG 
				&& minType == ConstantType.LONG) {
			return max.longValue() - min.longValue();
		}
		return max.doubleValue() - min.doubleValue();
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		max = null;
		min = null;
	}

}
