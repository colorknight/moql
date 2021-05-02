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
package org.datayoo.moql.operand.function.factory;

import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.Function;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
public interface FunctionFactory {

	Function createFunction(String name, List<Operand> parameters);
	
	String registFunction(String name, String className);
	
	String unregistFunction(String name);
	
	void importFunction(InputStream is);

	String getFunction(String name);

	Map<String, String> getAllFunctions();
	
}
