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

import com.google.gson.*;
import org.apache.commons.lang.Validate;
import org.moql.Filter;
import org.moql.MoqlTranslationException;
import org.moql.Operand;
import org.moql.Selector;
import org.moql.core.*;
import org.moql.metadata.LimitMetadata;
import org.moql.metadata.OrderType;
import org.moql.operand.constant.StringConstant;
import org.moql.operand.expression.AbstractOperationExpression;
import org.moql.operand.expression.ExpressionType;
import org.moql.operand.expression.ParenExpression;
import org.moql.operand.expression.logic.LogicOperator;
import org.moql.operand.expression.logic.NotExpression;
import org.moql.operand.expression.member.MemberVariableExpression;
import org.moql.operand.expression.relation.*;
import org.moql.operand.function.AbstractFunction;
import org.moql.operand.function.AggregationFunction;
import org.moql.operand.function.Regex;
import org.moql.sql.FunctionTranslator;
import org.moql.sql.SqlTranslator;
import org.moql.util.StringFormater;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Tang Tadin
 *
 */
public class ElasticSearchTranslator implements SqlTranslator {

  public static final String QUERY_FUNC_PREFIX = "q";

  public static final String COLUMN_ELEMENT = "column";

  public static final String GROUP_ELEMENT = "group";

  public static final String SUB_GROUP_ELEMENT = "subgrp";

  public static final String CONDITION_ELEMENT = "condition";

  protected Map<String, ESFunctionTranslator> functionTranslators = new HashMap<String, ESFunctionTranslator>();

  {
    functionTranslators
        .put(QMatchTranslator.QMATCH_FUNCTION, new QMatchTranslator());
    functionTranslators.put(Regex.FUNCTION_NAME, new RegExpTranslator());
    functionTranslators.put(QMoreLikeTranslator.QMORE_LIKE_FUNCTION,
        new QMoreLikeTranslator());
  }

  protected static Gson gson = new GsonBuilder().serializeNulls()
      .setPrettyPrinting().create();

  public String translate2Sql(Selector selector) {
    Validate.notNull(selector, "selector is null!");
    if (selector instanceof SelectorImpl) {
      return translate2Sql((SelectorImpl) selector);
    } else {
      return translate2Sql((SetlectorImpl) selector);
    }
  }

  protected String translate2Sql(SelectorImpl selector) {
    checkGrammer(selector);
    JsonObject jsonObject = new JsonObject();
    JsonObject aggs = translate2Aggregations(selector);
    if (aggs != null) {
      jsonObject.addProperty("size", 0);
      JsonObject filter = translate2Filter(selector);
      JsonObject shellAggs = new JsonObject();
      JsonObject condition = new JsonObject();
      condition.add("filter", filter);
      condition.add("aggs", aggs);
      shellAggs.add(CONDITION_ELEMENT, condition);
      jsonObject.add("aggs", shellAggs);
    } else {
      translateLimit(selector.getLimit(), jsonObject);
      JsonObject filter = translate2Filter(selector);
      jsonObject.add("filter", filter);
      JsonArray sortArray = translate2Sort(selector);
      if (sortArray != null) {
        jsonObject.add("sort", sortArray);
      }
    }
    // JsonObject filter = translate2Filter(selector);
    // JsonObject.put("filter", filter);
    // if (aggs != null) {
    // JsonObject.put("aggs", aggs);
    // }
    // // when the sql has group clause, the order clause will be translated
    // // in the 'translate2Aggregations' function.
    // if (!(selector.getRecordSetOperator() instanceof Group)) {
    //
    // }
    return gson.toJson(jsonObject);
  }

  protected String translate2Sql(SetlectorImpl setlector) {
    throw new UnsupportedOperationException("");
  }

