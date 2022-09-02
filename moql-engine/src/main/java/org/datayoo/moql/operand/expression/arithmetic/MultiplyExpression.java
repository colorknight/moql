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
package org.datayoo.moql.operand.expression.arithmetic;

import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.constant.ConstantType;
import org.datayoo.moql.util.ArithmeticUtils;

/**
 * @author Tang Tadin
 */
public class MultiplyExpression extends AbstractArithmeticExpression {

  public MultiplyExpression(Operand operand, Operand operand2) {
    super(ArithmeticOperator.MULTIPLY, operand, operand2);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected Object calc(Number number, Number number2,
      ConstantType returnType) {
    Number ret = ArithmeticUtils.multiply(number, number2);
    return convertReturnValue(ret, returnType);
  }

}
