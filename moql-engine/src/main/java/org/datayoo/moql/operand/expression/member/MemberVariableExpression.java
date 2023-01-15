/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.operand.expression.member;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.OperateException;
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.operand.expression.AbstractExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.variable.Variable;
import org.datayoo.moql.util.StringFormater;
import org.dom4j.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class MemberVariableExpression extends AbstractExpression
    implements MemberExpression {

  protected Variable variable;

  protected Operand target;

  protected Class<?> clazz;

  protected Field field;

  protected Map<Class<?>, Field> fieldCache = new HashMap<Class<?>, Field>();

  protected Set<MemberVisitor> memberVisitors = null;

  {
    expressionType = ExpressionType.MEMBER;
  }

  public MemberVariableExpression(Operand target, Variable variable) {
    this(target, variable, null);
  }

  public MemberVariableExpression(Operand target, Variable variable,
      Set<MemberVisitor> memberVisitors) {
    Validate.notNull(target, "Parameter 'target' is null!");
    Validate.notNull(variable, "Parameter 'variable' is null!");

    this.target = target;
    this.variable = variable;

    name = buildNameString();
    this.memberVisitors = memberVisitors;
  }

  protected String buildNameString() {
    // TODO Auto-generated method stub
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(target.toString());
    sbuf.append(SelectorConstants.PERIOD);
    sbuf.append(variable.toString());
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
  @SuppressWarnings({ "rawtypes" })
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object o = target.operate(entityMap);
    return operateProc(o);
  }

  protected Object operateProc(Object o) {
    if (o == null)
      return null;
    if (!(o instanceof OperandContextList)) {
      if (o instanceof Map) {
        return operate((Map) o);
      }
      if (o instanceof JsonObject) {  // modified 2017/02/05
        return operate((JsonObject) o);
      }
      if (o instanceof Element) {
        return operate((Element) o);
      }
      MemberVisitor memberVisitor = getVisitor(o);
      if (memberVisitor != null) {
        String name = variable.getName();
        return memberVisitor.operate(o, name);
      }
      return operate(o);
    } else {
      OperandContextList ctxList = (OperandContextList) o;
      OperandContextList resultList = new OperandContextArrayList(
          ctxList.size());
      for (Object obj : ctxList) {
        Object ret = operate(obj);
        resultList.add(ret);
      }
      return resultList;
    }
  }

  @SuppressWarnings({ "rawtypes" })
  protected Object operate(Map map) {
    String name = variable.getName();
    return map.get(name);
  }

  // added 2017/02/05
  protected Object operate(JsonObject jsonObject) {
    String name = variable.getName();
    JsonElement value = jsonObject.get(name);
    if (value == null || value.isJsonNull())
      return null;
    if (value instanceof JsonPrimitive) {
      JsonPrimitive jp = (JsonPrimitive) value;
      if (jp.isString())
        return jp.getAsString();
      if (jp.isBoolean())
        return jp.getAsBoolean();
      if (jp.isNumber()) {
        return getNumber(jp);
      }
    }
    return value;
  }

  protected Object getNumber(JsonPrimitive value) {
    String v = value.getAsString();
    if (v.indexOf('.') == -1)
      return value.getAsLong();
    else
      return value.getAsDouble();
  }

  // added 2017/02/05
  protected Object operate(Element element) {
    String name = variable.getName();
    List list = element.elements(name);
    if (list == null)
      return null;
    if (list.size() > 1)
      return list;
    return list.get(0);
  }

  protected MemberVisitor getVisitor(Object o) {
    if (memberVisitors == null)
      return null;
    for (MemberVisitor memberVisitor : memberVisitors) {
      if (memberVisitor.isVisitable(o))
        return memberVisitor;
    }
    return null;
  }

  protected Object operate(Object o) {
    if (o instanceof Map) {
      return ((Map) o).get(variable.getName());
    }
    if (o.getClass().isArray()) {
      return operateArray(o);
    }
    Field f = getField(o);
    try {
      return f.get(o);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new OperateException(
          StringFormater.format("Invoke field '{}' in class '{}' failed!",
              variable.getName(), o.getClass().getName()), e);
    }
  }

  protected Object operateArray(Object o) {
    if (variable.getName().equals("length")) {
      return Array.getLength(o);
    }
    throw new UnsupportedOperationException("");
  }

  protected Field getField(Object targetObject) {
    Class<?> objClazz = targetObject.getClass();
    if (clazz != null && objClazz.equals(clazz)) {
      return field;
    }
    Field f = fieldCache.get(objClazz);
    if (f == null) {
      try {
        f = objClazz.getDeclaredField(variable.getName());
        f.setAccessible(true);
      } catch (Exception e) {
        throw new OperateException(
            StringFormater.format("Get field '{}' from class '{}' failed!",
                variable.getName(), objClazz.getName()), e);
      }
      fieldCache.put(objClazz, f);
      clazz = objClazz;
      field = f;
    }
    return f;
  }

  @Override
  public void bind(String[] entityNames) {
    target.bind(entityNames);
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object o = target.operate(entityArray);
    return operateProc(o);
  }
}
