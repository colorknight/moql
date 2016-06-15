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
package org.moql.operand.expression;

import org.apache.commons.lang.Validate;
import org.moql.Operand;
import org.moql.SelectorConstants;
import org.moql.metadata.OperatorType;

/**
 * @author Tang Tadin
 */
public abstract class AbstractOperationExpression extends AbstractExpression implements OperationExpression {
	
	protected OperatorType operatorType;

	protected OperatorGetter operator;
	
	protected Operand lOperand;
	
	protected Operand rOperand;
	
	public AbstractOperationExpression(OperatorType operatorType, OperatorGetter operator, Operand lOperand, Operand rOperand) {
		Validate.notNull(operatorType, "Parameter 'operatorType' is null!");
		Validate.notNull(operator, "Parameter 'operator' is null!");
		if (operatorType == OperatorType.BINARY) {
			Validate.notNull(lOperand, "Parameter 'lOperand' is null!");
		}
		Validate.notNull(rOperand, "Parameter 'rOperand' is null!");
		
		this.operatorType = operatorType;
		this.operator = operator;
		this.lOperand = lOperand;
		this.rOperand = rOperand;
		name = buildNameString();
	}
	
	protected String buildNameString() {
		StringBuffer sbuf = new StringBuffer();
		if (lOperand != null) {
			sbuf.append(lOperand.toString());
			sbuf.append(SelectorConstants.BLANKSPACE);
			sbuf.append(operator.getOperator());
			sbuf.append(SelectorConstants.BLANKSPACE);
			sbuf.append(rOperand.toString());	
		} else {
			sbuf.append(operator.getOperator());
			sbuf.append(SelectorConstants.BLANKSPACE);
			sbuf.append(SelectorConstants.LPAREN);
			sbuf.append(rOperand.toString());
			sbuf.append(SelectorConstants.RPAREN);
		}
		return sbuf.toString();
	}

	@Override
	public OperatorGetter getOperator() {
		// TODO Auto-generated method stub
		return operator;
	}

	@Override
	public Operand getLeftOperand() {
		// TODO Auto-generated method stub
		return lOperand;
	}

	@Override
	public OperatorType getOperatorType() {
		// TODO Auto-generated method stub
		return operatorType;
	}

	@Override
	public Operand getRightOperand() {
		// TODO Auto-generated method stub
		return rOperand;
	}

}
