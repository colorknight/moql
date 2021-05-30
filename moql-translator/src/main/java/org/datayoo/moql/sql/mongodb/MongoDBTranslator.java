package org.datayoo.moql.sql.mongodb;

import com.google.gson.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.Validate;
import org.datayoo.moql.Filter;
import org.datayoo.moql.MoqlTranslationException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.Selector;
import org.datayoo.moql.core.*;
import org.datayoo.moql.core.group.GroupRecordSetOperator;
import org.datayoo.moql.core.join.LeftJoin;
import org.datayoo.moql.metadata.ColumnMetadata;
import org.datayoo.moql.metadata.LimitMetadata;
import org.datayoo.moql.metadata.OrderType;
import org.datayoo.moql.operand.constant.BooleanConstant;
import org.datayoo.moql.operand.constant.DoubleConstant;
import org.datayoo.moql.operand.constant.LongConstant;
import org.datayoo.moql.operand.constant.StringConstant;
import org.datayoo.moql.operand.expression.AbstractOperationExpression;
import org.datayoo.moql.operand.expression.ExpressionType;
import org.datayoo.moql.operand.expression.ParenExpression;
import org.datayoo.moql.operand.expression.logic.AndExpression;
import org.datayoo.moql.operand.expression.logic.LogicOperator;
import org.datayoo.moql.operand.expression.logic.NotExpression;
import org.datayoo.moql.operand.expression.member.MemberVariableExpression;
import org.datayoo.moql.operand.expression.relation.*;
import org.datayoo.moql.operand.function.AbstractFunction;
import org.datayoo.moql.operand.function.Function;
import org.datayoo.moql.operand.selector.ColumnSelectorOperand;
import org.datayoo.moql.operand.variable.Variable;
import org.datayoo.moql.sql.FunctionTranslator;
import org.datayoo.moql.sql.SqlTranslator;
import org.datayoo.moql.util.StringFormater;

import java.util.*;

public class MongoDBTranslator implements SqlTranslator {
  // Json Element
  public final String JE_QUERY_TYPE = "queryType"; // find,aggregate

  public final String JE_QUERY_COLLECTION = "queryCollection"; //

  public static final Set<String> exceptionFunctions = new HashSet();

  static {
    exceptionFunctions.add("ISODate");
  }

  protected Map<String, MongoFunctionTranslator> functionTranslators = new HashMap<String, MongoFunctionTranslator>();

  {
    functionTranslators.put(TextTranslator.FUNCTION_NAME, new TextTranslator());
  }

  protected static Gson gson = new GsonBuilder().serializeNulls()
      .setPrettyPrinting().create();

  @Override
  public String translate2Sql(Selector selector) {
    return translate2Sql(selector, new HashMap<String, Object>());
  }

  @Override
  public String translate2Sql(Selector selector,
      Map<String, Object> translationContext) {
    Validate.notNull(selector, "selector is null!");
    if (selector instanceof SelectorImpl) {
      return translate2Sql((SelectorImpl) selector);
    } else {
      return translate2Sql((SetlectorImpl) selector, translationContext);
    }
  }

  protected String translate2Sql(SelectorImpl selector) {
    checkGrammer(selector);
    JsonArray jsonArray = new JsonArray();
    JsonObject queryType = new JsonObject();
    if (isAggregations(selector)) {
      queryType.addProperty(JE_QUERY_TYPE, "aggregate");
      jsonArray.add(queryType);
      translateTable(selector.getTables(), jsonArray);
      JsonArray aggs = new JsonArray();
      translate2Aggs(selector, aggs);
      putObject(jsonArray, "aggs", aggs);
    } else {
      queryType.addProperty(JE_QUERY_TYPE, "find");
      jsonArray.add(queryType);
      translateTable(selector.getTables(), jsonArray);
      translate2Query(selector, jsonArray);
    }
    return gson.toJson(jsonArray);
  }

  protected void checkGrammer(SelectorImpl selector) {
    if (selector.getTables().getTablesMetadata().getTables().size() > 1) {
      throw new MoqlTranslationException("The sql querys more than 1 table!");
    }
  }

