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
package org.datayoo.moql.operand.expression.bit;

import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.constant.ConstantType;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.arithmetic.AbstractArithmeticExpression;
import org.datayoo.moql.operand.expression.arithmetic.ArithmeticOperator;

/**
 *
 * @author Tang Tadin
 *
 */
public class BitwiseXorExpression extends AbstractBitwiseExpression {

  public BitwiseXorExpression(Operand lOperand, Operand rOperand) {
    super(BitwiseOperator.BITWISEXOR, lOperand, rOperand);
    // TODO Auto-generated constructor stub
  }

  @Override protected Long calc(Long lNumber, Long rNumber) {
    // TODO Auto-generated method stub
    long ret = lNumber.longValue() ^ rNumber.longValue();
    return new Long(ret);
  }

}
