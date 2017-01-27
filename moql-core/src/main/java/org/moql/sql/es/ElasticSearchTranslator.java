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
    functionTranslators.put(QMatchTranslator.QMATCH_FUNCTION,
        new QMatchTranslator());
    functionTranslators.put(Regex.FUNCTION_NAME, new RegExpTranslator());
    functionTranslators.put(QMoreLikeTranslator.QMORE_LIKE_FUNCTION,
        new QMoreLikeTranslator());
  }

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
    JSONObject jsonObject = new JSONObject();
    JSONObject aggs = translate2Aggregations(selector);
    if (aggs != null) {
      jsonObject.put("size", 0);
      JSONObject filter = translate2Filter(selector);
      JSONObject shellAggs = new JSONObject();
      JSONObject condition = new JSONObject();
      condition.put("filter", filter);
      condition.put("aggs", aggs);
      shellAggs.put(CONDITION_ELEMENT, condition);
      jsonObject.put("aggs", shellAggs);
    } else {
      translateLimit(selector.getLimit(), jsonObject);
      JSONObject filter = translate2Filter(selector);
      jsonObject.put("filter", filter);
      JSONArray sortArray = translate2Sort(selector);
      if (sortArray != null) {
        jsonObject.put("sort", sortArray);
      }
    }
    // JSONObject filter = translate2Filter(selector);
    // jsonObject.put("filter", filter);
    // if (aggs != null) {
    // jsonObject.put("aggs", aggs);
    // }
    // // when the sql has group clause, the order clause will be translated
    // // in the 'translate2Aggregations' function.
    // if (!(selector.getRecordSetOperator() instanceof Group)) {
    //
    // }
    return jsonObject.toString();
  }

  protected String translate2Sql(SetlectorImpl setlector) {
    throw new UnsupportedOperationException("");
  }

  @Override
  public String translate2Condition(Filter filter) {
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

  protected JSONObject translate2Aggregations(SelectorImpl selector) {
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

  protected JSONObject translateSelectClause(SelectorImpl selector) {
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
        throw new MoqlTranslationException(
            StringFormater
                .format(
                    "Column '{}' is not a field! Column in select clause whith 'distinct' should be a field!",
                    column.getColumnMetadata().getName()));
      }
      aggregationColumns.add(column);
    }
    return translateGroupClause(aggregationColumns, null, selector);
  }

  protected JSONObject translateSelectClauseWithGroup(SelectorImpl selector) {
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

  protected JSONObject translateGroupClause(List<Column> groupColumns,
      List<Column> aggregationColumns, SelectorImpl selector) {
    JSONObject aggs = null;
    if (aggregationColumns != null) {
      aggs = translateAggregationColumns(aggregationColumns);
    }
    JSONObject fGroup = null;
    JSONObject lGroup = null;
    JSONObject temp = null;
    int i = 0;
    int length = groupColumns.size();
    for (Column column : groupColumns) {
      temp = translateGroup(column, selector);
      if (i + 1 == length) {
        if (aggs != null)
          temp.put("aggs", aggs);
      }
      if (fGroup == null) {
        fGroup = temp;
      } else {
        JSONObject subAggs = new JSONObject();
        subAggs.put(SUB_GROUP_ELEMENT + i, temp);
        lGroup.put("aggs", subAggs);
      }
      lGroup = temp;
      i++;
    }

    JSONObject group = new JSONObject();
    group.put(GROUP_ELEMENT, fGroup);
    return group;
  }

  protected JSONObject translateAggregationColumns(
      List<Column> aggregationColumns) {
    JSONObject aggs = new JSONObject();
    int i = 1;
    for (Column column : aggregationColumns) {
      AggregationFunction func = (AggregationFunction) column.getOperand();
      if (func.getName().equals("count"))
        continue;
      if (func.getParameterCount() != 1) {
        throw new MoqlTranslationException(StringFormater.format(
            "Function '{}' is unsupported!", func.getName()));
      }
      JSONObject agg = new JSONObject();
      agg.put("field", getOperandName(func.getParameters().get(0)));
      JSONObject jColumn = new JSONObject();
      jColumn.put(func.getName(), agg);
      aggs.put(COLUMN_ELEMENT + i++, jColumn);
    }
    return aggs;
  }

  protected JSONObject translateGroup(Column column, SelectorImpl selector) {
    JSONObject group = new JSONObject();
    JSONObject terms = new JSONObject();
    Operand operand = column.getOperand();
    if (operand instanceof MemberVariableExpression)
      terms.put("field", getOperandName(operand));
    else
      throw new MoqlTranslationException(StringFormater.format(
          "Group column '{}' is not a field!", getOperandName(operand)));
    translateLimit(selector.getLimit(), terms);
    translateOrder(column, selector.getOrder(), terms);
    group.put("terms", terms);
    return group;
  }

  protected void translateLimit(Limit limit, JSONObject jsonObject) {
    if (limit == null) {
      return;
    }
    int size = 0;
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    if (limit.getLimitMetadata().getOffset() != 0) {
      throw new MoqlTranslationException("Unsupported 'offset' in limit!");
    }
    size = limitMetadata.getValue();
    jsonObject.put("size", size);
  }

  protected void translateOrder(Column column, Order order,
      JSONObject jsonObject) {
    if (order == null)
      return;
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    for (int i = 0; i < columns.length; i++) {
      if (column == columns[i]) {
        JSONObject jsonOrder = new JSONObject();
        jsonOrder.put("_term", orderImpl.getOrderTypes()[i].toString()
            .toLowerCase());
        jsonObject.put("order", jsonOrder);
      }
    }
  }

  protected JSONObject translate2Filter(SelectorImpl selector) {
    JSONObject filter = new JSONObject();
    Condition condition = selector.getWhere();
    if (condition == null) {
      filter.put("match_all", new JSONObject());
    } else {
      translateOperand(condition.getOperand(), filter);
    }
    return filter;
  }

  protected void translateOperand(Operand operand, Object jsonObject) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        translateLogicExpression(expression, jsonObject);
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        translateRelationExpression(expression, jsonObject);
      } else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
        throw new MoqlTranslationException(StringFormater.format(
            "The expression '{}' does not support!", expression
                .getExpressionType().toString()));
      }
    } else if (operand instanceof ParenExpression) {
      ParenExpression parenExpression = (ParenExpression) operand;
      translateParenExpression(parenExpression, jsonObject);
    } else if (operand instanceof AbstractFunction) {
      AbstractFunction function = (AbstractFunction) operand;
      translateFunction(function, jsonObject);
    } else {
      throw new MoqlTranslationException(StringFormater.format(
          "The operand '{}' does not support!", operand.getOperandType()
              .toString()));
    }
  }

  protected void translateNotExpression(NotExpression expression,
      Object jsonObject) {
    if (expression.getRightOperand() instanceof IsExpression) {
      IsExpression isExpression = (IsExpression) expression.getRightOperand();
      translateIsExpression(isExpression.getLeftOperand(), jsonObject, true);
    } else {
      JSONObject not = new JSONObject();
      translateOperand(expression.getRightOperand(), not);
      putObject(jsonObject, "not", not);
    }
  }

  protected void putObject(Object jsonObject, String name, Object valueJson) {
    if (jsonObject instanceof JSONObject) {
      ((JSONObject) jsonObject).put(name, valueJson);
    } else {
      JSONObject jo = new JSONObject();
      jo.put(name, valueJson);
      ((JSONArray) jsonObject).add(jo);
    }
  }

  protected void translateLogicBinaryExpression(String operator,
      Operand lOperand, Operand rOperand, Object jsonObject) {
    JSONArray logic = new JSONArray();
    translateOperand(lOperand, logic);
    translateOperand(rOperand, logic);
    putObject(jsonObject, operator, logic);
  }

  protected void translateLogicExpression(
      AbstractOperationExpression expression, Object jsonObject) {
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
      AbstractOperationExpression expression, Object jsonObject) {
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
      Object jsonObject) {
    translateOperand(expression.getOperand(), jsonObject);
  }

  protected void translateEQExpression(Operand lOperand, Operand rOperand,
      Object jsonObject) {
    JSONObject eq = new JSONObject();
    eq.put(getOperandName(lOperand), getOperandName(rOperand));
    putObject(jsonObject, "term", eq);
  }

  protected void translateLGExpression(String operator, Operand lOperand,
      Operand rOperand, Object jsonObject) {
    JSONObject range = new JSONObject();
    JSONObject cmp = new JSONObject();
    cmp.put(operator, getOperandName(rOperand));
    range.put(getOperandName(lOperand), cmp);
    putObject(jsonObject, "range", range);
  }

  protected void translateNEExpression(Operand lOperand, Operand rOperand,
      Object jsonObject) {
    JSONObject not = new JSONObject();
    JSONObject term = new JSONObject();
    term.put(getOperandName(lOperand), getOperandName(rOperand));
    not.put("term", term);
    putObject(jsonObject, "not", not);
  }

  protected void translateBetweenExpression(BetweenExpression expression,
      Object jsonObject) {
    JSONObject range = new JSONObject();
    JSONObject cmp = new JSONObject();
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i == 0) {
        cmp.put("gte", getOperandName(rOperand));
        i++;
      } else {
        cmp.put("lt", getOperandName(rOperand));
      }
    }
    range.put(getOperandName(expression.getLeftOperand()), cmp);
    putObject(jsonObject, "range", range);
  }

  protected void translateLikeExpression(Operand lOperand, Operand rOperand,
      Object jsonObject) {
    JSONObject regex = new JSONObject();
    regex.put(getOperandName(lOperand),
        LikeExpression.translatePattern2Regex(getOperandName(rOperand)));
    putObject(jsonObject, "regexp", regex);
  }

  protected void translateInExpression(InExpression expression,
      Object jsonObject) {
    JSONObject terms = new JSONObject();
    JSONArray array = new JSONArray();
    for (Operand rOperand : expression.getrOperands()) {
      array.add(getOperandName(rOperand));
    }
    terms.put(getOperandName(expression.getLeftOperand()), array);
    putObject(jsonObject, "terms", terms);
  }

  protected void translateExistsExpression(ExistsExpression expression,
      Object jsonObject) {
    JSONObject exists = new JSONObject();
    exists.put("field", getOperandName(expression.getRightOperand()));
    putObject(jsonObject, "exists", exists);
  }

  protected void translateIsExpression(Operand lOperand, Object jsonObject,
      boolean not) {
    JSONObject exists = new JSONObject();
    if (not) {
      exists.put("field", getOperandName(lOperand));
      putObject(jsonObject, "exists", exists);
    } else {
      JSONObject notJo = new JSONObject();
      exists.put("field", getOperandName(lOperand));
      putObject(notJo, "exists", exists);
      putObject(jsonObject, "not", notJo);
    }
  }

  protected void translateFunction(AbstractFunction function, Object jsonObject) {
    ESFunctionTranslator functionTranslator = functionTranslators.get(function
        .getName());
    if (functionTranslator == null) {
      throw new MoqlTranslationException(StringFormater.format(
          "The function '{}' does not support!", function.getName()));
    } else {
      functionTranslator.translate(function, jsonObject);
    }
  }

  protected JSONArray translate2Sort(SelectorImpl selector) {
    if (selector.getOrder() == null)
      return null;
    JSONArray sort = new JSONArray();
    OrderImpl order = (OrderImpl) selector.getOrder();
    Column[] columns = order.getOrderColumns();
    OrderType[] orderTypes = order.getOrderTypes();
    for (int i = 0; i < columns.length; i++) {
      JSONObject sortColumn = new JSONObject();
      JSONObject props = new JSONObject();
      props.put("order", orderTypes[i].toString().toLowerCase());
      sortColumn.put(getOperandName(columns[i].getOperand()), props);
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

  @Override
  public List<FunctionTranslator> getFunctionTranslators() {
    // TODO Auto-generated method stub
    return new LinkedList<FunctionTranslator>(functionTranslators.values());
  }

}
