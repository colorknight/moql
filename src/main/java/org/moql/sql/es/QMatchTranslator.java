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
import org.moql.Operand;
import org.moql.operand.function.Function;

import java.util.List;

/**
 * @author Tang Tadin
 */
public class QMatchTranslator extends AbstractESFunctionTranslator {

  public static final String QMATCH_FUNCTION = "qmatch";

  public QMatchTranslator() {
    super(QMATCH_FUNCTION);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, Object jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 2) {
      throw new IllegalArgumentException(
          "Error function! The qmatch function's format should be qmatch(fields,queryString)!");
    }
    JSONObject query = new JSONObject();
    JSONObject match = new JSONObject();

    List<Operand> parameters = function.getParameters();
    String fieldString = getOperandName(parameters.get(0));
    String[] fields = fieldString.split(",");
    if (fields.length == 1) {
      match.put(fields[0], getOperandName(parameters.get(1)));
      query.put("match", match);
    } else {
      JSONArray array = new JSONArray();
      for(int i = 0; i < fields.length; i++) {
        array.add(getOperandName(fields[i]));
      }
      match.put("query", getOperandName(parameters.get(1)));
      match.put("fields", array);
      query.put("multi_match", match);
    }
    putObject(jsonObject, "query", query);
  }


}
