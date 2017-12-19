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
package org.moql.function;

import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.operand.function.AbstractFunction;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Tang Tadin
 *
 */
public class Regex extends AbstractFunction {
	
	public static final String FUNCTION_NAME = "regex";
	
	protected Pattern pattern;
	
	protected boolean isConstantRegex = false;
	
	protected String currentRegex;
	
	public Regex(List<Operand> parameters) {
		super(FUNCTION_NAME, 2, parameters);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
	 */
	@Override
	protected Object innerOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = parameters.get(0).operate(entityMap);
		if (obj == null)
			return false;
		if (pattern == null) {
			Operand operand = parameters.get(1); 
			String regex = operand.operate(entityMap).toString();
			pattern = Pattern.compile(regex);
			if (operand.isConstantReturn()) {
				isConstantRegex = true;
			}
			currentRegex = regex;
		} else {
			if (!isConstantRegex) {
				String regex = parameters.get(1).operate(entityMap).toString();
				// the same string should be have the same address in one virtual machine
				if (!currentRegex.equals(regex)) {
					pattern = Pattern.compile(regex);
					currentRegex = regex;
				}
			}
		}
		Matcher matcher = pattern.matcher(obj.toString());
		return matcher.matches();
	}

}
