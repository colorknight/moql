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
package org.moql;


public interface Operand {
	/**
	 * get the name of operand
	 */
	String getName();
	/**
	 * get the position of the operand in the expression text
	 * @return org.antlr.runtime.Token 
	 */
	Object getSource();
	/**
	 * get or caculate the value from the given entity map. The entity map
	 * is a map which entry's key is the variable operand's name, and the 
	 * value is the operand's value. 
	 */
	Object operate(EntityMap entityMap);
	/**
	 * get or caculate the boolean value from the given entity map. The 
	 * return is true if the value is not null, otherwise is false.
	 */
	boolean booleanOperate(EntityMap entityMap);
	/**
	 * increment entityMap to the operand 
	 */
	void increment(EntityMap entityMap);
	/**
	 * return the value of the operand
	 */
	Object getValue();
	/**
	 * get the type of the operand
	 */
	OperandType getOperandType();
	/**
	 * the return is true when the operand's type is CONSTANT, otherwise is false 
	 */
	boolean isConstantReturn();
	/**
	 * clear the result of the operand. Some operand will add up the return if it's operate method
	 * is called many times. For example the function operand 'count()', it's return
	 * will be incremented 1 by the operate method called per time. 
	 */
	void clear();
	
}
