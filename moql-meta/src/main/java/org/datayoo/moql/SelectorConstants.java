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
package org.datayoo.moql;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class SelectorConstants {
	
	public static final String NULL = "null";

	public static final char LPAREN = '(';
	
	public static final char RPAREN = ')';
	
	public static final char LBRACKET = '[';
	
	public static final char RBRACKET = ']';
	
	public static final char LBRACE = '{';
	
	public static final char RBRACE = '}';
	
	public static final char COMMA = ',';
	
	public static final char QUOTE = '\'';
	
	public static final char PERIOD = '.';
	
	public static final char UNDERSCORE = '_';	//
	
	public static final char BLANKSPACE = ' ';	//	
	
	public static final char BACKSLASH= '\\';
	
	//	Operator
	public static final String PLUS = "+";
	
	public static final String MINUS = "-";
	
	public static final String ASTERRISK = "*";
	
	public static final String SLASH= "/";
	
	public static final String PERCENT= "%";

	public static final String LSHIFT= "<<";

	public static final String RSHIFT= ">>";

	public static final String SWANGDASH = "~";	//	'~'

	public static final String AMPERSAND = "&";	//	'&'

	public static final String VERTICAL = "|";	//	'|'
	
	public static final String CIRCUMFLEX = "^";	//	'^'
	
	public static final String AND = "and";
	
	public static final String OR = "or";
	
	public static final String NOT = "not";
	
	public static final String EQ = "=";
	
	public static final String LT = "<";
	
	public static final String GT = ">";
	
	public static final String LE = "<=";
	
	public static final String GE = ">=";
	
	public static final String NE = "<>";

	public static final String NE2 = "!=";

	public static final String PAREN = "(";
	
	public static final String BETWEEN = "between";
	
	public static final String IN = "in";
	
	public static final String LIKE = "like";
	
	public static final String IS = "is";
	
	public static final String EXISTS = "exists";
	//	expression
	public static final String EXPR = "expr";
	
	public static final String NOT_AVAILABLE = "N/A";
	
	
	//	system properties
	public static final String PROP_FUNCTION_FACTORY = "org.moql.functionfactory";
	
	//
	
}