  protected boolean isAggregations(SelectorImpl selector) {
    // mongodb lookup clause
    if (isAggregations(selector.getTables()))
      return true;
    RecordSetOperator recordSetOperator = selector.getRecordSetOperator();
    if (recordSetOperator instanceof Group)
      return true;
    Columns columns = recordSetOperator.getColumns();
    if (columns.getColumnsMetadata().isDistinct())
      throw new UnsupportedOperationException(
          "Unsupported 'distinct' clause!Please use 'groupBy' clause instead!");
    return false;
  }

  protected boolean isAggregations(Tables tables) {
    // $lookup
    if (tables.getQueryable() instanceof LeftJoin) {
      LeftJoin leftJoin = (LeftJoin) tables.getQueryable();
      if (leftJoin.getOn() == null) {
        throw new UnsupportedOperationException(
            "Unsupported 'left join' clause without 'on' clause!");
      }
      if (leftJoin.getRightQueryable() instanceof Table && leftJoin.getOn()
          .getOperand() instanceof EqualExpression) {
        return true;
      }
    }
    return false;
  }

  protected void translateTable(Tables tables, JsonArray jsonArray) {
    String tableName = null;
    if (tables.getQueryable() instanceof Table) {
      Table table = (Table) tables.getQueryable();
      tableName = table.getTableMetadata().getName();
    } else {
      Join join = (Join) tables.getQueryable();
      tableName = ((Table) join.getLeftQueryable()).getTableMetadata()
          .getName();
    }
    JsonPrimitive jp = new JsonPrimitive(tableName);
    putObject(jsonArray, JE_QUERY_COLLECTION, jp);
  }

  protected void translate2Query(SelectorImpl selector, JsonArray jsonArray) {
    translateProjectionClause(selector.getRecordSetOperator(), null, jsonArray);
    if (selector.getWhere() != null) {
      translateWhereClause(selector.getWhere(), jsonArray);
    }
    if (selector.getOrder() != null) {
      translateOrderClause(selector.getOrder(), jsonArray);
    }
    if (selector.getLimit() != null) {
      translateLimitClause(selector.getLimit(), jsonArray);
    }
  }

  protected void translateProjectionClause(RecordSetOperator recordSetOperator,
      String leftJoinTableAlias, JsonArray jsonArray) {
    if (isSelectAll(recordSetOperator))
      return;
    JsonObject projection = new JsonObject();
    boolean idProjected = false;
    for (Column column : recordSetOperator.getColumns().getColumns()) {
      if (column.getOperand() instanceof ColumnSelectorOperand)
        throw new UnsupportedOperationException(
            "Unsupported nested selector in select clause!");
      if (leftJoinTableAlias != null) {
        if (isLeftJoinProjection(column.getColumnMetadata(),
            leftJoinTableAlias))
          continue;
      }
      String value = column.getColumnMetadata().getName();
      int index = value.indexOf('(');
      if (index == -1) {
        index = value.indexOf('.');
        value = value.substring(index + 1);
      }
      if (value.equals("_id"))
        idProjected = true;
      projection.addProperty(value, 1);
    }
    if (!idProjected) {
      // 排除_id
      projection.addProperty("_id", 0);
    }
    JsonObject jo = new JsonObject();
    jo.add("$project", projection);
    jsonArray.add(jo);
  }

  protected boolean isLeftJoinProjection(ColumnMetadata columnMetadata,
      String leftJoinTableAlias) {
    // field of leftjoin's table, removed
    String name = columnMetadata.getName();
    int index = name.indexOf('.');
    if (index != -1) {
      String prefix = name.substring(0, index);
      if (prefix.equals(leftJoinTableAlias))
        return true;
    }
    return false;
  }

  protected boolean isSelectAll(RecordSetOperator recordSetOperator) {
    Columns columns = recordSetOperator.getColumns();
    if (columns.getColumns().size() == 1) {
      Column column = columns.getColumns().get(0);
      String value = column.getColumnMetadata().getValue();
      if (value.endsWith(".*"))
        return true;
    }
    return false;
  }

