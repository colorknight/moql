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
public class Column extends AbstractFunction {

  public static final String FUNCTION_NAME = "column";

  protected String colName;

  protected int inx = -1;

  public Column(List<Operand> parameters) {
    super(FUNCTION_NAME, 1, parameters);
    // TODO Auto-generated constructor stub
    colName = parameters.get(0).operate((EntityMap) null).toString();
  }

  /* (non-Javadoc)
   * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
   */
  @Override
  protected Object innerOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return entityMap.getEntity(colName);
  }

  @Override
  public void bind(String[] entityNames) {
    for (int i = 0; i < entityNames.length; i++) {
      if (entityNames[i].equals(colName))
        inx = i;
    }
    this.binded = true;
  }

  @Override
  protected Object innerOperate(Object[] entityArray) {
    if (inx != -1)
      return entityArray[inx];
    return null;
  }
}
