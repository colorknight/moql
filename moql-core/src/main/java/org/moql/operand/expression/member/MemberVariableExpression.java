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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.Validate;
import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.OperateException;
import org.moql.SelectorConstants;
import org.moql.operand.OperandContextArrayList;
import org.moql.operand.OperandContextList;
import org.moql.operand.expression.AbstractExpression;
import org.moql.operand.expression.ExpressionType;
import org.moql.operand.variable.Variable;
import org.moql.util.StringFormater;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author Tang Tadin
 *
 */
public class MemberVariableExpression extends AbstractExpression implements MemberExpression {
	
	protected Variable variable;

	protected Operand target;
	
	protected Class<?> clazz;
	
	protected Method method;
	
	protected String getter;
	
	protected Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();
	
	{
		expressionType = ExpressionType.MEMBER;
	}
	
	public MemberVariableExpression(Operand target, Variable variable) {
		Validate.notNull(target, "Parameter 'target' is null!");
		Validate.notNull(variable, "Parameter 'variable' is null!");
		
		this.target = target;
		this.variable = variable;
		
		name = buildNameString();
	}
	
	protected String buildNameString() {
		// TODO Auto-generated method stub
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(target.toString());
		sbuf.append(SelectorConstants.PERIOD);
		sbuf.append(variable.toString());
		return sbuf.toString();
	}
	
	protected String buildVariableGetter(boolean booleanType) {
		String name = variable.getName();
		StringBuffer sbuf = new StringBuffer();
		if (booleanType) {
			sbuf.append("is");
		} else 
			sbuf.append("get");
		sbuf.append(Character.toUpperCase(name.charAt(0)));
		sbuf.append(name.substring(1));
		return sbuf.toString();
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
		return variable;
	}

	@Override
	@SuppressWarnings({"rawtypes"})
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object o = target.operate(entityMap);
		if (o == null)
			return null;
		if(!(o instanceof OperandContextList)) {
			if (o instanceof Map) {
				return operate((Map)o);
			} if (o instanceof JsonObject) {	// modified 2017/02/05
				return operate((JsonObject)o);
			}
			return operate(o);
		} else {
			OperandContextList ctxList = (OperandContextList)o;
			OperandContextList resultList = new OperandContextArrayList(ctxList.size());
			for(Object obj : ctxList) {
				Object ret = operate(obj);
				resultList.add(ret);
			}
			return resultList;
		}
	}
	@SuppressWarnings({"rawtypes"})
	protected Object operate(Map map) {
		String name = variable.getName();
		return map.get(name);
	}
	// added 2017/02/05
	protected Object operate(JsonObject jsonObject) {
		String name = variable.getName();
		return jsonObject.get(name);
	}
	
	protected Object operate(Object o) {
		Method m = getMethod(o);
		try {
			return m.invoke(o);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new OperateException(StringFormater.format(
					"Invoke field '{}' in class '{}' failed!", variable.getName(), o.getClass().getName()),e);
		}
	}
	
	protected Method getMethod(Object targetObject) {
		Class<?> objClazz = targetObject.getClass();
		if (clazz != null 
				&& objClazz.equals(clazz)) {
			return method;
		}
		Method m = methodCache.get(objClazz);
		if (m == null) {
			if (getter != null) {
				throw new OperateException(StringFormater.format(
						"Get field '{}' from class '{}' failed!", variable.getName(), objClazz.getName()));
			}
			try {
				getter = buildVariableGetter(false);
				m = objClazz.getMethod(getter, new Class[]{});
			} catch (Exception e) {
				getter = buildVariableGetter(true);
				try {
					m = objClazz.getMethod(getter, new Class[]{});
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					throw new OperateException(StringFormater.format(
							"Get field '{}' from class '{}' failed!", variable.getName(), objClazz.getName()), e1);
				} 
				// TODO Auto-generated catch block
							}
			methodCache.put(objClazz, m);
			clazz = objClazz;
			method = m;
		}
		return m;
	}

}
