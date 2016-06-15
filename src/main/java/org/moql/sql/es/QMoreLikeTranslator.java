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
public class QMoreLikeTranslator extends AbstractESFunctionTranslator {

  public static final String QMORE_LIKE_FUNCTION = "qmoreLike";

  public QMoreLikeTranslator() {
    super(QMORE_LIKE_FUNCTION);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void innerTranslate(Function function, Object jsonObject) {
    // TODO Auto-generated method stub
    if (function.getParameterCount() != 4) {
      throw new IllegalArgumentException(
          "Error function! The qmoreLike function's format should be qmoreLike(fields,likeText,minTermFreq,maxQueryTerms)!");
    }
    JSONObject query = new JSONObject();
    JSONObject moreLike = new JSONObject();

    List<Operand> parameters = function.getParameters();
    String fieldString = getOperandName(parameters.get(0));
    String[] fields = fieldString.split(",");
    JSONArray array = new JSONArray();
    for (int i = 0; i < fields.length; i++) {
      array.add(getOperandName(fields[i]));
    }
    moreLike.put("fields", array);
    moreLike.put("like_text", getOperandName(parameters.get(1)));
    moreLike.put("min_term_freq", parameters.get(2).getValue());
    moreLike.put("max_query_terms", parameters.get(3).getValue());
    query.put("more_like_this", moreLike);

    putObject(jsonObject, "query", query);
  }

}
