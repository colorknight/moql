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
import org.moql.NumberConvertable;
import org.moql.Operand;
import org.moql.operand.function.AbstractFunction;
import org.moql.operand.function.FunctionType;
import org.moql.util.StringFormater;

import java.util.List;

public abstract class AggregationFunction extends AbstractFunction {

	{
		functionType = FunctionType.AGGREGATE;
	}

	protected AggregationFunction(String name, int parameterCount,
			List<Operand> parameters) {
		super(name, parameterCount, parameters);
		// TODO Auto-generated constructor stub
	}
	
	public AggregationFunction(String name, List<Operand> parameters) {
		super(name, parameters);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object innerOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		increment(entityMap);
		return getValue();
	}
	
	protected Number toNumber(Object obj) {
		if (obj instanceof Number)
			return (Number)obj;
		if (obj instanceof NumberConvertable) {
			return ((NumberConvertable)obj).toNumber();
		}
		throw new IllegalArgumentException(StringFormater.format("Object '{}' of class '{}' can not cast to number!",
				obj.toString(), obj.getClass().getName()));
	}

	
	public abstract void increment(EntityMap entityMap);
	
	public abstract Object getValue();
	
	public abstract void clear();
	
}
