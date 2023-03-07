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
package org.datayoo.moql.operand.expression.array;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.operand.expression.AbstractExpression;
import org.datayoo.moql.operand.expression.ExpressionType;

/**
 * @author Tang Tadin
 */
public class ArrayExpression extends AbstractExpression {

  protected Operand array;

  protected Operand index;

  {
    expressionType = ExpressionType.ARRAY;
  }

  public ArrayExpression(Operand array, Operand index) {
    // TODO Auto-generated constructor stub
    Validate.notNull(array, "array is null!");
    this.array = array;
    this.index = index;

    name = buildNameString();
  }

  protected String buildNameString() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(array.toString());
    sbuf.append(SelectorConstants.LBRACKET);
    if (index != null)
      sbuf.append(index.toString());
    sbuf.append(SelectorConstants.RBRACKET);
    return sbuf.toString();
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object o = array.operate(entityMap);
    if (o == null)
      return null;
    ArrayAccessor arrayAccessor = ArrayExpressionUtils.getArrayAccessor(o);
    if (index != null) {
      Object inx = index.operate(entityMap);
      return arrayAccessor.getObject(o, inx);
    } else {
      return arrayAccessor.toOperandContextList(o);
    }
  }

  @Override
  public void bind(String[] entityNames) {
    array.bind(entityNames);
    if (index != null)
      index.bind(entityNames);
    this.binded = true;
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object o = array.operate(entityArray);
    if (o == null)
      return null;
    ArrayAccessor arrayAccessor = ArrayExpressionUtils.getArrayAccessor(o);
    if (index != null) {
      Object inx = index.operate(entityArray);
      return arrayAccessor.getObject(o, inx);
    } else {
      return arrayAccessor.toOperandContextList(o);
    }
  }

  public Operand getArray() {
    return array;
  }

  public Operand getIndex() {
    return index;
  }

  @Override
  public Operand setValue(Object[] entityArray, Object value) {
    Object o = array.operate(entityArray);
    if (o == null)
      return null;
    ArrayAccessor arrayAccessor = ArrayExpressionUtils.getArrayAccessor(o);
    if (index != null) {
      Object inx = index.operate(entityArray);
      arrayAccessor.setObject(o, inx, value);
    } else {
      throw new UnsupportedOperationException(
          "The operand unsupport set value!");
    }
    return this;
  }

  @Override
  public Operand setValue(EntityMap entityMap, Object value) {
    Object o = array.operate(entityMap);
    if (o == null)
      return null;
    ArrayAccessor arrayAccessor = ArrayExpressionUtils.getArrayAccessor(o);
    if (index != null) {
      Object inx = index.operate(entityMap);
      arrayAccessor.setObject(o, inx, value);
    } else {
      throw new UnsupportedOperationException(
          "The operand unsupport set value!");
    }
    return this;
  }
}
