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
package org.datayoo.moql.util;

import org.datayoo.moql.SelectorConstants;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class MoqlStringUtils {
	/**
	 * Translate the string to moql string's format.
	 * @param string
	 * @return
	 */
	public static String string2MoqlString(String string) {
		if (string == null) {
			return null;
		}
		StringBuffer sbuf = new StringBuffer(string.length()+50);
		sbuf.append(SelectorConstants.QUOTE);
		for(int i = 0; i < string.length(); i++) {
			if (string.charAt(i) == SelectorConstants.QUOTE) {
				sbuf.append(SelectorConstants.QUOTE);
			}
			sbuf.append(string.charAt(i));
		}
		return sbuf.toString();
	}
	
	public static String moqlString2String(String moqlString) {
		if (moqlString == null)
			return null;
		//	the minimum sql string is "''"
		if (moqlString.length() < 2
				|| moqlString.charAt(0) != SelectorConstants.QUOTE
				|| moqlString.charAt(moqlString.length()-1) != SelectorConstants.QUOTE) {
			throw new IllegalArgumentException(StringFormater.format("sqlString '{}' is invalid!", moqlString));
		}
		StringBuffer sbuf = new StringBuffer(moqlString.length());
		
		for(int i = 1; i < moqlString.length() - 1; i++) {
			if (moqlString.charAt(i) == SelectorConstants.QUOTE
					&& moqlString.charAt(i+1) == SelectorConstants.QUOTE) {
				i++;
			}
			sbuf.append(moqlString.charAt(i));
		}
		return sbuf.toString();
	}
}
