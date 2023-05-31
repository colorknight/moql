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
package org.datayoo.moql.operand.function;

import org.datayoo.moql.*;
import org.datayoo.moql.engine.MoqlEngine;
import org.datayoo.moql.operand.constant.StringConstant;
import org.datayoo.moql.operand.expression.array.ArrayAccessor;
import org.datayoo.moql.operand.expression.array.ArrayExpressionUtils;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class ElementMatch extends AbstractFunction {
  // 元素匹配
  public static final String FUNCTION_NAME = "elementMatch";

  protected Operand operand;

  protected Filter filter;

  public ElementMatch(List<Operand> parameters) {
    super(FUNCTION_NAME, 2, parameters);
    operand = parameters.get(0);
    if (!(parameters.get(1) instanceof StringConstant)) {
      throw new UnsupportedOperationException(
          "The separator parameter should be a string constant in 'elementMatch' function!");
    }
    String condition = (String) parameters.get(1).operate((EntityMap) null);
    try {
      filter = MoqlEngine.createFilter(condition);
    } catch (MoqlException e) {
      throw new IllegalArgumentException("Create filter failed!", e);
    }
  }

  /* (non-Javadoc)
   * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
   */
  @Override
  protected Object innerOperate(EntityMap entityMap) {
    Object obj = operand.operate(entityMap);
    return innerOperateProc(entityMap, obj);
  }

  protected Object innerOperateProc(EntityMap entityMap, Object obj) {
    if (obj == null)
      return null;
    EntityMap temp = new EntityMapImpl(entityMap);
    try {
      ArrayAccessor arrayAccessor = ArrayExpressionUtils.getArrayAccessor(obj);
      for (int i = 0; i < arrayAccessor.getSize(obj); i++) {
        Object e = arrayAccessor.getObject(obj, i);
        temp.putEntity("e", e);
        if (filter.isMatch(temp))
          return true;
      }
    } catch (Throwable t) {
      temp.putEntity("e", obj);
      if (filter.isMatch(temp))
        return true;
    }
    return false;
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    Object obj = operand.operate(entityArray);
    return innerOperateProc(entityMap, obj);
  }
}