  protected void translateWhereClause(Condition condition,
      JsonArray jsonArray) {
    JsonObject whereClause = new JsonObject();
    translateOperand(condition.getOperand(), whereClause);
    JsonObject jo = new JsonObject();
    jo.add("$match", whereClause);
    jsonArray.add(jo);
  }

  protected void translateLimitClause(Limit limit, JsonArray jsonArray) {
    if (limit == null) {
      return;
    }
    JsonObject jsonObject = new JsonObject();
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    jsonObject.addProperty("$limit", limitMetadata.getValue());
    jsonArray.add(jsonObject);
    if (limitMetadata.getOffset() != 0) {
      jsonObject = new JsonObject();
      jsonObject.addProperty("$skip", limitMetadata.getOffset());
      jsonArray.add(jsonObject);
    }
  }

  protected void translateOrderClause(Order order, JsonArray jsonArray) {
    if (order == null)
      return;
    JsonObject sortObject = new JsonObject();
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    OrderType[] orderTypes = orderImpl.getOrderTypes();
    for (int i = 0; i < columns.length; i++) {
      int o = 1;
      if (orderTypes[i].name().equalsIgnoreCase("desc"))
        o = -1;
      translateOrderColumn(columns[i], o, sortObject);
    }
    JsonObject jo = new JsonObject();
    jo.add("$sort", sortObject);
    jsonArray.add(jo);
  }

  protected void translateOrderColumn(Column column, int od,
      JsonObject sortObject) {
    if (column.getOperand() instanceof Function) {
      translateFunction((AbstractFunction) column.getOperand(), sortObject);
    } else {
      sortObject.addProperty(getFieldName(column.getOperand().getName()), od);
    }
  }

  protected void translate2Aggs(SelectorImpl selector, JsonArray jsonArray) {
    boolean leftJoin = isLeftJoin(selector.getTables());
    if (leftJoin) {
      translateLeftJoin(selector.getTables(), jsonArray);
      translateProjectionClause(selector.getRecordSetOperator(),
          getLeftJoinTableAlias(selector.getTables()), jsonArray);
    }
    if (selector.getWhere() != null) {
      translateWhereClause(selector.getWhere(), jsonArray);
    }
    if (!leftJoin) {
      translateGroupClause(selector, jsonArray);
    }
    if (selector.getHaving() != null) {
      translateHavingClause((HavingImpl) selector.getHaving(), jsonArray);
    }
    if (selector.getOrder() != null) {
      translateOrderClause(selector.getOrder(), jsonArray);
    }
    if (selector.getLimit() != null) {
      translateLimitClause(selector.getLimit(), jsonArray);
    }
  }

  protected boolean isLeftJoin(Tables tables) {
    if (tables.getQueryable() instanceof LeftJoin) {
      return true;
    }
    return false;
  }

  protected String getLeftJoinTableAlias(Tables tables) {
    LeftJoin leftJoin = (LeftJoin) tables.getQueryable();
    Table table = (Table) leftJoin.getLeftQueryable();
    return table.getTableMetadata().getValue();
  }

  protected void translateLeftJoin(Tables tables, JsonArray jsonArray) {
    LeftJoin leftJoin = (LeftJoin) tables.getQueryable();
    Table table = (Table) leftJoin.getRightQueryable();
    EqualExpression expression = (EqualExpression) leftJoin.getOn()
        .getOperand();
    JsonObject lookup = new JsonObject();
    lookup.addProperty("from", table.getTableMetadata().getValue());
    lookup.addProperty("localField",
        getFieldName(expression.getLeftOperand().getName()));
    lookup.addProperty("foreignField",
        getFieldName(expression.getRightOperand().getName()));
    lookup.addProperty("as", table.getTableMetadata().getValue());
    putObject(jsonArray, "$lookup", lookup);
  }

  protected String getFieldName(String name) {
    int inx = name.lastIndexOf('.');
    if (inx != -1) {
      return name.substring(inx + 1);
    }
    return name;
  }

