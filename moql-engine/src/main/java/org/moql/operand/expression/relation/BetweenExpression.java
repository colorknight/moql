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

import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.SelectorConstants;
import org.moql.metadata.OperatorType;
import org.moql.operand.expression.OperandsExpression;
import org.moql.util.CompareHelper;

import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class BetweenExpression extends AbstractRelationExpression {
	
	protected List<Operand> rOperands;

	public BetweenExpression(Operand lOperand, Operand rOperand) {
		super(OperatorType.BINARY, RelationOperator.BETWEEN, lOperand, rOperand);
		// TODO Auto-generated constructor stub
		if (!(rOperand instanceof OperandsExpression)) {
			throw new IllegalArgumentException("Parameter 'rOperand' is not an OperandsExpression class!");
		}
		rOperands = ((OperandsExpression)rOperand).getOperands();
		if (rOperands.size() != 2) {
			throw new IllegalArgumentException("Parameter 'rOperand' should has 2 operand!");
		}
		name = delayBuildNameString();
	}

	/* (non-Javadoc)
	 * @see org.moql.operand.expression.AbstractOperationExpression#buildNameString()
	 */
	@Override
	protected String buildNameString() {
		// TODO Auto-generated method stub
		return null;
	}

	protected String delayBuildNameString() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(lOperand.toString());
		sbuf.append(SelectorConstants.BLANKSPACE);
		sbuf.append(operator.getOperator());
		sbuf.append(SelectorConstants.BLANKSPACE);
		sbuf.append(rOperands.get(0).toString());
		sbuf.append(SelectorConstants.BLANKSPACE);
		sbuf.append(SelectorConstants.AND);
		sbuf.append(SelectorConstants.BLANKSPACE);
		sbuf.append(rOperands.get(1).toString());
		return sbuf.toString();
	}

	@Override
	public boolean booleanOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object lValue = lOperand.operate(entityMap);
		Object rLValue = rOperands.get(0).operate(entityMap);
		Object rGValue = rOperands.get(1).operate(entityMap);
		if (lValue == null || rLValue == null || rGValue == null)
			return false;
		int ret = CompareHelper.compare(lValue, rLValue);
		if (ret < 0)
			return false;
		ret = CompareHelper.compare(lValue, rGValue);
		return ret < 0? true:false;
	}

	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		return booleanOperate(entityMap);
	}
	
	public List<Operand> getrOperands() {
		return rOperands;
	}

}
