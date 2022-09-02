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
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.Operand;
import org.datayoo.moql.util.StringFormater;

import java.math.BigDecimal;
import java.util.List;

public abstract class AggregationFunction extends AbstractFunction {

  {
    functionType = FunctionType.AGGREGATE;
  }

  protected AggregationFunction(String name, int parameterCount,
      List<Operand> parameters) {
    super(name, parameterCount, parameters);
    // TODO Auto-generated constructor stub
  }

  public AggregationFunction(String name, List<Operand> parameters) {
    super(name, parameters);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected Object innerOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    increment(entityMap);
    return getValue();
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    increment(entityArray);
    return getValue();
  }

  protected Number toNumber(Object obj) {
    if (obj instanceof Number)
      return (Number) obj;
    if (obj instanceof NumberConvertable) {
      return ((NumberConvertable) obj).toNumber();
    }
    throw new IllegalArgumentException(StringFormater.format(
        "Object '{}' of class '{}' can not cast to number!", obj.toString(),
        obj.getClass().getName()));
  }

  protected BigDecimal toBigDecimal(Object obj) {
    if (obj instanceof BigDecimal)
      return (BigDecimal) obj;
    Number number = toNumber(obj);
    return new BigDecimal(number.toString());
  }

  public abstract void increment(EntityMap entityMap);

  public abstract void increment(Object[] entityArray);

  public abstract Object getValue();

  public abstract void clear();

}
