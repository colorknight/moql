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
package org.datayoo.moql.sql.mongodb;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.Function;
import org.datayoo.moql.sql.es.AbstractESFunctionTranslator;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class TextTranslator extends AbstractMongoFunctionTranslator {

  public static final String FUNCTION_NAME = "text";

  public TextTranslator() {
    super(FUNCTION_NAME);
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() < 1 || function.getParameterCount() > 4) {
      throw new IllegalArgumentException(
          "Error function! The text function's format should be text(searchString,language,caseSenstive,diacriticSensitive)!");
    }
    JsonObject text = new JsonObject();
    List<Operand> parameters = function.getParameters();
    text.addProperty("$search", getOperandName(parameters.get(0)));
    if (parameters.size() > 1) {
      text.addProperty("$language", getOperandName(parameters.get(1)));
      if (parameters.size() > 2) {
        boolean b = Boolean.valueOf(getOperandName(parameters.get(2)));
        text.addProperty("$caseSensitive", b);
        if (parameters.size() > 3) {
          b = Boolean.valueOf(getOperandName(parameters.get(3)));
          text.addProperty("$diacriticSensitive", b);
        }
      }
    }
    putObject(jsonObject, "$text", text);
  }

}
