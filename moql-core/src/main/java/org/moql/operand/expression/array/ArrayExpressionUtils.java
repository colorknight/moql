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
package org.moql.operand.expression.array;

import org.moql.RecordSet;
import org.moql.operand.OperandContextList;
import org.moql.util.StringFormater;

import java.sql.ResultSet;
import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ArrayExpressionUtils {

	protected static final ArrayAccessor arrayAccessor = new SystemArrayAccessor();
	
	protected static final ArrayAccessor mapAccessor = new MapAccessor();
	
	protected static final ArrayAccessor iteratorAccessor = new IteratorAccessor();
	
	protected static final ArrayAccessor resultSetAccessor = new ResultSetAccessor();
	
	protected static final ArrayAccessor recordSetAccessor = new RecordSetAccessor();
	
	public static ArrayAccessor getArrayAccessor(Object o) {
		if (o.getClass().isArray()) {
			return arrayAccessor;
		}
		if (o instanceof Map) {
			return mapAccessor;
		}
		if (o instanceof Iterable) {
			return iteratorAccessor;
		}
		if (o instanceof ResultSet) {
			return resultSetAccessor;
		}
		if (o instanceof RecordSet) {
			return recordSetAccessor;
		}
		throw new IllegalArgumentException(StringFormater.format("Unsupport class '{}'!", o.getClass().getName()));
	}
	
	public static boolean isArray(Object o) {
		if (o.getClass().isArray()) {
			return true;
		}
		if (o instanceof Map) {
			return true;
		}
		if (o instanceof Iterable) {
			return true;
		}
		if (o instanceof ResultSet) {
			return true;
		}
		if (o instanceof RecordSet) {
			return true;
		}
		return false;
	}
	
	public static OperandContextList toOperandContextList(Object o) {
		if (o instanceof OperandContextList)
			return (OperandContextList)o;
		if (o.getClass().isArray()) {
			return arrayAccessor.toOperandContextList(o);
		}
		if (o instanceof Map) {
			return mapAccessor.toOperandContextList(o);
		}
		if (o instanceof Iterable) {
			return iteratorAccessor.toOperandContextList(o);
		}
		if (o instanceof ResultSet) {
			return resultSetAccessor.toOperandContextList(o);
		}
		if (o instanceof RecordSet) {
			return recordSetAccessor.toOperandContextList(o);
		}
		return null;
	}
}
