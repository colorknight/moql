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
import org.datayoo.moql.SelectorConstants;
import org.datayoo.moql.metadata.OperatorType;
import org.datayoo.moql.operand.constant.StringConstant;
import org.datayoo.moql.util.StringFormater;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Tang Tadin
 *
 */
public class LikeExpression extends AbstractRelationExpression {
	
	protected Pattern pattern;

	public LikeExpression(Operand lOperand, Operand rOperand) {
		super(OperatorType.BINARY, RelationOperator.LIKE, lOperand, rOperand);
		// TODO Auto-generated constructor stub
		if (!(rOperand instanceof StringConstant)) {
			throw new IllegalArgumentException("Parameter 'rOperand' is not a String");
		}
		String data = (String) rOperand.operate(null);
		String regex = translatePattern2Regex(data);
		pattern = Pattern.compile(regex, Pattern.MULTILINE);
	}
	
	public static String translatePattern2Regex(String pattern) {
		StringBuffer sbuf = new StringBuffer(pattern.length()*2);
		for(int i = 0; i < pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == SelectorConstants.PERCENT.charAt(0)) {
				sbuf.append("[^\f]*");
			} else if (ch == SelectorConstants.UNDERSCORE) {
				sbuf.append(SelectorConstants.PERIOD);
			} else if (ch == SelectorConstants.PERIOD) {
				sbuf.append(".");
			} else if (ch == SelectorConstants.BACKSLASH) {
				ch = pattern.charAt(++i);
				if (ch == SelectorConstants.PERCENT.charAt(0)) {
					sbuf.append(SelectorConstants.PERCENT);
				} else if (ch == SelectorConstants.UNDERSCORE) {
					sbuf.append(SelectorConstants.UNDERSCORE);
				} else if (ch == SelectorConstants.BACKSLASH) {
					sbuf.append("\\\\");
				} else {
					throw new IllegalArgumentException(
							StringFormater
									.format("Invalid escape char at {} of search pattern '{}'!", i, pattern));
				}
			} else {
				sbuf.append(ch);
			}
		}
		return sbuf.toString();
	}

	@Override
	public Object operate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object value = lOperand.operate(entityMap);
		if (value == null)
			return false;
		Matcher matcher = pattern.matcher(value.toString());
		return matcher.matches();
	}

}
