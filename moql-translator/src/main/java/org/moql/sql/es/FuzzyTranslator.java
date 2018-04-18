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
package org.moql.sql.es;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.moql.Operand;
import org.moql.operand.function.Function;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class FuzzyTranslator extends AbstractESFunctionTranslator {

  public static final String FUNCTION_NAME = "fuzzy";

  public FuzzyTranslator() {
    super(FUNCTION_NAME);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 2
        && function.getParameterCount() != 5) {
      throw new IllegalArgumentException(
          "Error function! The fuzzy function's format should be fuzzy(field, value) or fuzzy(field,"
              + "value,fuzziness,prefix_length,max_expansions)!");
    }
    JsonObject fuzzy = new JsonObject();

    List<Operand> parameters = function.getParameters();
    String fieldString = getOperandName(parameters.get(0));
    String field = getOperandName(fieldString);
    if (parameters.size() == 2) {
      fuzzy.addProperty(field,
          getOperandName(parameters.get(1)));
    } else {
      JsonObject inFuzzy = new JsonObject();
      inFuzzy.addProperty("value", getOperandName(parameters.get(1)));
      inFuzzy.addProperty("fuzziness", (Long) parameters.get(3).getValue());
      inFuzzy.addProperty("prefix_length", (Long) parameters.get(4).getValue());
      inFuzzy
          .addProperty("max_expansions", (Long) parameters.get(5).getValue());
      putObject(fuzzy, field, inFuzzy);
    }

    putObject(jsonObject, "fuzzy", fuzzy);
  }

}
