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
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.metadata.OperatorType;

/**
 *
 * @author Tang Tadin
 *
 */
// unsupported in moql grammar
public class ExistsExpression extends AbstractRelationExpression {

  public ExistsExpression(Operand operand) {
    super(OperatorType.UNARY, RelationOperator.EXISTS, null, operand);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected String buildNameString() {
    // TODO Auto-generated method stub
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(operator.getOperator());
    sbuf.append(SelectorConstants.LPAREN);
    sbuf.append(rOperand.toString());
    sbuf.append(SelectorConstants.RPAREN);
    return sbuf.toString();
  }

  @Override
  public Object operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    return rOperand.booleanOperate(entityMap);
  }

  @Override
  public Object operate(Object[] entityArray) {
    return isTrue(rOperand.operate(entityArray));
  }
}