  protected void translateGroupClause(SelectorImpl selector,
      JsonArray jsonArray) {
    GroupRecordSetOperator groupRecordSetOperator = (GroupRecordSetOperator) selector
        .getRecordSetOperator();
    Column[] columns = groupRecordSetOperator.getGroupColumns();
    JsonObject group = new JsonObject();
    translateGroupColumns(columns, group);
    columns = groupRecordSetOperator.getNonGroupColumns();
    for (int i = 0; i < columns.length; i++) {
      if (columns[i] == null)
        continue;
      JsonObject func = new JsonObject();
      translateFunction((AbstractFunction) columns[i].getOperand(), func);
      group.add(getFieldName(columns[i].getColumnMetadata().getName()), func);
    }
    putObject(jsonArray, "$group", group);
  }

  protected void translateGroupColumns(Column[] columns, JsonObject group) {
    JsonObject jo = new JsonObject();
    for (int i = 0; i < columns.length; i++) {
      ColumnMetadata columnMetadata = columns[i].getColumnMetadata();
      jo.add(getFieldName(columnMetadata.getName()),
          translateUnaryOperand(columns[i].getOperand()));
    }
    group.add("_id", jo);
  }

  protected void putObject(JsonElement jsonElement, String name,
      JsonElement valueJson) {
    if (jsonElement instanceof JsonObject) {
      ((JsonObject) jsonElement).add(name, valueJson);
    } else {
      JsonObject jo = new JsonObject();
      jo.add(name, valueJson);
      ((JsonArray) jsonElement).add(jo);
    }
  }

