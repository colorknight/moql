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
package org.datayoo.moql.operand.function;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SemiVariance extends AggregationFunction {
	
	public static final String FUNCTION_NAME = "semiVariance";

	protected org.apache.commons.math3.stat.descriptive.moment.SemiVariance semiVariance = 
			new org.apache.commons.math3.stat.descriptive.moment.SemiVariance();
	
	protected double cutoff;
	
	protected List<Double> numbers = new LinkedList<Double>();
	
	public SemiVariance(List<Operand> parameters) {
		super(FUNCTION_NAME, 1, parameters);
		// TODO Auto-generated constructor stub
		if (parameters.size() == 0 || parameters.size() > 3) {
			throw new IllegalArgumentException("Invalid format! The format is 'semiVariance(field, cutoff, [, direction])'");
		}
		if (parameters.size() == 2) {
			semiVariance = new org.apache.commons.math3.stat.descriptive.moment.SemiVariance();
		}
	}

	@Override
	public void increment(EntityMap entityMap) {
		// TODO Auto-generated method stub
		if (semiVariance == null) {
			Object obj = parameters.get(1).operate(entityMap);
			Number num = toNumber(obj);
			cutoff = num.doubleValue();
			obj = parameters.get(2).operate(entityMap);
			semiVariance = new org.apache.commons.math3.stat.descriptive.moment.SemiVariance(
					org.apache.commons.math3.stat.descriptive.moment.SemiVariance.Direction.valueOf(obj.toString()));
		}
		Object obj = parameters.get(0).operate(entityMap);
		if (obj == null) {
			return;
		} 
		Number num = toNumber(obj);
		numbers.add(num.doubleValue());
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		double[] values = new double[numbers.size()];
		int i = 0;
		for(Double num : numbers) {
			values[i++] = num;
		}
		return semiVariance.evaluate(values, cutoff);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		numbers.clear();
	}

}
