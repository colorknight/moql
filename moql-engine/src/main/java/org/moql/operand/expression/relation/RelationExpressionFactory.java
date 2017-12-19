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
package org.moql.operand.expression.relation;

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
public abstract class RelationExpressionFactory {
	
	public static OperationExpression createRelationExpression(String operator, Operand lOperand, Operand rOperand) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");
		
		if (operator.equalsIgnoreCase(SelectorConstants.EQ)) {
			return new EqualExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.LT)) {
			return new LittleThanExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.GT)) {
			return new GreatThanExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.LE)) {
			return new LittleAndEqualExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.GE)) {
			return new GreatAndEqualExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.NE)) {
			return new NotEqualExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.BETWEEN)) {
			return new BetweenExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.IN)) {
			return new InExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.LIKE)) {
			return new LikeExpression(lOperand, rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.IS)) {
			return new IsExpression(lOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.EXISTS)) {
			return new ExistsExpression(rOperand);
		} else if (operator.equalsIgnoreCase(SelectorConstants.EXPR)) {
			return new OperandExpression(rOperand);
		}
		throw new IllegalArgumentException(StringFormater.format("Unsuppored operator '{}'!", operator));
	}
	
	public static boolean isUnary(String operator) {
		Validate.notEmpty(operator, "Parameter 'operator' is empty!");

		if (operator.equalsIgnoreCase(SelectorConstants.EXISTS)) {
			return true;
		} else if (operator.equalsIgnoreCase(SelectorConstants.EXPR)) {
			return true;
		}
		return false;
	}
}
