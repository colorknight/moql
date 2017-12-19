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

import org.apache.commons.lang.Validate;
import org.moql.Operand;
import org.moql.SelectorConstants;
import org.moql.operand.expression.OperationExpression;
import org.moql.util.StringFormater;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ArithmeticExpressionFactory {
	
	public static OperationExpression createArithmeticExpression( 
			String operator, Operand lOperand, Operand rOperand) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");
		Validate.notNull(lOperand, "Parameter 'lOperand' is null!");
		Validate.notNull(rOperand, "Parameter 'rOperand' is null!");
		if (operator.length() > 1) {
			throw new IllegalArgumentException(StringFormater.format("Operator '' is invalid!", operator));
		}
		char op = operator.charAt(0);
		
		if (op == SelectorConstants.PLUS.charAt(0))
			return new AddExpression(lOperand, rOperand);
		else if (op == SelectorConstants.MINUS.charAt(0))
			return new SubtractExpression(lOperand, rOperand);
		else if (op == SelectorConstants.ASTERRISK.charAt(0))
			return new MultiplyExpression(lOperand, rOperand);
		else if (op == SelectorConstants.SLASH.charAt(0))
			return new DivideExpression(lOperand, rOperand);
		else if (op == SelectorConstants.PERCENT.charAt(0))
			return new ModularExpression(lOperand, rOperand);
		else if (op == SelectorConstants.AMPERSAND.charAt(0))
			return new BitwiseAndExpression(lOperand, rOperand);
		else if (op == SelectorConstants.VERTICAL.charAt(0))
			return new BitwiseOrExpression(lOperand, rOperand);
		else if (op == SelectorConstants.CIRCUMFLEX.charAt(0))
			return new BitwiseXorExpression(lOperand, rOperand);
		throw new IllegalArgumentException(StringFormater.format("Unsuppored operator '{}'!", operator));
	}
	
}