  @Override public String translate2Condition(Filter filter) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("");
  }

  protected void checkGrammer(SelectorImpl selector) {
    if (selector.getTables().getTablesMetadata().getTables().size() > 1) {
      throw new MoqlTranslationException("The sql querys more than 1 table!");
    }
    if (selector.getHaving() != null) {
      throw new MoqlTranslationException("Having clause doesn't support!");
    }
  }

  protected JsonObject translate2Aggregations(SelectorImpl selector) {
    if (selector.getRecordSetOperator() instanceof Group) {
      return translateSelectClauseWithGroup(selector);
    } else {
      return translateSelectClause(selector);
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

  protected JsonObject translateSelectClause(SelectorImpl selector) {
    Columns columns = selector.getRecordSetOperator().getColumns();
    boolean isDistinct = columns.getColumnsMetadata().isDistinct();
    if (!isDistinct)
      return null;
    List<Column> aggregationColumns = new LinkedList<Column>();
    for (Column column : columns.getColumns()) {
      if (column.isJustUsed4Order())
        continue;
      Operand operand = column.getOperand();
      if (column.getColumnMetadata().getName().endsWith(".*")
          || !(operand instanceof MemberVariableExpression)) {
        throw new MoqlTranslationException(StringFormater.format(
            "Column '{}' is not a field! Column in select clause whith 'distinct' should be a field!",
            column.getColumnMetadata().getName()));
      }
      aggregationColumns.add(column);
    }
    return translateGroupClause(aggregationColumns, null, selector);
  }

  protected JsonObject translateSelectClauseWithGroup(SelectorImpl selector) {
    Columns columns = selector.getRecordSetOperator().getColumns();
    List<Column> groupColumns = new LinkedList<Column>();
    List<Column> aggregationColumns = new LinkedList<Column>();
    for (Column column : columns.getColumns()) {
      if (column.isJustUsed4Order())
        continue;
      if (column.getOperand() instanceof AggregationFunction) {
        aggregationColumns.add(column);
      } else {
        groupColumns.add(column);
      }
    }
    return translateGroupClause(groupColumns, aggregationColumns, selector);
  }

  protected JsonObject translateGroupClause(List<Column> groupColumns,
      List<Column> aggregationColumns, SelectorImpl selector) {
    JsonObject aggs = null;
    if (aggregationColumns != null) {
      aggs = translateAggregationColumns(aggregationColumns);
    }
    JsonObject fGroup = null;
    JsonObject lGroup = null;
    JsonObject temp = null;
    int i = 0;
    int length = groupColumns.size();
    for (Column column : groupColumns) {
      temp = translateGroup(column, selector);
      if (i + 1 == length) {
        if (aggs != null)
          temp.add("aggs", aggs);
      }
      if (fGroup == null) {
        fGroup = temp;
      } else {
        JsonObject subAggs = new JsonObject();
        subAggs.add(SUB_GROUP_ELEMENT + i, temp);
        lGroup.add("aggs", subAggs);
      }
      lGroup = temp;
      i++;
    }

    JsonObject group = new JsonObject();
    group.add(GROUP_ELEMENT, fGroup);
    return group;
  }

  protected JsonObject translateAggregationColumns(
      List<Column> aggregationColumns) {
    JsonObject aggs = new JsonObject();
    int i = 1;
    for (Column column : aggregationColumns) {
      AggregationFunction func = (AggregationFunction) column.getOperand();
      if (func.getName().equals("count"))
        continue;
      if (func.getParameterCount() != 1) {
        throw new MoqlTranslationException(StringFormater
            .format("Function '{}' is unsupported!", func.getName()));
      }
      JsonObject agg = new JsonObject();
      agg.addProperty("field", getOperandName(func.getParameters().get(0)));
      JsonObject jColumn = new JsonObject();
      jColumn.add(func.getName(), agg);
      aggs.add(COLUMN_ELEMENT + i++, jColumn);
    }
    return aggs;
  }

  protected JsonObject translateGroup(Column column, SelectorImpl selector) {
    JsonObject group = new JsonObject();
    JsonObject terms = new JsonObject();
    Operand operand = column.getOperand();
    if (operand instanceof MemberVariableExpression)
      terms.addProperty("field", getOperandName(operand));
    else
      throw new MoqlTranslationException(StringFormater
          .format("Group column '{}' is not a field!",
              getOperandName(operand)));
    translateLimit(selector.getLimit(), terms);
    translateOrder(column, selector.getOrder(), terms);
    group.add("terms", terms);
    return group;
  }

  protected void translateLimit(Limit limit, JsonObject jsonObject) {
    if (limit == null) {
      return;
    }
    int size = 0;
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    if (limit.getLimitMetadata().getOffset() != 0) {
      throw new MoqlTranslationException("Unsupported 'offset' in limit!");
    }
    size = limitMetadata.getValue();
    jsonObject.addProperty("size", size);
  }

  protected void translateOrder(Column column, Order order,
      JsonObject JsonObject) {
    if (order == null)
      return;
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    for (int i = 0; i < columns.length; i++) {
      if (column == columns[i]) {
        JsonObject jsonOrder = new JsonObject();
        jsonOrder.addProperty("_term",
            orderImpl.getOrderTypes()[i].toString().toLowerCase());
        JsonObject.add("order", jsonOrder);
      }
    }
  }

  protected JsonObject translate2Filter(SelectorImpl selector) {
    JsonObject filter = new JsonObject();
    Condition condition = selector.getWhere();
    if (condition == null) {
      filter.add("match_all", new JsonObject());
    } else {
      translateOperand(condition.getOperand(), filter);
    }
    return filter;
  }

  protected void translateOperand(Operand operand, JsonElement jsonObject) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        translateLogicExpression(expression, jsonObject);
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        translateRelationExpression(expression, jsonObject);
      } else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
        throw new MoqlTranslationException(StringFormater
            .format("The expression '{}' does not support!",
                expression.getExpressionType().toString()));
      }
    } else if (operand instanceof ParenExpression) {
      ParenExpression parenExpression = (ParenExpression) operand;
      translateParenExpression(parenExpression, jsonObject);
    } else if (operand instanceof AbstractFunction) {
      AbstractFunction function = (AbstractFunction) operand;
      translateFunction(function, jsonObject);
    } else {
      throw new MoqlTranslationException(StringFormater
          .format("The operand '{}' does not support!",
              operand.getOperandType().toString()));
    }
  }

  protected void translateNotExpression(NotExpression expression,
      JsonElement jsonObject) {
    if (expression.getRightOperand() instanceof IsExpression) {
      IsExpression isExpression = (IsExpression) expression.getRightOperand();
      translateIsExpression(isExpression.getLeftOperand(), jsonObject, true);
    } else {
      JsonObject not = new JsonObject();
      translateOperand(expression.getRightOperand(), not);
      putObject(jsonObject, "not", not);
    }
  }

  protected void putObject(JsonElement jsonObject, String name,
      JsonElement valueJson) {
    if (jsonObject instanceof JsonObject) {
      ((JsonObject) jsonObject).add(name, valueJson);
    } else {
      JsonObject jo = new JsonObject();
      jo.add(name, valueJson);
      ((JsonArray) jsonObject).add(jo);
    }
  }

  protected void translateLogicBinaryExpression(String operator,
      Operand lOperand, Operand rOperand, JsonElement jsonObject) {
    JsonArray logic = new JsonArray();
    translateOperand(lOperand, logic);
    translateOperand(rOperand, logic);
    putObject(jsonObject, operator, logic);
  }

  protected void translateLogicExpression(
      AbstractOperationExpression expression, JsonElement jsonObject) {
    if (expression.getOperator() == LogicOperator.NOT) {
      translateNotExpression((NotExpression) expression, jsonObject);
    } else {
      if (expression.getOperator() == LogicOperator.AND)
        translateLogicBinaryExpression("and", expression.getLeftOperand(),
            expression.getRightOperand(), jsonObject);
      else
        translateLogicBinaryExpression("or", expression.getLeftOperand(),
            expression.getRightOperand(), jsonObject);
    }
  }

  protected void translateRelationExpression(
      AbstractOperationExpression expression, JsonElement jsonObject) {
    if (expression.getOperator() == RelationOperator.EQ) {
      translateEQExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.GT) {
      translateLGExpression("gt", expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.GE) {
      translateLGExpression("gte", expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.LT) {
      translateLGExpression("lt", expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.LE) {
      translateLGExpression("lte", expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.NE) {
      translateNEExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.BETWEEN) {
      translateBetweenExpression((BetweenExpression) expression, jsonObject);
    } else if (expression.getOperator() == RelationOperator.LIKE) {
      translateLikeExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonObject);
    } else if (expression.getOperator() == RelationOperator.IN) {
      translateInExpression((InExpression) expression, jsonObject);
    } else if (expression.getOperator() == RelationOperator.IS) {
      translateIsExpression(expression.getLeftOperand(), jsonObject, false);
    } else if (expression.getOperator() == RelationOperator.EXISTS) {
      throw new MoqlTranslationException(
          "The 'exists' operator does not support!");
    } else {
      translateOperand(expression.getRightOperand(), jsonObject);
    }

  }

  protected void translateParenExpression(ParenExpression expression,
      JsonElement jsonObject) {
    translateOperand(expression.getOperand(), jsonObject);
  }

  protected void translateEQExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonObject) {
    JsonObject eq = new JsonObject();
    eq.addProperty(getOperandName(lOperand), getOperandName(rOperand));
    putObject(jsonObject, "term", eq);
  }

  protected void translateLGExpression(String operator, Operand lOperand,
      Operand rOperand, JsonElement jsonObject) {
    JsonObject range = new JsonObject();
    JsonObject cmp = new JsonObject();
    cmp.addProperty(operator, getOperandName(rOperand));
    range.add(getOperandName(lOperand), cmp);
    putObject(jsonObject, "range", range);
  }

  protected void translateNEExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonObject) {
    JsonObject not = new JsonObject();
    JsonObject term = new JsonObject();
    term.addProperty(getOperandName(lOperand), getOperandName(rOperand));
    not.add("term", term);
    putObject(jsonObject, "not", not);
  }

  protected void translateBetweenExpression(BetweenExpression expression,
      JsonElement jsonObject) {
    JsonObject range = new JsonObject();
    JsonObject cmp = new JsonObject();
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i == 0) {
        cmp.addProperty("gte", getOperandName(rOperand));
        i++;
      } else {
        cmp.addProperty("lt", getOperandName(rOperand));
      }
    }
    range.add(getOperandName(expression.getLeftOperand()), cmp);
    putObject(jsonObject, "range", range);
  }

  protected void translateLikeExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonObject) {
    JsonObject regex = new JsonObject();
    regex.addProperty(getOperandName(lOperand),
        LikeExpression.translatePattern2Regex(getOperandName(rOperand)));
    putObject(jsonObject, "regexp", regex);
  }

  protected void translateInExpression(InExpression expression,
      JsonElement jsonObject) {
    JsonObject terms = new JsonObject();
    JsonArray array = new JsonArray();
    for (Operand rOperand : expression.getrOperands()) {
      array.add(getOperandName(rOperand));
    }
    terms.add(getOperandName(expression.getLeftOperand()), array);
    putObject(jsonObject, "terms", terms);
  }

  protected void translateExistsExpression(ExistsExpression expression,
      JsonElement jsonObject) {
    JsonObject exists = new JsonObject();
    exists.addProperty("field", getOperandName(expression.getRightOperand()));
    putObject(jsonObject, "exists", exists);
  }

  protected void translateIsExpression(Operand lOperand, JsonElement jsonObject,
      boolean not) {
    JsonObject exists = new JsonObject();
    if (not) {
      exists.addProperty("field", getOperandName(lOperand));
      putObject(jsonObject, "exists", exists);
    } else {
      JsonObject notJo = new JsonObject();
      exists.addProperty("field", getOperandName(lOperand));
      putObject(notJo, "exists", exists);
      putObject(jsonObject, "not", notJo);
    }
  }

  protected void translateFunction(AbstractFunction function,
      JsonElement jsonObject) {
    ESFunctionTranslator functionTranslator = functionTranslators
        .get(function.getName());
    if (functionTranslator == null) {
      throw new MoqlTranslationException(StringFormater
          .format("The function '{}' does not support!", function.getName()));
    } else {
      functionTranslator.translate(function, jsonObject);
    }
  }

  protected JsonArray translate2Sort(SelectorImpl selector) {
    if (selector.getOrder() == null)
      return null;
    JsonArray sort = new JsonArray();
    OrderImpl order = (OrderImpl) selector.getOrder();
    Column[] columns = order.getOrderColumns();
    OrderType[] orderTypes = order.getOrderTypes();
    for (int i = 0; i < columns.length; i++) {
      JsonObject sortColumn = new JsonObject();
      JsonObject props = new JsonObject();
      props.addProperty("order", orderTypes[i].toString().toLowerCase());
      sortColumn.add(getOperandName(columns[i].getOperand()), props);
      sort.add(sortColumn);
    }
    return sort;
  }

  @Override
  public void addFunctionTranslator(FunctionTranslator functionTranslator) {
    // TODO Auto-generated method stub
    Validate.notNull(functionTranslator, "functionTranslator is null!");
    if (!(functionTranslator instanceof ESFunctionTranslator)) {
      throw new IllegalArgumentException(
          "functionTranslator is not a instance of ESFunctionTranslator interface.");
    }
    functionTranslators.put(functionTranslator.getFunctionName(),
        (ESFunctionTranslator) functionTranslator);
  }

  @Override
  public void addAllFunctionTranslator(
      List<FunctionTranslator> functionTranslators) {
    // TODO Auto-generated method stub
    Validate.notNull(functionTranslators, "functionTranslators is null!");
    for (FunctionTranslator functionTranslator : functionTranslators) {
      addFunctionTranslator(functionTranslator);
    }
  }

  @Override
  public FunctionTranslator removeFunctionTranslator(String functionName) {
    // TODO Auto-generated method stub
    Validate.notEmpty(functionName, "functionName is empty!");
    return functionTranslators.remove(functionName);
  }

  @Override public List<FunctionTranslator> getFunctionTranslators() {
    // TODO Auto-generated method stub
    return new LinkedList<FunctionTranslator>(functionTranslators.values());
  }

}
