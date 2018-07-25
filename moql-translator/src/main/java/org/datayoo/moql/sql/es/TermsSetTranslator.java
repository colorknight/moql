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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.Function;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class TermsSetTranslator extends AbstractESFunctionTranslator {

  public static final String FUNCTION_NAME = "termsSet";

  public TermsSetTranslator() {
    super(FUNCTION_NAME);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 3) {
      throw new IllegalArgumentException(
          "Error function! The termsSet function's format should be termsSet(field, valueSet, minMatchField)!");
    }

    List<Operand> parameters = function.getParameters();
    JsonObject termsSet = new JsonObject();
    JsonObject inTermsSet = new JsonObject();
    String valueSet = getOperandName(parameters.get(1));
    String[] fields = valueSet.split(",");
    JsonArray array = new JsonArray();
    for (int i = 0; i < fields.length; i++) {
      array.add(getOperandName(fields[i]));
    }
    inTermsSet.add("terms", array);
    inTermsSet.addProperty("minimum_should_match_field",
        getOperandName(parameters.get(2)));
    termsSet.add(getOperandName(parameters.get(0)), inTermsSet);
    putObject(jsonObject, "terms_set", termsSet);
  }

}