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
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.operand.PseudoOperand;
import org.datayoo.moql.operand.constant.Constant;
import org.datayoo.moql.operand.constant.LongConstant;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.expression.arithmetic.ModularExpression;
import org.datayoo.moql.operand.expression.logic.LogicOperator;
import org.datayoo.moql.operand.expression.logic.NotExpression;
import org.datayoo.moql.operand.expression.relation.*;
import org.datayoo.moql.util.StringFormater;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class MongoDBTranslator implements SqlTranslator {

  protected Map<String, FunctionTranslator> functionTranslators = new HashMap<String, FunctionTranslator>();

  @Override
  public String translate2Sql(Selector selector) {
    return translate2Sql(selector, new HashMap<String, Object>());
  }

  public String translate2Sql(Selector selector,
      Map<String, Object> translationContext) {
    Validate.notNull(selector, "selector is null!");
    if (selector instanceof SelectorImpl) {
      return translate2Sql((SelectorImpl) selector, translationContext);
    } else {
      throw new IllegalArgumentException(
          "Just support the selector type is SelectorImpl");
    }
  }

  @Override
  public String translate2Condition(Filter filter) {
    throw new UnsupportedOperationException("");
  }

  @Override
  public String translate2Condition(Filter filter,
      Map<String, Object> translationContext) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("");
  }

  protected String translate2Sql(SelectorImpl selector,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("db.");
    sbuf.append(getTableName(selector.getTables(), translationContext));
    if (isDistinct(selector.getRecordSetOperator())) {
      sbuf.append(translate2DistinctClause(selector.getRecordSetOperator()));
    } else {
      sbuf.append(".find(");
      String condition = null;
      String projection = null;
      if (selector.getWhere() != null) {
        condition = translateCondition(selector.getWhere(), translationContext);
      }
      //	condition isn't '{}'
      if (condition.length() > 2) {
        sbuf.append(condition);
      }
      projection = translateProjection(selector, translationContext);
      if (projection.length() > 0) {
        sbuf.append(" , ");
        sbuf.append(projection);
      }
      sbuf.append(")");
      if (selector.getOrder() != null) {
        sbuf.append(translate2SortClause((OrderImpl) selector.getOrder(),
            translationContext));
      }
      if (selector.getLimit() != null) {
        sbuf.append(
            translate2LimitClause(selector.getLimit(), translationContext));
      }
    }
    return sbuf.toString();
  }

  @SuppressWarnings("rawtypes")
  protected String getTableName(Tables tables,
      Map<String, Object> translationContext) {
    Queryable queryable = tables.getQueryable();
    TableMetadata tableMetadata;
    if (queryable instanceof Table) {
      tableMetadata = ((Table) queryable).getTableMetadata();
    } else {
      Join join = (Join) queryable;
      tableMetadata = (TableMetadata) join.getJoinMetadata().getLQueryable();
      ;
    }
    return tableMetadata.getName();
  }

  protected boolean isDistinct(RecordSetOperator recordSetOperator) {
    return recordSetOperator.getColumns().getColumnsMetadata().isDistinct();
  }

  protected String translate2DistinctClause(
      RecordSetOperator recordSetOperator) {
    StringBuffer sbuf = new StringBuffer();
    ColumnsMetadata columnsMetadata = recordSetOperator.getColumns()
        .getColumnsMetadata();
    if (columnsMetadata.isDistinct()) {
      sbuf.append(".distinct(");
      sbuf.append(columnsMetadata.getColumns().get(0).getName());
      sbuf.append(")");
    }
    return sbuf.toString();
  }

  protected String translateColumns(Columns columns,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (columns.getColumns().size() == 1) {
      //	identifier.* --> PseudoOperand
      if (columns.getColumns().get(0).getOperand() instanceof PseudoOperand) {
        return sbuf.toString();
      }
    }
    int i = 0;
    sbuf.append('{');
    for (Column column : columns.getColumns()) {
      if (column.isJustUsed4Order())
        continue;
      if (i != 0) {
        sbuf.append(", ");
      }
      sbuf.append(column.getColumnMetadata().getName());
      sbuf.append(":1");
      i++;
    }
    sbuf.append('}');
    return sbuf.toString();
  }

  protected String translateCondition(Condition condition,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(translateOperand(condition.getOperand(), translationContext));
    sbuf.append('}');
    return sbuf.toString();
  }

  protected String translateProjection(SelectorImpl selector,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append('}');
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
      } else {
        throw new IllegalArgumentException(StringFormater
            .format("Doesn't support operand with type '{}'!",
                expression.getExpressionType()));
      }
    } else if (operand instanceof Constant) {
      sbuf.append(operand.getName());
    } else {
      throw new IllegalArgumentException(StringFormater
          .format("Doesn't support operand with type '{}'!",
              operand.getOperandType()));
    }
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
            translateLogicExpression("$and", expression.getLeftOperand(),
                expression.getRightOperand(), translationContext));
      else
        sbuf.append(translateLogicExpression("$or", expression.getLeftOperand(),
            expression.getRightOperand(), translationContext));
    }
    return sbuf.toString();
  }

  protected String translateNotExpression(NotExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    Operand operand = expression.getRightOperand();
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression rExpression = (AbstractOperationExpression) expression
          .getRightOperand();
      if (rExpression.getExpressionType() == ExpressionType.LOGIC) {
        throw new IllegalArgumentException(
            "Logic expression doesn't support 'not' operator!");
      }
    } else {
      throw new IllegalArgumentException(StringFormater
          .format("The operand with type '{}' doesn't support 'not' operator!",
              operand.getOperandType()));
    }
    if (expression.getRightOperand() instanceof InExpression) {
      translateInExpression("$nin", (InExpression) expression.getRightOperand(),
          translationContext);
    } else if (expression.getRightOperand() instanceof ExistsExpression) {
      translateExistsExpression((ExistsExpression) expression.getRightOperand(),
          false, translationContext);
    } else {
      String operandExpression = translateOperand(expression.getRightOperand(),
          translationContext);
      int index = operandExpression.indexOf(':');
      sbuf.append(operandExpression.substring(0, index));
      sbuf.append(" {$not: ");
      sbuf.append(operandExpression.substring(index));
      sbuf.append('}');
    }
    return sbuf.toString();
  }

  protected String translateLogicExpression(String operator, Operand lOperand,
      Operand rOperand, Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("{");
    sbuf.append(operator);
    sbuf.append(": [");
    sbuf.append(translateOperand(lOperand, translationContext));
    sbuf.append(',');
    sbuf.append(translateOperand(rOperand, translationContext));
    sbuf.append("]}");
    return sbuf.toString();
  }

  protected String translateBinaryExpression(String operator, Operand lOperand,
      Operand rOperand, Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(translateOperand(lOperand, translationContext));
    sbuf.append('{');
    sbuf.append(operator);
    sbuf.append(": ");
    sbuf.append(translateOperand(rOperand, translationContext));
    sbuf.append("}}");
    return sbuf.toString();
  }

  protected String translateRelationExpression(
      AbstractOperationExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    if (expression.getOperator() == RelationOperator.EQ) {
      sbuf.append(translateEqualExpression((EqualExpression) expression,
          translationContext));
    } else if (expression.getOperator() == RelationOperator.GT) {
      sbuf.append(translateBinaryExpression("$gt", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.GE) {
      sbuf.append(translateBinaryExpression("$gte", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.LT) {
      sbuf.append(translateBinaryExpression("$lt", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.LE) {
      sbuf.append(translateBinaryExpression("$lte", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.NE) {
      sbuf.append(translateBinaryExpression("$ne", expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.BETWEEN) {
      sbuf.append(translateBetweenExpression((BetweenExpression) expression,
          translationContext));
    } else if (expression.getOperator() == RelationOperator.LIKE) {
      sbuf.append(translateLikeExpression((LikeExpression) expression,
          translationContext));
    } else if (expression.getOperator() == RelationOperator.IN) {
      sbuf.append(translateInExpression("$in", (InExpression) expression,
          translationContext));
    } else if (expression.getOperator() == RelationOperator.IS) {
      sbuf.append(translateBinaryExpression(null, expression.getLeftOperand(),
          expression.getRightOperand(), translationContext));
    } else if (expression.getOperator() == RelationOperator.EXISTS) {
      sbuf.append(translateExistsExpression((ExistsExpression) expression, true,
          translationContext));
    } else {
      sbuf.append(
          translateOperand(expression.getRightOperand(), translationContext));
    }

    return sbuf.toString();
  }

  protected String translateEqualExpression(EqualExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    Operand lOperand = expression.getLeftOperand();
    Operand rOperand = expression.getRightOperand();
    if (lOperand instanceof ModularExpression) {
      ModularExpression modularExpression = (ModularExpression) lOperand;
      return translateModularExpression(modularExpression.getLeftOperand(),
          (LongConstant) modularExpression.getRightOperand(),
          (LongConstant) rOperand, translationContext);
    }
    sbuf.append('{');
    sbuf.append(translateOperand(lOperand, translationContext));
    sbuf.append(": ");
    sbuf.append(translateOperand(rOperand, translationContext));
    sbuf.append('}');
    return sbuf.toString();
  }

  protected String translateModularExpression(Operand operand,
      LongConstant divisor, LongConstant remainder,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(translateOperand(operand, translationContext));
    sbuf.append(": {$mod : [");
    sbuf.append(divisor.getValue());
    sbuf.append(',');
    sbuf.append(remainder.getValue());
    sbuf.append("]}");
    return sbuf.toString();
  }

  protected String translateLikeExpression(LikeExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(
        translateOperand(expression.getLeftOperand(), translationContext));
    sbuf.append(": ");
    String pattern = translateOperand(expression.getRightOperand(),
        translationContext);
    pattern.substring(1, pattern.length() - 1);
    sbuf.append('/');
    if (pattern.length() > 1) {
      if (pattern.charAt(0) != '%') {
        sbuf.append('^');
      }
    }
    sbuf.append(pattern);
    sbuf.append("/}");
    return sbuf.toString();
  }

  protected String translateBetweenExpression(BetweenExpression expression,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(
        translateOperand(expression.getLeftOperand(), translationContext));
    sbuf.append(": {");
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i == 0) {
        sbuf.append("$gte: ");
      } else {
        sbuf.append(", $lte: ");
      }
      sbuf.append(translateOperand(rOperand, translationContext));
    }
    sbuf.append("}}");
    return sbuf.toString();
  }

  protected String translateInExpression(String operator,
      InExpression expression, Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(
        translateOperand(expression.getLeftOperand(), translationContext));
    sbuf.append(": {");
    sbuf.append(operator);
    sbuf.append(": [");
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i != 0) {
        sbuf.append(", ");
      }
      sbuf.append(translateOperand(rOperand, translationContext).trim());
      i++;
    }
    sbuf.append("]}}");
    return sbuf.toString();
  }

  protected String translateExistsExpression(ExistsExpression expression,
      boolean exist, Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('{');
    sbuf.append(
        translateOperand(expression.getRightOperand(), translationContext)
            .trim());
    sbuf.append(": {$exist: ");
    if (exist)
      sbuf.append("true");
    else
      sbuf.append("false");
    sbuf.append("}}");
    return sbuf.toString();
  }

  @SuppressWarnings("rawtypes")
  protected boolean hasJoin(SelectorImpl selector) {
    Queryable queryable = selector.getTables().getQueryable();
    if (queryable instanceof Join)
      return true;
    return false;
  }

  @SuppressWarnings({ "rawtypes" })
  protected String translateQueryable(Queryable queryable, boolean multiTables,
      Map<String, Object> translationContext) {
    if (queryable instanceof Table) {
      return null; //translateTable((Table)queryable);
    } else {
      return translateJoin((Join) queryable, multiTables, translationContext);
    }
  }

  protected String translateJoin(Join join, boolean multiTables,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    JoinMetadata joinMetadata = join.getJoinMetadata();
    String leftQuery = translateQueryable(join.getLeftQueryable(), multiTables,
        translationContext);
    if (!multiTables) {
      sbuf.append(leftQuery);
      //sbuf.append(getJoinType(joinMetadata.getJoinType()));
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

  protected String translate2SortClause(OrderImpl order,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(".sort({");
    Column[] columns = order.getOrderColumns();
    OrderType[] orderTypes = order.getOrderTypes();
    List<OrderMetadata> orderMetadatas = order.getOrderMetadatas();
    int type = 0;
    for (int i = 0; i < columns.length; i++) {
      if (i != 0) {
        sbuf.append(", ");
      }
      OrderMetadata orderMetadata = orderMetadatas.get(i);
      sbuf.append(orderMetadata.getColumn());
      sbuf.append(": ");
      if (orderTypes[i] == OrderType.ASC) {
        type = 1;
      } else {
        type = -1;
      }
      sbuf.append(type);
    }
    sbuf.append("})");
    return sbuf.toString();
  }

  protected String translate2LimitClause(Limit limit,
      Map<String, Object> translationContext) {
    StringBuffer sbuf = new StringBuffer();
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    sbuf.append(".limit(");
    sbuf.append(limitMetadata.getValue());
    sbuf.append(')');
    if (limitMetadata.getOffset() != 0) {
      sbuf.append(".skip(");
      sbuf.append(limitMetadata.getOffset());
      sbuf.append(")");
    }
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
