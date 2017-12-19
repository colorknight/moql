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
import org.moql.operand.constant.ConstantType;

import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class Sum extends AggregationFunction {
	
	public static final String FUNCTION_NAME = "sum";

	protected Operand operand;
	
	protected Number sum = new Long(0);
	
	protected ConstantType sumType = ConstantType.LONG;
	
	public Sum(List<Operand> parameters) {
		super(FUNCTION_NAME, 1, parameters);
		// TODO Auto-generated constructor stub
		operand = parameters.get(0);
	}
	
	@Override
	public void increment(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = operand.operate(entityMap);
		if (obj == null)
			return;
		Number num = toNumber(obj);
		ConstantType numType = getConstantType(num);
		if (sum == null) {
			sum = num;
			sumType = numType;
		} else {
			if (sumType == ConstantType.DOUBLE
					|| numType == ConstantType.DOUBLE) {
				sum = new Double(sum.doubleValue()+num.doubleValue());
				sumType = ConstantType.DOUBLE;
			} else {
				sum = new Long(sum.longValue()+num.longValue());
			}
		}
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return sum;
	}
	
	protected ConstantType getConstantType(Number number) {
		if (number instanceof Float 
				|| number instanceof Double)
			return ConstantType.DOUBLE;
		
		return ConstantType.LONG;
	}

	@Override
	public synchronized void clear() {
		// TODO Auto-generated method stub
		sum = new Long(0);
	}

}
