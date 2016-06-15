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

import org.moql.SelectorConstants;
import org.moql.operand.expression.OperatorGetter;

public enum RelationOperator implements OperatorGetter {
	EQ,	//	equal
	LT,	//	little than
	GT,	//	great than
	LE,	//	little and equal
	GE,	//	great and equal
	NE,	//	not equal
	BETWEEN,
	IN,
	LIKE,
	IS,
	EXISTS,
	EXPR;
	
	public String getOperator() {
		if (this == EQ)
			return SelectorConstants.EQ;
		if (this == LT)
			return SelectorConstants.LT;
		if (this == GT)
			return SelectorConstants.GT;
		if (this == LE)
			return SelectorConstants.LE;
		if (this == GE)
			return SelectorConstants.GE;
		if (this == NE)
			return SelectorConstants.NE;
		if (this == BETWEEN)
			return SelectorConstants.BETWEEN;
		if (this == IN)
			return SelectorConstants.IN;
		if (this == LIKE)
			return SelectorConstants.LIKE;
		if (this == IS)
			return SelectorConstants.IS;
		if (this == EXISTS)
			return SelectorConstants.EXISTS;
		return SelectorConstants.EXPR;
	}
}
