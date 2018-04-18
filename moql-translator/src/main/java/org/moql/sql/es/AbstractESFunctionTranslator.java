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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
  public void translate(Function function, JsonElement jsonObject) {
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
  
  protected abstract void innerTranslate(Function function, JsonElement jsonObject);
  
  protected void putObject(JsonElement jsonObject, String name, Object valueJson) {
    if (jsonObject instanceof JsonObject) {
      if (valueJson instanceof JsonElement) {
        ((JsonObject) jsonObject).add(name, (JsonElement)valueJson);
      } else {
        ((JsonObject) jsonObject).addProperty(name, valueJson.toString());
      }
    } else {
      JsonObject jo = new JsonObject();
      if (valueJson instanceof JsonElement) {
        jo.add(name, (JsonElement)valueJson);
      } else {
        jo.addProperty(name, valueJson.toString());
      }
      ((JsonArray) jsonObject).add(jo);
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
