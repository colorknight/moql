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
 *
 * @author Tang Tadin
 *
 */
public class MemberFunction extends AbstractFunction {

  public MemberFunction(String name, List<Operand> parameters) {
    super(name, parameters);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected boolean determineConstantsReturn(List<Operand> parameters) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected Object innerOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    throw new UnsupportedOperationException();
  }
}
