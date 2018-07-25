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
package org.datayoo.moql.sql.es;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.Function;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class MatchPhrasePrefixTranslator extends AbstractESFunctionTranslator {

  public static final String FUNCTION_NAME = "matchPhrasePrefix";

  public MatchPhrasePrefixTranslator() {
    super(FUNCTION_NAME);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 2
        && function.getParameterCount() != 3) {
      throw new IllegalArgumentException(
          "Error function! The matchPhrasePrefix function's format should be "
              + "matchPhrasePrefix(field, queryString) or "
              + "matchPhrasePrefix(field, queryString, analyzer)!");
    }

    List<Operand> parameters = function.getParameters();
    String fieldString = getOperandName(parameters.get(0));
    String field = getOperandName(fieldString);
    if (parameters.size() == 2) {
      JsonObject matchPhrasePrefix = new JsonObject();
      matchPhrasePrefix
          .addProperty(field, getOperandName(parameters.get(1)));
      putObject(jsonObject, "match_phrase_prefix", matchPhrasePrefix);
    } else {
      JsonObject matchPhrasePrefix = new JsonObject();
      JsonObject inPhrasePrefix = new JsonObject();

      inPhrasePrefix.addProperty("query", getOperandName(parameters.get(1)));
      inPhrasePrefix.addProperty("analyzer", getOperandName(parameters.get(2)));
      matchPhrasePrefix.add(field, inPhrasePrefix);
      putObject(jsonObject, "match_phrase_prefix", matchPhrasePrefix);
    }

  }
}