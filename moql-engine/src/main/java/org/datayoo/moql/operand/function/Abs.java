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

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class Abs extends AbstractFunction {

  public static final String FUNCTION_NAME = "abs";

  protected Operand operand;

  public Abs(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
    // TODO Auto-generated constructor stub
    operand = parameters.get(0);
  }

  /* (non-Javadoc)
   * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
   */
  @Override
  protected Object innerOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object obj = operand.operate(entityMap);
    return innerOperateProc(obj);
  }

  protected Object innerOperateProc(Object obj) {
    if (obj == null)
      return null;
    if (obj instanceof Double || obj instanceof Float) {
      Number num = (Number) obj;
      double val = num.doubleValue();
      return Math.abs(val);
    } else if (obj instanceof Long || obj instanceof Integer) {
      Number num = (Number) obj;
      long val = num.longValue();
      return Math.abs(val);
    } else {
      try {
        Long val = Long.valueOf(obj.toString());
        return Math.abs(val);
      } catch (NumberFormatException e) {
        Double val = Double.valueOf(obj.toString());
        return Math.abs(val);
      }
    }
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    Object obj = operand.operate(entityArray);
    return innerOperateProc(obj);
  }
}
