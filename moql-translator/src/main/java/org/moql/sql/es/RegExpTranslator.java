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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.moql.Operand;
import org.moql.operand.function.Function;
import org.moql.operand.function.Regex;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class RegExpTranslator extends AbstractESFunctionTranslator {

  public RegExpTranslator() {
    super(Regex.FUNCTION_NAME);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, JsonElement jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 2) {
      throw new IllegalArgumentException(
          "Error function! The regex function's format should be regex(field,pattern)!");
    }
    JsonObject regexp = new JsonObject();
    List<Operand> parameters = function.getParameters();
    regexp.addProperty(getOperandName(parameters.get(0)),
        getOperandName(parameters.get(1)));
    putObject(jsonObject, "regexp", regexp);
  }

}
