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
public class MatchPhraseTranslator extends AbstractESFunctionTranslator {

  public static final String FUNCTION_NAME = "matchPhrase";

  public MatchPhraseTranslator() {
    super(FUNCTION_NAME);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 2
        && function.getParameterCount() != 3) {
      throw new IllegalArgumentException(
          "Error function! The matchPhrase function's format should be "
              + "matchPhrase(field, queryString) or "
              + "matchPhrase(field, queryString, analyzer)!");
    }

    List<Operand> parameters = function.getParameters();
    String fieldString = getOperandName(parameters.get(0));
    String field = getOperandName(fieldString);
    if (parameters.size() == 2) {
      JsonObject matchPhrase = new JsonObject();
      matchPhrase.addProperty(field, getOperandName(parameters.get(1)));
      putObject(jsonObject, "match_phrase", matchPhrase);
    } else {
      JsonObject matchPhrase = new JsonObject();
      JsonObject inPhrase = new JsonObject();

      inPhrase.addProperty("query", getOperandName(parameters.get(1)));
      inPhrase.addProperty("analyzer", getOperandName(parameters.get(2)));
      matchPhrase.add(field, inPhrase);
      putObject(jsonObject, "match_phrase", matchPhrase);
    }

  }
}