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
package org.datayoo.moql.operand.expression.bit;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.Operand;
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.operand.expression.OperationExpression;
import org.datayoo.moql.operand.expression.arithmetic.*;
import org.datayoo.moql.util.StringFormater;

/**
 * 
 * @author Tang Tadin
 *
 */
public class BitwiseExpressionFactory {
	
	public static OperationExpression createBitwiseExpression(
			String operator, Operand lOperand, Operand rOperand) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");
		if (operator.length() > 2) {
			throw new IllegalArgumentException(
					StringFormater.format("Operator '{}' is invalid!", operator));
		}
		Validate.notNull(rOperand, "Parameter 'rOperand' is null!");

		if (operator.equals(SelectorConstants.SWANGDASH))
			return new BitwiseNotExpression(rOperand);
		if (operator.equals(SelectorConstants.AMPERSAND))
			return new BitwiseAndExpression(lOperand, rOperand);
		else if (operator.equals(SelectorConstants.VERTICAL))
			return new BitwiseOrExpression(lOperand, rOperand);
		else if (operator.equals(SelectorConstants.CIRCUMFLEX))
			return new BitwiseXorExpression(lOperand, rOperand);
		else if (operator.equals(SelectorConstants.LSHIFT))
			return new LeftShiftExpression(lOperand, rOperand);
		else if (operator.equals(SelectorConstants.RSHIFT))
			return new RightShiftExpression(lOperand, rOperand);
		throw new IllegalArgumentException(StringFormater.format("Unsuppored operator '{}'!", operator));
	}
	
}
