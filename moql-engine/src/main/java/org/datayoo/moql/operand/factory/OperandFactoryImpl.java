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
package org.datayoo.moql.operand.factory;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.commons.lang3.Validate;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.antlr.OperandLexer;
import org.datayoo.moql.antlr.OperandParser;
import org.datayoo.moql.operand.OperandFactory;
import org.datayoo.moql.operand.PseudoOperand;
import org.datayoo.moql.operand.function.factory.FunctionFactory;
import org.datayoo.moql.operand.function.factory.FunctionFactoryImpl;
import org.datayoo.moql.util.StringFormater;

import java.io.ByteArrayInputStream;

/**
 * 
 * @author Tang Tadin
 *
 */
public class OperandFactoryImpl implements OperandFactory {
	
	protected static FunctionFactory functionFactory = FunctionFactoryImpl.createFunctionFactory();
	
	protected static OperandFactory operandFactory;
	
	protected OperandFactoryImpl() {}
	
	public static synchronized OperandFactory createOperandFactory() {
		if (operandFactory == null) {
			operandFactory = new OperandFactoryImpl();
		}
		return operandFactory;
	}
	
	@Override
	public Operand createOperand(String operand) throws MoqlException {
		// TODO Auto-generated method stub
		Validate.notEmpty(operand, "Parameter 'operand' is empty!");
		Operand pseudoOperand = createPseudoOperand(operand);
		if (pseudoOperand != null)
		  return pseudoOperand;
		try {
			ANTLRInputStream is = new ANTLRInputStream(new ByteArrayInputStream(operand.getBytes()));
			OperandLexer lexer = new OperandLexer(is);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			OperandParser parser = new OperandParser(tokens);
			parser.setFunctionFactory(functionFactory);
			return parser.operand();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new MoqlException(
          StringFormater.format("Create operand '{}' failed!", operand), e);
		}
	}
	
	protected static Operand createPseudoOperand(String operand) {
	   //  
    if (operand.endsWith(".*")) { //a.*,b.*,etc
      return new PseudoOperand(operand);
    }
    return null;
	}
	
	public static String registFunction(String name, String className) {
		return functionFactory.registFunction(name, className);
	}
	
	public static String unregistFunction(String name) {
		return functionFactory.unregistFunction(name);
	}

}
