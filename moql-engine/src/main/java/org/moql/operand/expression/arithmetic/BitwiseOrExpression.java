/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moql.operand.expression.arithmetic;

import org.moql.Operand;
import org.moql.operand.constant.ConstantType;

/**
 * 
 * @author Tang Tadin
 *
 */
public class BitwiseOrExpression extends AbstractArithmeticExpression {

	public BitwiseOrExpression(Operand operand, Operand operand2) {
		super(ArithmeticOperator.BITWISEOR, operand, operand2);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Object calc(Number number, Number number2, ConstantType returnType) {
		// TODO Auto-generated method stub
		long ret = number.longValue() | number2.longValue();
		return new Long(ret);
	}

}
