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

import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.util.StringFormater;

import java.lang.reflect.Array;

/**
 * @author Tang Tadin
 */
public class SystemArrayAccessor implements ArrayAccessor {

  @Override
  public Object getObject(Object array, Object index) {
    // TODO Auto-generated method stub
    if (index instanceof Number) {
      return Array.get(array, ((Number) index).intValue());
    }
    //		if (index.getClass().equals(String.class)) {
    //			return Array.get(array, Integer.valueOf((String)index));
    //		}
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      return Array.get(array, inx.intValue());
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public void setObject(Object array, Object index, Object value) {
    // TODO Auto-generated method stub
    if (index instanceof Number) {
      Array.set(array, ((Number) index).intValue(), value);
      return;
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      Array.set(array, inx.intValue(), value);
      return;
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public OperandContextList toOperandContextList(Object array) {
    // TODO Auto-generated method stub
    OperandContextList ctxList = new OperandContextArrayList(
        Array.getLength(array));
    for (int i = 0; i < Array.getLength(array); i++) {
      ctxList.add(Array.get(array, i));
    }
    return ctxList;
  }

}
