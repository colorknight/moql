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
package org.moql.operand.expression.logic;

import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.metadata.OperatorType;
import org.moql.operand.expression.AbstractOperationExpression;
import org.moql.operand.expression.ExpressionType;

/**
 * 
 * @author Tang Tadin
 *
 */
public class NotExpression extends AbstractOperationExpression {

	public NotExpression(Operand operand) {
		super(OperatorType.UNARY, LogicOperator.NOT, null, operand);
		// TODO Auto-generated constructor stub
		expressionType = ExpressionType.LOGIC;
	}

	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		return booleanOperate(entityMap);
	}

	/* (non-Javadoc)
	 * @see org.moql.operand.AbstractOperand#booleanOperate(org.moql.data.EntityMap)
	 */
	@Override
	public boolean booleanOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		boolean value = rOperand.booleanOperate(entityMap);
		return !value;
	}

}
