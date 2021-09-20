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
import org.datayoo.moql.util.StringFormater;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class Count extends AggregationFunction {

  public static final String FUNCTION_NAME = "count";

  protected long count = 0;

  protected Operand operand = null;

  protected boolean distinct = false;

  protected Set<Object> cache = new HashSet<Object>();

  public Count(List<Operand> parameters) {
    super(FUNCTION_NAME, VARIANT_PARAMETERS, parameters);
    // TODO Auto-generated constructor stub
    if (this.parameters.size() > 2)
      throw new IllegalArgumentException(
          StringFormater.format("Function '{}' need 1 or 2 parameters!", name));
    if (parameters.size() == 2) {
      Object object = parameters.get(1).operate((EntityMap) null);
      if (object instanceof Boolean) {
        distinct = ((Boolean) (object)).booleanValue();
      } else {
        throw new IllegalArgumentException(
            "Parameter 'distinct' should be 'Boolean' type!");
      }
      if (distinct)
        operand = parameters.get(0);
    }
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (!distinct)
      count++;
    else {
      Object value = operand.operate(entityMap);
      if (cache.add(value)) {
        count++;
      }
    }
  }

  @Override
  public void increment(Object[] entityArray) {
    // TODO Auto-generated method stub
    if (!distinct)
      count++;
    else {
      Object value = operand.operate(entityArray);
      if (cache.add(value)) {
        count++;
      }
    }
  }

  @Override
  public Object getValue() {
    // TODO Auto-generated method stub
    return new Long(count);
  }

  public boolean isDistinct() {
    return distinct;
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    count = 0;
    cache.clear();
  }

}
