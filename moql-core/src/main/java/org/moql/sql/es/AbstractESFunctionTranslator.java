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
package org.moql.sql.es;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.Validate;
import org.moql.Operand;
import org.moql.operand.constant.StringConstant;
import org.moql.operand.expression.member.MemberVariableExpression;
import org.moql.operand.function.Function;
import org.moql.util.StringFormater;

/**
 * @author Tang Tadin
 */
public abstract class AbstractESFunctionTranslator implements
    ESFunctionTranslator {

  protected String functionName;

  public AbstractESFunctionTranslator(String functionName) {
    Validate.notEmpty(functionName, "Parameter 'functionName' is empty!");
    this.functionName = functionName;
  }

  public String getFunctionName() {
    return functionName;
  }

  @Override
  public String translate(Function function) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public void translate(Function function, Object jsonObject) {
    // TODO Auto-generated method stub
    Validate.notNull(function, "Parameter 'function' is null!");
    Validate.notNull(jsonObject, "Parameter 'jsonObject' is null!");
    if (!functionName.equalsIgnoreCase(function.getName())) {
      throw new IllegalArgumentException(StringFormater.format(
          "FunctionTranslator '{}' couldn't translate the function '{}'!",
          functionName, function.getName()));
    }
    innerTranslate(function, jsonObject);
  }
  
  protected abstract void innerTranslate(Function function, Object jsonObject);
  
  protected void putObject(Object jsonObject, String name, Object valueJson) {
    if (jsonObject instanceof JSONObject) {
      ((JSONObject) jsonObject).put(name, valueJson);
    } else {
      JSONObject jo = new JSONObject();
      jo.put(name, valueJson);
      ((JSONArray) jsonObject).add(jo);
    }
  }
  
  protected String getOperandName(Operand operand) {
    String name = operand.getName();
    if (operand instanceof MemberVariableExpression) {
      int index = name.indexOf('.');
      if (index != -1) {
        return name.substring(index + 1);
      }
    } else if (operand instanceof StringConstant) {
      return name.substring(1, name.length() - 1);
    }
    return name;
  }
  
  protected String getOperandName(String name) {
    name = name.trim();
    int index = name.indexOf('.');
    if (index != -1) {
      return name.substring(index + 1);
    }
    return name;
  }
  
}
