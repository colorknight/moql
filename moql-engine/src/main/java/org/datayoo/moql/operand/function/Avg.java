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

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tang Tadin
 */
public class Avg extends AggregationFunction {

  public static final String FUNCTION_NAME = "avg";

  protected Operand operand;

  protected BigDecimal sum = new BigDecimal(0);

  protected long count = 0;

  public Avg(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
    // TODO Auto-generated constructor stub
    operand = parameters.get(0);
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object obj = operand.operate(entityMap);
    count++;
    if (obj == null) {
      return;
    }
    BigDecimal num = toBigDecimal(obj);
    sum = sum.add(num);
  }

  @Override
  public void increment(Object[] entityArray) {
    // TODO Auto-generated method stub
    Object obj = operand.operate(entityArray);
    count++;
    if (obj == null) {
      return;
    }
    BigDecimal num = toBigDecimal(obj);
    sum = sum.add(num);
  }

  @Override
  public Object getValue() {
    return sum.divide(new BigDecimal(count), 6, BigDecimal.ROUND_HALF_UP)
        .doubleValue();
  }

  @Override
  public synchronized void clear() {
    // TODO Auto-generated method stub
    sum = new BigDecimal(0);
    count = 0;
  }

}
