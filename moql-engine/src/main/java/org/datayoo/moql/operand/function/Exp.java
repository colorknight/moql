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
public class Exp extends AbstractFunction {
  // e的幂运算
  public static final String FUNCTION_NAME = "exp";

  protected Operand operand;

  public Exp(List<Operand> parameters) {
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
    double val = 0;
    if (obj instanceof Number) {
      Number num = (Number) obj;
      val = num.doubleValue();
    } else {
      val = Double.valueOf(obj.toString());
    }
    return Math.exp(val);
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    Object obj = operand.operate(entityArray);
    return innerOperateProc(obj);
  }
}
