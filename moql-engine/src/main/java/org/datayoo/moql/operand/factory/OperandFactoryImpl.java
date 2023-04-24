/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import org.datayoo.moql.operand.expression.member.MemberVisitor;
import org.datayoo.moql.operand.function.MemberFunction;
import org.datayoo.moql.operand.function.factory.FunctionFactory;
import org.datayoo.moql.operand.function.factory.FunctionFactoryImpl;
import org.datayoo.moql.util.StringFormater;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class OperandFactoryImpl implements OperandFactory {

  protected FunctionFactory functionFactory = new FunctionFactoryImpl();

  protected Set<MemberVisitor> memberVisitors = new HashSet<>();

  public OperandFactoryImpl() {
  }

  @Override
  public Operand createOperand(String operand) throws MoqlException {
    // TODO Auto-generated method stub
    Validate.notEmpty(operand, "Parameter 'operand' is empty!");
    Operand pseudoOperand = createPseudoOperand(operand);
    if (pseudoOperand != null)
      return pseudoOperand;
    try {
      ANTLRInputStream is = new ANTLRInputStream(
          new ByteArrayInputStream(operand.getBytes()));
      OperandLexer lexer = new OperandLexer(is);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      OperandParser parser = new OperandParser(tokens);
      parser.setFunctionFactory(functionFactory);
      parser.setMemberVisitors(memberVisitors);
      return parser.operand();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlException(
          StringFormater.format("Create operand '{}' failed!", operand), e);
    }
  }

  protected static Operand createPseudoOperand(String operand) {
    //
    if (operand.endsWith(".*") || operand.equals("*")) { //a.*,b.*,etc
      return new PseudoOperand(operand);
    }
    return null;
  }

  public String registFunction(String name, String className) {
    return functionFactory.registFunction(name, className);
  }

  @Override
  public String forceRegistFunction(String name, String className) {
    return functionFactory.forceRegistFunction(name, className);
  }

  public String unregistFunction(String name) {
    return functionFactory.unregistFunction(name);
  }

  @Override
  public void addMemberVisitor(MemberVisitor memberVisitor) {
    memberVisitors.add(memberVisitor);
  }

  @Override
  public boolean removeMemberVisitor(MemberVisitor memberVisitor) {
    return memberVisitors.remove(memberVisitor);
  }
}
