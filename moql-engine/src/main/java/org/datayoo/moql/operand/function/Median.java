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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Tang Tadin
 *
 */
public class Median extends AggregationFunction {

  public static final String FUNCTION_NAME = "median";

  protected Operand operand;

  protected org.apache.commons.math3.stat.descriptive.rank.Median median = new org.apache.commons.math3.stat.descriptive.rank.Median();
  protected Set<Double> numbers = new HashSet<Double>();

  public Median(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
    // TODO Auto-generated constructor stub
    operand = parameters.get(0);
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object obj = operand.operate(entityMap);
    if (obj == null) {
      return;
    }
    Number num = toNumber(obj);
    numbers.add(num.doubleValue());
  }

  @Override
  public void increment(Object[] entityArray) {
    // TODO Auto-generated method stub
    Object obj = operand.operate(entityArray);
    if (obj == null) {
      return;
    }
    Number num = toNumber(obj);
    numbers.add(num.doubleValue());
  }

  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    double[] values = new double[numbers.size()];
    int i = 0;
    for (Double num : numbers) {
      values[i++] = num;
    }
    return median.evaluate(values);
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    numbers.clear();
  }

}
