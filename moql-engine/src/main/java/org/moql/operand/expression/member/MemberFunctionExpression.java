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
package org.moql.operand.expression.member;


import org.apache.commons.lang.Validate;
import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.OperateException;
import org.moql.SelectorConstants;
import org.moql.operand.OperandContextArrayList;
import org.moql.operand.OperandContextList;
import org.moql.operand.expression.AbstractExpression;
import org.moql.operand.expression.ExpressionType;
import org.moql.operand.function.Function;
import org.moql.util.StringFormater;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
public class MemberFunctionExpression extends AbstractExpression implements MemberExpression {
	
	protected Function function;
	
	protected Operand target;
	
	protected Class<?> clazz;
	
	protected Method method;
	
	protected Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();
	
	{
		expressionType = ExpressionType.MEMBER;
	}
	
	public MemberFunctionExpression(Operand target, Function function) {
		// TODO Auto-generated constructor stub
		Validate.notNull(target, "Parameter 'target' is null!");
		Validate.notNull(function, "Parameter 'function' is null!");
		
		this.target = target;
		this.function = function;
		
		name = buildNameString();
	}

	protected String buildNameString() {
		// TODO Auto-generated method stub
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(target.toString());
		sbuf.append(SelectorConstants.PERIOD);
		sbuf.append(function.toString());
		return sbuf.toString();
	}

	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object o = target.operate(entityMap);
		if (o == null)
			return null;
		Object[] parameterObjects = getParameterObjects(entityMap);
		if(!(o instanceof OperandContextList)) {
			return operate(o, parameterObjects);
		} else {
			OperandContextList ctxList = (OperandContextList)o;
			OperandContextList resultList = new OperandContextArrayList(ctxList.size());
			for(Object obj : ctxList) {
				Object ret = operate(obj, parameterObjects);
				resultList.add(ret);
			}
			return resultList;
		}
		
	}
	
	protected Object operate(Object o, Object[] parameterObjects) {
		Method m = getMethod(o, parameterObjects);
		try {
			return m.invoke(o, parameterObjects);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new OperateException(StringFormater.format(
					"Invoke method '{}' in class '{}' failed!", function.getName(), o.getClass().getName()),e);
		}
	}
	
	protected Method getMethod(Object targetObject, Object[] parameterObjects) {
		Class<?> objClazz = targetObject.getClass();
		if (clazz != null 
				&& objClazz.equals(clazz)) {
			return method;
		}
		Method m = methodCache.get(objClazz);
		if (m == null) {
			Class<?>[] parameterTypes = getParameterTypes(parameterObjects);
			try {
				m = objClazz.getMethod(function.getName(), parameterTypes);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new OperateException(StringFormater.format(
						"Get method '{}' from class '{}' failed!", function.getName(), objClazz.getName()), e);
			}
			methodCache.put(objClazz, m);
			clazz = objClazz;
			method = m;
		}
		return m;
	}
	
	protected Object[] getParameterObjects(EntityMap entityMap) {
		List<Operand> parameters = function.getParameters();
		Object[] parameterObjects = new Object[parameters.size()];
		int i = 0;
		for(Operand param : parameters) {
			Object o = param.operate(entityMap);
			parameterObjects[i++] = o; 
		}
		return parameterObjects;
	}

	protected Class<?>[] getParameterTypes(Object[] parameterObjects) {
		Class<?>[] parameterTypes = new Class[parameterObjects.length];
		for(int i = 0; i < parameterObjects.length; i++) {
			parameterTypes[i] = parameterObjects[i].getClass();
		}
		return parameterTypes;
	}

	@Override
	public Operand getTarget() {
		// TODO Auto-generated method stub
		return target;
	}

	/* (non-Javadoc)
	 * @see org.moql.operand.expression.member.MemberOperand#getMember()
	 */
	@Override
	public Operand getMember() {
		// TODO Auto-generated method stub
		return function;
	}

}
