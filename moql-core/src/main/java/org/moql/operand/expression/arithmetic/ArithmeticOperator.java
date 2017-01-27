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

import org.moql.SelectorConstants;
import org.moql.operand.expression.OperatorGetter;

/**
 * 
 * @author Tang Tadin
 *
 */
public enum ArithmeticOperator implements OperatorGetter {
	ADD,
	SUBTRACT,
	MULTIPLY,
	DIVIDE,
	MODULAR,
	BITWISEAND,	//	'&'
	BITWISEOR,	//	'|'
	BITWISEXOR;	//	'^'
	
	public String getOperator() {
		if(this == ADD)
			return SelectorConstants.PLUS;
		if(this == SUBTRACT)
			return SelectorConstants.MINUS;
		if(this == MULTIPLY)
			return SelectorConstants.ASTERRISK;
		if(this == DIVIDE)
			return SelectorConstants.SLASH;
		if(this == MODULAR)
			return SelectorConstants.PERCENT;
		if(this == BITWISEAND)
			return SelectorConstants.AMPERSAND;
		if(this == BITWISEOR)
			return SelectorConstants.VERTICAL;
		return SelectorConstants.CIRCUMFLEX;
	}
}
