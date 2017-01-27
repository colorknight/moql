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

import org.moql.operand.OperandContextArrayList;
import org.moql.operand.OperandContextList;

import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
@SuppressWarnings("unchecked")
public class MapAccessor implements ArrayAccessor {

	@Override
	public Object getObject(Object array, Object index) {
		// TODO Auto-generated method stub
		Map<Object, Object> map = (Map<Object, Object>)array;
		return map.get(index);
	}

	@Override
	public OperandContextList toOperandContextList(Object array) {
		// TODO Auto-generated method stub
		Map<Object, Object> map = (Map<Object, Object>)array;
		return new OperandContextArrayList(map.values());
	}

}
