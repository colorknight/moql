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
package org.datayoo.moql.operand.expression.logic;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.Operand;
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.operand.expression.OperationExpression;
import org.datayoo.moql.util.StringFormater;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class LogicExpressionFactory {
	
	public static OperationExpression createLogicExpression(String operator, Operand lOperand, Operand rOperand) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");
		
		if (operator.equalsIgnoreCase(SelectorConstants.AND)) {
			return new AndExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.OR)) {
			return new OrExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.NOT)){
			return new NotExpression(rOperand);
		}
		throw new IllegalArgumentException(
				StringFormater.format("Unsuppored operator '{}'!", operator));
	}
	
	public static boolean isUnary(String operator) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");
		if (operator.equalsIgnoreCase(SelectorConstants.NOT)) {
			return true;
		} 
		return false;
	}
}
