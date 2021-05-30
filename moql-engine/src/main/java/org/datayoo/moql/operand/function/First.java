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

import java.util.List;

/**
 *
 * @author Tang Tadin
 *
 */
public class First extends AggregationFunction {

  public static final String FUNCTION_NAME = "first";

  protected Operand operand;

  protected Object first;

  protected boolean settedValue = false;

  public First(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
  }

  @Override
  public void increment(EntityMap entityMap) {
    // TODO Auto-generated method stub
    if (!settedValue) {
      first = parameters.get(0).operate(entityMap);
      settedValue = true;
    }
  }

  @Override
  public Object getValue() {
    return first;
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    first = null;
  }

}
