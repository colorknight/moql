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

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.OperateException;
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.operand.expression.AbstractExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.function.Function;
import org.datayoo.moql.operand.nativeFunc.AbstractNativeFunction;
import org.datayoo.moql.util.StringFormater;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Tang Tadin
 */
public class MemberFunctionExpression extends AbstractExpression
    implements MemberExpression {

  protected Function function;

  protected Operand target;

  protected Class<?> clazz;

  protected Method method;

  protected Map<Class<?>, Method> methodCache = new HashMap<Class<?>, Method>();

  protected boolean nativeFunction = false;

  {
    expressionType = ExpressionType.MEMBER;
  }

  public MemberFunctionExpression(Operand target, Function function) {
    // TODO Auto-generated constructor stub
    Validate.notNull(target, "Parameter 'target' is null!");
    Validate.notNull(function, "Parameter 'function' is null!");

    this.target = target;
    this.function = function;
    if (function instanceof AbstractNativeFunction) {
      nativeFunction = true;
    }
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
    if (!nativeFunction) {
      Object[] parameterObjects = getParameterObjects(entityMap);
      return operateProc(o, parameterObjects);
    } else {
      AbstractNativeFunction anf = (AbstractNativeFunction) function;
      anf.setTarget(o);
      return anf.operate(entityMap);
    }
  }

  protected Object operateProc(Object o, Object[] parameterObjects) {
    if (!(o instanceof OperandContextList)) {
      return operate(o, parameterObjects);
    } else {
      OperandContextList ctxList = (OperandContextList) o;
      OperandContextList resultList = new OperandContextArrayList(
          ctxList.size());
      for (Object obj : ctxList) {
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
      throw new OperateException(
          StringFormater.format("Invoke method '{}' in class '{}' failed!",
              function.getName(), o.getClass().getName()), e);
    }
  }

  protected Method getMethod(Object targetObject, Object[] parameterObjects) {
    Class<?> objClazz = targetObject.getClass();
    if (clazz != null && objClazz.equals(clazz)) {
      return method;
    }
    Method m = methodCache.get(objClazz);
    if (m == null) {
      Class<?>[] parameterTypes = getParameterTypes(parameterObjects);
      try {
        m = objClazz.getMethod(function.getName(), parameterTypes);
      } catch (Exception e) {
        m = derivationMethod(objClazz, function.getName(), parameterTypes);
        if (m == null) {
          throw new OperateException(
              StringFormater.format("Get method '{}' from class '{}' failed!",
                  function.getName(), objClazz.getName()), e);
        }
      }
      methodCache.put(objClazz, m);
      clazz = objClazz;
      method = m;
    }
    return m;
  }

  protected Method derivationMethod(Class<?> objClazz, String methodName,
      Class<?>[] parameterTypes) {
    List<Method> methods = getMethods(objClazz, methodName,
        parameterTypes.length);
    methods.sort(new Comparator<Method>() {
      @Override
      public int compare(Method o1, Method o2) {
        Class[] o1ps = o1.getParameterTypes();
        Class[] o2ps = o2.getParameterTypes();
        for (int i = 0; i < o1ps.length; i++) {
          if (o1ps[i].isAssignableFrom(o2ps[i])) {
            return -1;
          } else if (o2ps[i].isAssignableFrom(o1ps[i])) {
            return 1;
          }
        }
        return 0;
      }
    });
    return getMethod(methods, parameterTypes);
  }

  protected Method getMethod(List<Method> methods, Class<?>[] parameterTypes) {
    if (methods.size() == 1)
      return methods.get(0);
    for (Method method : methods) {
      Class[] pts = method.getParameterTypes();
      boolean matched = true;
      for (int i = 0; i < pts.length; i++) {
        if (pts[i].isAssignableFrom(parameterTypes[i])) {
          continue;
        }
        matched = false;
        break;
      }
      if (matched)
        return method;
    }
    return null;
  }

  protected List<Method> getMethods(Class<?> objClazz, String methodName,
      int paramCount) {
    List<Method> methods = new LinkedList<>();
    for (Method method : objClazz.getMethods()) {
      if (method.getName().equals(methodName)
          && method.getParameters().length == paramCount) {
        methods.add(method);
      }
    }
    return methods;
  }

  protected Object[] getParameterObjects(EntityMap entityMap) {
    List<Operand> parameters = function.getParameters();
    Object[] parameterObjects = new Object[parameters.size()];
    int i = 0;
    for (Operand param : parameters) {
      Object o = param.operate(entityMap);
      parameterObjects[i++] = o;
    }
    return parameterObjects;
  }

  protected Class<?>[] getParameterTypes(Object[] parameterObjects) {
    Class<?>[] parameterTypes = new Class[parameterObjects.length];
    for (int i = 0; i < parameterObjects.length; i++) {
      if (parameterObjects[i] != null)
        parameterTypes[i] = parameterObjects[i].getClass();
      else
        parameterTypes[i] = Object.class;
      parameterTypes[i] = adjustPrimitiveType(parameterTypes[i]);
    }
    return parameterTypes;
  }

  protected Class<?> adjustPrimitiveType(Class<?> clazz) {
    String name = clazz.getName();
    if (name.equals(Integer.class.getName())) {
      return Integer.TYPE;
    } else if (name.equals(Long.class.getName())) {
      return Long.TYPE;
    } else if (name.equals(Short.class.getName())) {
      return Short.TYPE;
    } else if (name.equals(Byte.class.getName())) {
      return Byte.TYPE;
    } else if (name.equals(Character.class.getName())) {
      return Character.TYPE;
    } else if (name.equals(Double.class.getName())) {
      return Double.TYPE;
    } else if (name.equals(Float.class.getName())) {
      return Float.TYPE;
    } else if (name.equals(Boolean.class.getName())) {
      return Boolean.TYPE;
    }
    return clazz;
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

  @Override
  public void bind(String[] entityNames) {
    target.bind(entityNames);
    function.bind(entityNames);
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object o = target.operate(entityArray);
    if (o == null)
      return null;
    Object[] parameterObjects = getParameterObjects(entityArray);
    return operateProc(o, parameterObjects);
  }

  protected Object[] getParameterObjects(Object[] entityArray) {
    List<Operand> parameters = function.getParameters();
    Object[] parameterObjects = new Object[parameters.size()];
    int i = 0;
    for (Operand param : parameters) {
      Object o = param.operate(entityArray);
      parameterObjects[i++] = o;
    }
    return parameterObjects;
  }
}
