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
package org.datayoo.moql.operand.expression.relation;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.constant.NullConstant;

/**
 *
 * @author Tang Tadin
 *
 */
public class IsExpression extends AbstractRelationExpression {

  protected static NullConstant NULL = new NullConstant();

  public IsExpression(Operand operand) {
    super(OperatorType.BINARY, RelationOperator.IS, operand, NULL);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see org.moql.operand.Operand#operate(org.moql.data.EntityMap)
   */
  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return booleanOperate(entityMap);
  }

  @Override
  public boolean booleanOperate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    Object value = lOperand.operate(entityMap);
    if (value == null)
      return true;
    return false;
  }

  @Override
  public Object operate(Object[] entityArray) {
    Object value = lOperand.operate(entityArray);
    if (value == null)
      return true;
    return false;
  }
}
