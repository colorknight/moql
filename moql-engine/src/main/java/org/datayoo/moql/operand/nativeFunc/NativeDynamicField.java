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
package org.datayoo.moql.operand.nativeFunc;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.OperateException;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.operand.expression.member.MemberVisitor;
import org.datayoo.moql.operand.function.AbstractFunction;
import org.datayoo.moql.util.StringFormater;
import org.dom4j.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class NativeDynamicField extends AbstractNativeFunction {

  public static final String FUNCTION_NAME = "_fe";

  protected Operand operand;

  public NativeDynamicField(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
    constantReturn = false;
    Operand param = parameters.get(0);
    String exp = (String) param.operate((EntityMap) null);
    try {
      operand = MoqlEngine.createOperand(exp);
    } catch (MoqlException e) {
      throw new IllegalArgumentException("parameter is invalid!", e);
    }
  }

  @Override
  protected Object innerOperate(EntityMap entityMap) {
    Object o = operand.operate(entityMap);
    if (!(o instanceof String))
      throw new IllegalArgumentException(
          StringFormater.format("The value of '{}' is not a string!",
              operand.toString()));
    return getField((String) o);
  }

  protected Object getField(String field) {
    if (target instanceof Map) {
      return operate((Map) target, field);
    }
    if (target instanceof JsonObject) {  // modified 2017/02/05
      return operate((JsonObject) target, field);
    }
    if (target instanceof Element) {
      return operate((Element) target, field);
    }
    return operate(target, field);
  }

  protected Object operate(Map map, String field) {
    return map.get(field);
  }

  // added 2017/02/05
  protected Object operate(JsonObject jsonObject, String field) {
    JsonElement value = jsonObject.get(field);
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
  protected Object operate(Element element, String field) {
    List list = element.elements(field);
    if (list == null)
      return null;
    if (list.size() > 1)
      return list;
    return list.get(0);
  }

  protected Object operate(Object o, String field) {
    if (o.getClass().isArray()) {
      return operateArray(o, field);
    }
    Field f = getField(o, field);
    try {
      return f.get(o);
    } catch (Exception e) {
      throw new OperateException(
          StringFormater.format("Invoke field '{}' in class '{}' failed!",
              field, o.getClass().getName()), e);
    }
  }

  protected Object operateArray(Object o, String field) {
    if (field.equals("length")) {
      return Array.getLength(o);
    }
    throw new UnsupportedOperationException("");
  }

  protected Field getField(Object targetObject, String field) {
    Class<?> objClazz = targetObject.getClass();
    try {
      Field f = objClazz.getDeclaredField(field);
      f.setAccessible(true);
      return f;
    } catch (Exception e) {
      throw new OperateException(
          StringFormater.format("Get field '{}' from class '{}' failed!", field,
              objClazz.getName()), e);
    }
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    Object o = operand.operate(entityArray);
    if (!(o instanceof String))
      throw new IllegalArgumentException(
          StringFormater.format("The value of '{}' is not a string!"));
    return getField((String) o);
  }
}
