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
package org.datayoo.moql.operand.expression.relation;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.Operand;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.expression.RangeExpression;
import org.datayoo.moql.util.CompareHelper;

/**
 * 
 * @author Tang Tadin
 *
 */
public class InRangeExpression extends AbstractRelationExpression {

	protected boolean lClosure = false;

	protected boolean rClosure = false;

	protected Operand lRange;

	protected Operand rRange;

	public InRangeExpression(Operand lOperand, Operand rOperand) {
		super(OperatorType.BINARY, RelationOperator.IN, lOperand, rOperand);
		// TODO Auto-generated constructor stub
		if (!(rOperand instanceof RangeExpression)) {
			throw new IllegalArgumentException("Invalid right operand!");
		}
		RangeExpression rangeExpression = (RangeExpression)rOperand;
		lClosure = rangeExpression.islClosure();
		rClosure = rangeExpression.isrClosure();
		lRange = rangeExpression.getOperands().get(0);
		rRange = rangeExpression.getOperands().get(1);
	}

	@Override
	public boolean booleanOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object lValue = lOperand.operate(entityMap);
		if (lValue == null) {
			return false;
		}
		Object lObj = lRange.operate(entityMap);
		int ret = CompareHelper.compare(lValue, lObj);
		if (ret < 0 || ret == 0 && !lClosure)
			return false;
		Object rObj = rRange.operate(entityMap);
		if (ret > 0 || ret == 0 && !rClosure)
			return false;
		return true;
	}

	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		return booleanOperate(entityMap);
	}

}