  protected void translateOperand(Operand operand, JsonElement jsonElement) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        translateLogicExpression(expression, jsonElement);
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        translateRelationExpression(expression, jsonElement);
      } else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
        throw new MoqlTranslationException(StringFormater
            .format("The expression '{}' does not support!",
                expression.getExpressionType().toString()));
      }
    } else if (operand instanceof ParenExpression) {
      ParenExpression parenExpression = (ParenExpression) operand;
      translateParenExpression(parenExpression, jsonElement);
    } else if (operand instanceof AbstractFunction) {
      AbstractFunction function = (AbstractFunction) operand;
      translateFunction(function, jsonElement);
    } else {
      throw new MoqlTranslationException(StringFormater
          .format("The operand '{}' does not support!",
              operand.getOperandType().toString()));
    }
  }

  protected void translateLogicExpression(
      AbstractOperationExpression expression, JsonElement jsonElement) {
    if (expression.getOperator() == LogicOperator.NOT) {
      translateNotExpression((NotExpression) expression, jsonElement);
    } else {
      if (expression.getOperator() == LogicOperator.AND) {
        translateLogicBinaryExpression("$and", expression.getLeftOperand(),
            expression.getRightOperand(), jsonElement);
      } else {
        translateLogicBinaryExpression("$or", expression.getLeftOperand(),
            expression.getRightOperand(), jsonElement);
      }
    }
  }

  protected void translateNotExpression(NotExpression expression,
      JsonElement jsonElement) {
    if (!translateNotOperand(expression.getRightOperand(), jsonElement)) {
      throw new UnsupportedOperationException(String
          .format("Does't support NotExpression as '%s'!",
              expression.toString()));
    }
  }

  protected boolean translateNotOperand(Operand operand,
      JsonElement jsonElement) {
    if (operand instanceof IsExpression) {
      IsExpression isExpression = (IsExpression) operand;
      translateIsExpression(isExpression.getLeftOperand(), jsonElement, true);
    } else if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        if (expression.getOperator() == LogicOperator.AND) {
          JsonArray norArray = new JsonArray();
          if (translate2Nor((AndExpression) expression, norArray)) {
            putObject(jsonElement, "$nor", norArray);
            return true;
          }
        }
        return false;
      } else if (expression.getExpressionType() == ExpressionType.RELATION
          && expression.getOperator() == RelationOperator.IN) {
        translateInExpression("$nin", (InExpression) expression, jsonElement);
      } else {
        JsonObject tmp = new JsonObject();
        translateOperand(operand, tmp);
        repackNotExpression(tmp, jsonElement);
      }
    } else if (operand instanceof ParenExpression) {
      operand = ((ParenExpression) operand).getOperand();
      return translateNotOperand(operand, jsonElement);
    } else {
      return false;
    }
    return true;
  }

  protected void repackNotExpression(JsonObject tmp, JsonElement jsonElement) {
    Map.Entry<String, JsonElement> entry = tmp.entrySet().iterator().next();
    JsonObject field = new JsonObject();
    field.add("$not", entry.getValue());
    putObject(jsonElement, entry.getKey(), field);
  }

  protected boolean translate2Nor(AndExpression andExpression,
      JsonArray norArray) {
    if (!translate2Nor(andExpression.getLeftOperand(), norArray))
      return false;
    return translate2Nor(andExpression.getRightOperand(), norArray);
  }

  protected boolean translate2Nor(Operand operand, JsonArray norArray) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        if (expression.getOperator() != LogicOperator.AND) {
          return false;
        } else {
          if (!translate2Nor((AndExpression) expression, norArray))
            return false;
        }
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        translateOperand(expression, norArray);
      }
    } else {
      return false;
    }
    return true;
  }

  protected void translateLogicBinaryExpression(String operator,
      Operand lOperand, Operand rOperand, JsonElement jsonElement) {
    JsonArray logicArray = new JsonArray();
    translateOperand(lOperand, logicArray);
    translateOperand(rOperand, logicArray);
    putObject(jsonElement, operator, logicArray);
  }

  protected void translateRelationExpression(
      AbstractOperationExpression expression, JsonElement jsonElement) {
    if (expression.getOperator() == RelationOperator.EQ) {
      translateRelationExpression("$eq", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.GT) {
      translateRelationExpression("$gt", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.GE) {
      translateRelationExpression("$gte", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.LT) {
      translateRelationExpression("$lt", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.LE) {
      translateRelationExpression("$lte", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.NE) {
      translateRelationExpression("$ne", expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.BETWEEN) {
      translateBetweenExpression((BetweenExpression) expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.LIKE) {
      translateLikeExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.IN) {
      translateInExpression("$in", (InExpression) expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.IS) {
      translateIsExpression(expression.getLeftOperand(), jsonElement, false);
    } else if (expression.getOperator() == RelationOperator.EXISTS) {
      translateExistsExpression((ExistsExpression) expression, jsonElement);
    } else {
      translateOperand(expression.getRightOperand(), jsonElement);
    }
  }

  protected void translateRelationExpression(String operator,
      AbstractOperationExpression expression, JsonElement jsonElement) {
    JsonObject jo = new JsonObject();
    jo.add(operator, translateUnaryOperand(expression.getRightOperand()));
    putObject(jsonElement, getFieldName(expression.getLeftOperand().getName()),
        jo);
  }

  protected void translateParenExpression(ParenExpression expression,
      JsonElement jsonElement) {
    translateOperand(expression.getOperand(), jsonElement);
  }

  protected JsonElement translateUnaryOperand(Operand operand) {
    String name = operand.getName();
    if (operand instanceof MemberVariableExpression) {
      int index = name.indexOf('.');
      if (index != -1) {
        return new JsonPrimitive("$" + name.substring(index + 1));
      }
    } else if (operand instanceof Variable) {
      return new JsonPrimitive("$" + name);
    } else if (operand instanceof StringConstant) {
      return new JsonPrimitive(name.substring(1, name.length() - 1));
    } else if (operand instanceof LongConstant) {
      return new JsonPrimitive(Long.valueOf(name));
    } else if (operand instanceof DoubleConstant) {
      return new JsonPrimitive(Double.valueOf(name));
    } else if (operand instanceof BooleanConstant) {
      return new JsonPrimitive(Boolean.valueOf(name));
    } else if (operand instanceof AbstractFunction) {
      if (!exceptionFunctions.contains(operand.getName())) {
        JsonObject function = new JsonObject();
        translateFunction((AbstractFunction) operand, function);
        return function;
      } else {
        return new JsonPrimitive(operand.toString().replace('\'', '\"'));
      }
    }
    return new JsonPrimitive(name);
  }

  protected void translateBetweenExpression(BetweenExpression expression,
      JsonElement jsonElement) {
    JsonObject cmp = new JsonObject();
    int i = 0;
    for (Operand rOperand : expression.getrOperands()) {
      if (i == 0) {
        cmp.add("$gte", translateUnaryOperand(rOperand));
        i++;
      } else {
        cmp.add("$lt", translateUnaryOperand(rOperand));
      }
    }
    putObject(jsonElement, getFieldName(expression.getLeftOperand().getName()),
        cmp);
  }

  protected void translateLikeExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonElement) {
    JsonObject regex = new JsonObject();
    regex.addProperty("$regex", LikeExpression
        .translatePattern2Regex(rOperand.operate(null).toString()));
    JsonElement je = regex.get("$regex");
    StringBuilder sbud = new StringBuilder();
    sbud.append("/");
    sbud.append(je.getAsString());
    sbud.append("/");
    regex.addProperty("$regex", sbud.toString());
    putObject(jsonElement, getFieldName(lOperand.getName()), regex);
  }

  protected void translateInExpression(String operator, InExpression expression,
      JsonElement jsonElement) {
    JsonObject in = new JsonObject();
    JsonArray array = new JsonArray();
    for (Operand rOperand : expression.getrOperands()) {
      array.add(translateUnaryOperand(rOperand));
    }
    in.add(operator, array);
    putObject(jsonElement, getFieldName(expression.getLeftOperand().getName()),
        in);
  }

  protected void translateIsExpression(Operand lOperand,
      JsonElement jsonElement, boolean isNot) {
    JsonObject is = new JsonObject();
    if (!isNot) {
      is.add("$eq", null);
    } else {
      is.add("$ne", null);
    }
    putObject(jsonElement, getFieldName(lOperand.getName()), is);
  }

  protected void translateExistsExpression(ExistsExpression existsExpression,
      JsonElement jsonElement) {
    JsonObject exists = new JsonObject();
    exists.addProperty("$exists", true);
    putObject(jsonElement,
        getFieldName(existsExpression.getRightOperand().getName()), exists);
  }

  protected void translateFunction(AbstractFunction function,
      JsonElement jsonObject) {
    MongoFunctionTranslator functionTranslator = functionTranslators
        .get(function.getName());
    if (functionTranslator == null) {
      if (function.getParameterCount() == 1) {
        JsonElement je = translateUnaryOperand(function.getParameters().get(0));
        putObject(jsonObject, "$" + function.getName(), je);
      } else {
        JsonArray jv = new JsonArray();
        for (int i = 0; i < function.getParameterCount(); i++) {
          jv.add(translateUnaryOperand(function.getParameters().get(i)));
        }
        putObject(jsonObject, "$" + function.getName(), jv);
      }
    } else {
      functionTranslator.translate(function, jsonObject);
    }
  }

  protected void translateHavingClause(HavingImpl having,
      JsonElement jsonElement) {
    JsonObject match = new JsonObject();
    translateOperand(having.getCondition().getOperand(), match);
    putObject(jsonElement, "$match", match);
  }

  protected String translate2Sql(SetlectorImpl setlector,
      Map<String, Object> translationContext) {
    throw new UnsupportedOperationException("pending...");
  }

  @Override
  public String translate2Condition(Filter filter) {
    throw new UnsupportedOperationException("");
  }

  @Override
  public String translate2Condition(Filter filter,
      Map<String, Object> translationContext) {
    throw new UnsupportedOperationException("");
  }

  @Override
  public void addFunctionTranslator(FunctionTranslator functionTranslator) {

  }

  @Override
  public void addAllFunctionTranslator(
      List<FunctionTranslator> functiionTranslators) {

  }

  @Override
  public FunctionTranslator removeFunctionTranslator(String functionName) {
    return null;
  }

  @Override
  public List<FunctionTranslator> getFunctionTranslators() {
    return null;
  }
}
