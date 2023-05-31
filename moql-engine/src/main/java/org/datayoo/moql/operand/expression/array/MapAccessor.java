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

import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;

import java.util.Map;

/**
 * @author Tang Tadin
 */
@SuppressWarnings("unchecked")
public class MapAccessor implements ArrayAccessor {

  @Override
  public Object getObject(Object array, Object index) {
    // TODO Auto-generated method stub
    Map<Object, Object> map = (Map<Object, Object>) array;
    return map.get(index);
  }

  @Override
  public void setObject(Object array, Object index, Object value) {
    Map<Object, Object> map = (Map<Object, Object>) array;
    map.put(index, value);
  }

  @Override
  public Object removeObject(Object array, Object value) {
    Map<Object, Object> map = (Map<Object, Object>) array;
    map.remove(value);
    return map;
  }

  @Override
  public OperandContextList toOperandContextList(Object array) {
    // TODO Auto-generated method stub
    Map<Object, Object> map = (Map<Object, Object>) array;
    return new OperandContextArrayList(map.values());
  }

  @Override
  public int getSize(Object array) {
    Map<Object, Object> map = (Map<Object, Object>) array;
    return map.size();
  }
}
