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
package org.datayoo.moql.sql;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.Filter;
import org.datayoo.moql.Operand;
import org.datayoo.moql.Selector;
import org.datayoo.moql.core.*;
import org.datayoo.moql.core.group.GroupRecordSetOperator;
import org.datayoo.moql.core.table.SelectorTable;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.expression.ParenExpression;
import org.datayoo.moql.operand.expression.arithmetic.ArithmeticOperator;
import org.datayoo.moql.operand.expression.bit.BitwiseOperator;
import org.datayoo.moql.operand.expression.logic.LogicOperator;
import org.datayoo.moql.operand.expression.logic.NotExpression;
import org.datayoo.moql.operand.expression.relation.BetweenExpression;
import org.datayoo.moql.operand.expression.relation.ExistsExpression;
import org.datayoo.moql.operand.expression.relation.InExpression;
import org.datayoo.moql.operand.expression.relation.RelationOperator;
import org.datayoo.moql.operand.function.AbstractFunction;
import org.datayoo.moql.operand.selector.ColumnSelectorOperand;
import org.datayoo.moql.operand.selector.ValueSelectorOperand;
import org.datayoo.moql.util.StringFormater;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class MoqlGrammarTranslator implements SqlTranslator {

  protected Map<String, FunctionTranslator> functionTranslators = new HashMap<String, FunctionTranslator>();

  public String translate2Sql(Selector selector) {
    return translate2Sql(selector, new HashMap<String, Object>());
  }

  @Override
  public String translate2Sql(Selector selector,
      Map<String, Object> translationContext) {
    Validate.notNull(selector, "selector is null!");
    Validate.notNull(translationContext, "translationContext is null!");
    if (selector instanceof SelectorImpl) {
      return translate2Sql((SelectorImpl) selector, translationContext);
    } else {
      return translate2Sql((SetlectorImpl) selector, translationContext);
    }
  }

  protected String translate2Sql(SelectorImpl selector,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(translate2SelectClause(selector.getRecordSetOperator(),
        translationContext));
    sbuf.append(translate2FromClause(selector.getTables(), translationContext));
    if (selector.getWhere() != null) {
      sbuf.append(
          translate2WhereClause(selector.getWhere(), translationContext));
    }
    if (selector.getRecordSetOperator() instanceof GroupRecordSetOperator) {
      sbuf.append(translate2GroupbyClause(
          (GroupRecordSetOperator) selector.getRecordSetOperator(),
          translationContext));
    }
    if (selector.getHaving() != null) {
      sbuf.append(translate2HavingClause((HavingImpl) selector.getHaving(),
          translationContext));
    }
    if (selector.getOrder() != null) {
      sbuf.append(translate2OrderbyClause((OrderImpl) selector.getOrder(),
          translationContext));
    }
    if (selector.getLimit() != null) {
      sbuf.append(
          translate2LimitClause(selector.getLimit(), translationContext));
    }
    return sbuf.toString();
  }

  protected String translate2Sql(SetlectorImpl setlector,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    SetlectorMetadata setlectorMetadata = (SetlectorMetadata) setlector
        .getSelectorDefinition();
    int i = 0;
    for (Selector selector : setlector.getSets()) {
      if (i != 0) {
        sbuf.append(getCombinationType(setlectorMetadata.getCombinationType()));
        sbuf.append(" ");
        if (!setlectorMetadata.getColumns().isDistinct()) {
          sbuf.append("all ");
        }
      }
      sbuf.append(translate2Sql(selector));
      i++;
    }
    if (setlector.getOrder() != null) {
      sbuf.append(translate2OrderbyClause((OrderImpl) setlector.getOrder(),
          translationContext));
    }
    return sbuf.toString();
  }

  protected String getCombinationType(CombinationType combinationType) {
    if (combinationType.equals(CombinationType.UNION)) {
      return "union";
    } else if (combinationType.equals(CombinationType.INTERSECT)) {
      return "intersect";
    } else if (combinationType.equals(CombinationType.EXCEPT)) {
      return "except";
    } else if (combinationType.equals(CombinationType.SYMEXCEPT)) {
      return "symexcept";
    } else {
      return "complementation";
    }
  }

  @Override
  public String translate2Condition(Filter filter) {
    return translate2Condition(filter, new HashMap<String, Object>());
  }

  @Override
  public String translate2Condition(Filter filter,
      Map<String, Object> translationContext) {
    // TODO Auto-generated method stub
    Validate.notNull(filter, "filter is null!");
    return translateOperand(((Condition) filter).getOperand(),
        translationContext);
  }

  protected String translate2SelectClause(RecordSetOperator recordSetOperator,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("select ");
    if (recordSetOperator.getCache() != null)
      sbuf.append(translateCache(recordSetOperator.getCache()));
    if (recordSetOperator.getColumns().getColumnsMetadata().isDistinct()) {
      sbuf.append("distinct ");
    }
    sbuf.append(
        translateColumns(recordSetOperator.getColumns(), translationContext));
    return sbuf.toString();
  }

  @SuppressWarnings({ "rawtypes" })
  protected String translateCache(Cache cache) {
    StringBuffer sbuf = new StringBuffer();
    CacheMetadata cacheMetadata = cache.getCacheMetadata();
    sbuf.append("cache(");
    sbuf.append(cacheMetadata.getSize());
    sbuf.append(",");
    sbuf.append(cacheMetadata.getWashoutStrategy().toString().toLowerCase());
    sbuf.append(") ");
    return sbuf.toString();
  }

  protected String translate2LimitClause(Limit limit,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    sbuf.append("limit ");
    if (limitMetadata.getOffset() != 0) {
      sbuf.append(limitMetadata.getOffset());
      sbuf.append(",");
    }
    sbuf.append(limitMetadata.getValue());
    if (limitMetadata.isPercent()) {
      sbuf.append("%");
    }
    sbuf.append(" ");
    return sbuf.toString();
  }

  protected String translateColumns(Columns columns,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    int i = 0;
    for (Column column : columns.getColumns()) {
      if (column.isJustUsed4Order())
        continue;
      if (i != 0) {
        sbuf.append(", ");
      }

      sbuf.append(
          translateOperand(column.getOperand(), translationContext).trim());
      if (column.getColumnMetadata().isHasAlias()) {
        sbuf.append(" ");
        sbuf.append(column.getColumnMetadata().getName());
      }
      i++;
    }
    sbuf.append(" ");
    return sbuf.toString();
  }

  protected String translate2FromClause(Tables tables,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    boolean multiTables = false;
    sbuf.append("from ");
    if (tables.getTablesMetadata().getTables().size() > 1) {
      multiTables = true;
    }
    sbuf.append(translateQueryable(tables.getQueryable(), multiTables,
        translationContext));
    return sbuf.toString();
  }

  @SuppressWarnings({ "rawtypes" })
  protected String translateQueryable(Queryable queryable, boolean multiTables,
      Map<String, Object> translationContext) {
    if (queryable instanceof Table) {
      return translateTable((Table) queryable, translationContext);
    } else {
      return translateJoin((Join) queryable, multiTables, translationContext);
    }
  }

  protected String translateTable(Table table,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    TableMetadata tableMetadata = table.getTableMetadata();
    if (table instanceof SelectorTable) {
      SelectorTable selectorTable = (SelectorTable) table;
      sbuf.append("(");
      sbuf.append(translate2Sql(selectorTable.getSelector()));
      sbuf.append(") ");
    } else {
      sbuf.append(tableMetadata.getValue());
      sbuf.append(" ");
    }
    sbuf.append(tableMetadata.getName());
    sbuf.append(" ");
    return sbuf.toString();
  }

  protected String translateJoin(Join join, boolean multiTables,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    JoinMetadata joinMetadata = join.getJoinMetadata();
    String leftQuery = translateQueryable(join.getLeftQueryable(), multiTables,
        translationContext);
    if (!multiTables) {
      sbuf.append(leftQuery);
      sbuf.append(getJoinType(joinMetadata.getJoinType()));
      sbuf.append(" ");
    } else {
      sbuf.append(leftQuery.trim());
      sbuf.append(", ");
    }
    sbuf.append(translateQueryable(join.getRightQueryable(), multiTables,
        translationContext));
    if (!multiTables && join.getOn() != null) {
      sbuf.append("on ");
      sbuf.append(
          translateOperand(join.getOn().getOperand(), translationContext));
    }
    return sbuf.toString();
  }

  protected String getJoinType(JoinType joinType) {
    if (joinType.equals(JoinType.INNER)) {
      return "inner join";
    } else if (joinType.equals(JoinType.LEFT)) {
      return "left join";
    } else if (joinType.equals(JoinType.RIGHT)) {
      return "right join";
    } else {
      return "full join";
    }
  }

  protected String translate2WhereClause(Condition condition,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("where ");
    sbuf.append(translateOperand(condition.getOperand(), translationContext));
    return sbuf.toString();
  }

  protected String translateOperand(Operand operand,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        sbuf.append(translateLogicExpression(expression, translationContext));
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        sbuf.append(
            translateRelationExpression(expression, translationContext));
      } else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
        sbuf.append(
            translateArithmeticExpression(expression, translationContext));
      } else if (expression.getExpressionType() == ExpressionType.BITWISE) {
        sbuf.append(translateBitwiseExpression(expression, translationContext));
      } else {
        throw new IllegalArgumentException(StringFormater
            .format("Doesn't support operand with type '{}'!",
                expression.getExpressionType()));
      }
    } else if (operand instanceof ParenExpression) {
      ParenExpression parenExpression = (ParenExpression) operand;
      sbuf.append(
          translateParenExpression(parenExpression, translationContext));
    } else if (operand instanceof ColumnSelectorOperand) {
      ColumnSelectorOperand selectorOperand = (ColumnSelectorOperand) operand;
      sbuf.append(translate2Sql(selectorOperand.getColumnSelector()));
    } else if (operand instanceof ValueSelectorOperand) {
      ValueSelectorOperand selectorOperand = (ValueSelectorOperand) operand;
      sbuf.append("(");
      sbuf.append(translate2Sql(selectorOperand.getValueSelector()).trim());
      sbuf.append(") ");
    } else if (operand instanceof AbstractFunction) {
      AbstractFunction function = (AbstractFunction) operand;
      sbuf.append(translateFunction(function, translationContext));
    } else {
      sbuf.append(operand.getName());
      sbuf.append(" ");
    }
    return sbuf.toString();
  }

  protected String translateNotExpression(NotExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("not ");
    sbuf.append(
        translateOperand(expression.getRightOperand(), translationContext));
    return sbuf.toString();
  }

  protected String translateBinaryExpression(String operator, Operand lOperand,
      Operand rOperand, Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(translateOperand(lOperand, translationContext));
    sbuf.append(operator);
    sbuf.append(' ');
    sbuf.append(translateOperand(rOperand, translationContext));
    return sbuf.toString();
  }

  protected String translateLogicExpression(
      AbstractOperationExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (expression.getOperator() == LogicOperator.NOT) {
      sbuf.append(translateNotExpression((NotExpression) expression,
          translationContext));
    } else {
      if (expression.getOperator() == LogicOperator.AND)
        sbuf.append(
            translateBinaryExpression("and", expression.getLeftOperand(),
                expression.getRightOperand(), translationContext));
      else
        sbuf.append(translateBinaryExpression("or", expression.getLeftOperand(),
            expression.getRightOperand(), translationContext));
    }
    return sbuf.toString();
  }

  protected String translateRelationExpression(
      AbstractOperationExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (expression.getOperator() == RelationOperator.EQ) {
      sbuf.append(translateBinaryExpression("=", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.GT) {
      sbuf.append(translateBinaryExpression(">", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.GE) {
      sbuf.append(translateBinaryExpression(">=", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.LT) {
      sbuf.append(translateBinaryExpression("<", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.LE) {
      sbuf.append(translateBinaryExpression("<=", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.NE) {
      sbuf.append(translateBinaryExpression("<>", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.BETWEEN) {
      sbuf.append(translateBetweenExpression((BetweenExpression) expression,
          translationContext));
    } else if (expression.getOperator() == RelationOperator.LIKE) {
      sbuf.append(translateBinaryExpression("like", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.IN) {
      sbuf.append(
          translateInExpression((InExpression) expression, translationContext));
    } else if (expression.getOperator() == RelationOperator.IS) {
      sbuf.append(translateBinaryExpression("is", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.EXISTS) {
      sbuf.append(translateExistsExpression((ExistsExpression) expression,
          translationContext));
    } else {
      sbuf.append(
          translateOperand(expression.getRightOperand(), translationContext));
    }

    return sbuf.toString();
  }

  protected String translateParenExpression(ParenExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("(");
    sbuf.append(
        translateOperand(expression.getOperand(), translationContext).trim());
    sbuf.append(") ");
    return sbuf.toString();
  }

  protected String translateBetweenExpression(BetweenExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(
        translateOperand(expression.getLeftOperand(), translationContext));
    sbuf.append("between ");
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      sbuf.append(translateOperand(rOperand, translationContext));
      if (i == 0) {
        sbuf.append("and ");
        i++;
      }
    }
    return sbuf.toString();
  }

  protected String translateInExpression(InExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(
        translateOperand(expression.getLeftOperand(), translationContext));
    sbuf.append("in (");
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i != 0) {
        sbuf.append(", ");
      }
      sbuf.append(translateOperand(rOperand, translationContext).trim());
      i++;
    }
    sbuf.append(") ");
    return sbuf.toString();
  }

  protected String translateExistsExpression(ExistsExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("exists(");
    sbuf.append(
        translateOperand(expression.getRightOperand(), translationContext)
            .trim());
    sbuf.append(") ");
    return sbuf.toString();
  }

  protected String translateArithmeticExpression(
      AbstractOperationExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (expression.getOperator() == ArithmeticOperator.ADD) {
      sbuf.append(translateBinaryExpression("+", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == ArithmeticOperator.SUBTRACT) {
      sbuf.append(translateBinaryExpression("-", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == ArithmeticOperator.MULTIPLY) {
      sbuf.append(translateBinaryExpression("*", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == ArithmeticOperator.DIVIDE) {
      sbuf.append(translateBinaryExpression("/", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == ArithmeticOperator.MODULAR) {
      sbuf.append(translateBinaryExpression("%", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else {
      sbuf.append(translateBinaryExpression("^", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    }
    return sbuf.toString();
  }

  protected String translateBitwiseExpression(
      AbstractOperationExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (expression.getOperator() == BitwiseOperator.LSHIFT) {
      sbuf.append(translateBinaryExpression("<<", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == BitwiseOperator.RSHIFT) {
      sbuf.append(translateBinaryExpression(">>", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == BitwiseOperator.BITWISEAND) {
      sbuf.append(translateBinaryExpression("&", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == BitwiseOperator.BITWISEOR) {
      sbuf.append(translateBinaryExpression("|", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == BitwiseOperator.BITWISEXOR) {
      sbuf.append(translateBinaryExpression("^", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else {
      sbuf.append("~ ");
      sbuf.append(
          translateOperand(expression.getRightOperand(), translationContext));
    }
    return sbuf.toString();
  }

  protected String translateFunction(AbstractFunction function,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    FunctionTranslator functionTranslator = functionTranslators
        .get(function.getName());
    if (functionTranslator == null) {
      sbuf.append(function.toString());
    } else {
      sbuf.append(functionTranslator.translate(function));
    }
    sbuf.append(" ");
    return sbuf.toString();
  }

  protected String translate2GroupbyClause(
      GroupRecordSetOperator groupRecordSetOperator,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    List<GroupMetadata> groupMetadatas = groupRecordSetOperator
        .getGroupMetadatas();
    sbuf.append("group by ");
    int i = 0;
    for (Column column : groupRecordSetOperator.getGroupColumns()) {
      if (i != 0) {
        sbuf.append(",");
      }
      GroupMetadata groupMetadata = groupMetadatas.get(i);
      try {
        int index = Integer.valueOf(groupMetadata.getColumn());
        sbuf.append(index);
      } catch (Throwable t) {
        sbuf.append(translateOperand(column.getOperand(), translationContext));
      }
      i++;
    }
    sbuf.append(" ");
    return sbuf.toString();
  }

  protected String translate2HavingClause(HavingImpl having,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("having ");
    sbuf.append(translateOperand(having.getCondition().getOperand(),
        translationContext));
    return sbuf.toString();
  }

  protected String translate2OrderbyClause(OrderImpl order,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("order by ");
    Column[] columns = order.getOrderColumns();
    OrderType[] orderTypes = order.getOrderTypes();
    List<OrderMetadata> orderMetadatas = order.getOrderMetadatas();
    for (int i = 0; i < columns.length; i++) {
      if (i != 0) {
        sbuf.append(", ");
      }
      OrderMetadata orderMetadata = orderMetadatas.get(i);
      try {
        int index = Integer.valueOf(orderMetadata.getColumn());
        sbuf.append(index);
      } catch (Throwable t) {
        sbuf.append(orderMetadata.getColumn());
      }
      sbuf.append(" ");
      sbuf.append(orderTypes[i].name().toLowerCase());
    }
    sbuf.append(" ");
    return sbuf.toString();
  }

  @Override
  public void addFunctionTranslator(FunctionTranslator functionTranslator) {
    // TODO Auto-generated method stub
    Validate.notNull(functionTranslator, "functionTranslator is null!");
    functionTranslators
        .put(functionTranslator.getFunctionName(), functionTranslator);
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
