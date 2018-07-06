package org.moql.sql.es;

import com.google.gson.*;
import org.apache.commons.lang.Validate;
import org.moql.*;
import org.moql.core.*;
import org.moql.core.group.GroupRecordSetOperator;
import org.moql.metadata.*;
import org.moql.operand.constant.StringConstant;
import org.moql.operand.expression.AbstractOperationExpression;
import org.moql.operand.expression.ExpressionType;
import org.moql.operand.expression.ParenExpression;
import org.moql.operand.expression.logic.LogicOperator;
import org.moql.operand.expression.logic.NotExpression;
import org.moql.operand.expression.member.MemberVariableExpression;
import org.moql.operand.expression.relation.*;
import org.moql.operand.function.*;
import org.moql.sql.FunctionTranslator;
import org.moql.sql.SqlTranslator;
import org.moql.util.StringFormater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticSearchTranslator implements SqlTranslator {

  protected Map<String, ESFunctionTranslator> functionTranslators = new HashMap<String, ESFunctionTranslator>();

  {
    functionTranslators
        .put(MatchTranslator.FUNCTION_NAME, new MatchTranslator());
    functionTranslators
        .put(MatchPhraseTranslator.FUNCTION_NAME, new MatchPhraseTranslator());
    functionTranslators.put(MatchPhrasePrefixTranslator.FUNCTION_NAME,
        new MatchPhrasePrefixTranslator());
    functionTranslators
        .put(TermsSetTranslator.FUNCTION_NAME, new TermsSetTranslator());
    functionTranslators.put(Regex.FUNCTION_NAME, new RegExpTranslator());
    functionTranslators
        .put(FuzzyTranslator.FUNCTION_NAME, new FuzzyTranslator());
    functionTranslators.put(TypeTranslator.FUNCTION_NAME, new TypeTranslator());
    functionTranslators.put(IdsTranslator.FUNCTION_NAME, new IdsTranslator());
    functionTranslators
        .put(MoreLikeTranslator.FUNCTION_NAME, new MoreLikeTranslator());
  }

  protected static Gson gson = new GsonBuilder().serializeNulls()
      .setPrettyPrinting().create();

  @Override public String translate2Sql(Selector selector) {
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

    if (isAggregations(selector)) {
      translate2Aggs(selector, jsonObject);
    } else {
      translate2Query(selector, jsonObject);
    }
    return gson.toJson(jsonObject);
  }

  protected void checkGrammer(SelectorImpl selector) {
    if (selector.getTables().getTablesMetadata().getTables().size() > 1) {
      throw new MoqlTranslationException("The sql querys more than 1 table!");
    }
  }

  protected void translate2Query(SelectorImpl selector, JsonObject jsonObject) {
    translateSelectClause(selector.getSelectorDefinition(), jsonObject);
    if (selector.getLimit() != null) {
      translateLimitClause(selector.getLimit(), jsonObject);
    }
    if (selector.getOrder() != null) {
      translateOrderClause(selector.getOrder(), jsonObject);
    }
    translate2CommonQuery(selector, jsonObject);
  }

  protected void translate2Aggs(SelectorImpl selector, JsonObject jsonObject) {
    jsonObject.addProperty("size", 0);
    translate2CommonQuery(selector, jsonObject);
    translate2Aggregations(selector, jsonObject);
  }

  protected void translateSelectClause(SelectorDefinition selectorDefinition,
      JsonObject jsonObject) {
    ColumnsMetadata columnsMetadata = ((SelectorMetadata) selectorDefinition)
        .getColumns();
    List<ColumnMetadata> columnMetadatas = columnsMetadata.getColumns();
    if (columnMetadatas.size() == 1) {
      ColumnMetadata columnMetadata = columnMetadatas.get(0);
      String value = columnMetadata.getValue();
      if (value.endsWith(".*"))
        return;
    }
    JsonObject source = new JsonObject();
    JsonArray includes = new JsonArray();
    for (ColumnMetadata columnMetadata : columnMetadatas) {
      if (columnMetadata.getNestedSelector() != null)
        throw new UnsupportedOperationException(
            "Unsupported nested selector in select clause!");
      String value = columnMetadata.getValue();
      int index = value.indexOf('(');
      if (index == -1) {
        index = value.indexOf('.');
        value = value.substring(index + 1);
      }
      includes.add(value);
    }
    source.add("includes", includes);
    jsonObject.add("_source", source);
  }

  protected void translateLimitClause(Limit limit, JsonObject jsonObject) {
    if (limit == null) {
      return;
    }
    LimitMetadata limitMetadata = limit.getLimitMetadata();
    if (limit.getLimitMetadata().getOffset() != 0) {
      jsonObject.addProperty("from", limitMetadata.getOffset());
    }
    jsonObject.addProperty("size", limitMetadata.getValue());
  }

  protected void translateOrderClause(Order order, JsonElement jsonElement) {
    if (order == null)
      return;
    JsonArray sortObject = new JsonArray();
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    OrderType[] orderTypes = orderImpl.getOrderTypes();
    for (int i = 0; i < columns.length; i++) {
      JsonObject orderObject = new JsonObject();
      JsonObject object = new JsonObject();
      object.addProperty("order", orderTypes[i].name().toLowerCase());
      orderObject.add(getOperandName(columns[i].getOperand()), object);
      sortObject.add(orderObject);
    }
    putObject(jsonElement, "sort", sortObject);
  }

  protected boolean isAggregations(SelectorImpl selector) {
    RecordSetOperator recordSetOperator = selector.getRecordSetOperator();
    if (recordSetOperator instanceof Group)
      return true;
    Columns columns = recordSetOperator.getColumns();
    if (columns.getColumnsMetadata().isDistinct())
      return true;

    for (Column column : columns.getColumns()) {
      if (column.getOperand() instanceof AggregationFunction)
        return true;
    }
    return false;
  }

  protected void translate2Aggregations(SelectorImpl selector,
      JsonObject jsonObject) {

    RecordSetOperator recordSetOperator = selector.getRecordSetOperator();
    if (recordSetOperator instanceof Group) {
      translate2GroupAggregations((GroupRecordSetOperator) recordSetOperator,
          jsonObject, selector.getLimit(), selector.getOrder());
    } else if (recordSetOperator.getColumns().getColumnsMetadata()
        .isDistinct()) {
      translate2DistinctAggregations(
          (ColumnsRecordSetOperator) recordSetOperator, jsonObject,
          selector.getLimit(), selector.getOrder());
    } else {
      translateColumnAggregations((ColumnsRecordSetOperator) recordSetOperator,
          jsonObject);
    }
  }

  protected void translate2GroupAggregations(
      GroupRecordSetOperator groupRecordSetOperator, JsonObject jsonObject,
      Limit limit, Order order) {
    Column[] columns = groupRecordSetOperator.getGroupColumns();
    JsonObject baseObject = jsonObject;
    jsonObject.add("aggs", baseObject);
    int size = getLimitSize(limit);
    for (int i = 0; i < columns.length; i++) {
      baseObject = translate2TermsAggs(columns[i], baseObject, size, order);
      size = 0;
    }
    columns = groupRecordSetOperator.getNonGroupColumns();
    JsonObject aggsObject = new JsonObject();
    baseObject.add("aggs", aggsObject);
    JsonArray orderArray = null;
    if (order != null) {
      orderArray = getOrderArray(baseObject);
    }
    for (int i = 0; i < columns.length; i++) {
      if (columns[i] == null)
        continue;
      JsonObject aggregation = new JsonObject();
      translateFunctionAggregation(
          (AggregationFunction) columns[i].getOperand(), aggregation);
      //  当聚集函数是0时，aggregation为空
      if (aggregation.entrySet().size() > 0)
        aggsObject.add(columns[i].getColumnMetadata().getName(), aggregation);
      if (order != null) {
        OrderType orderType = getOrderType(columns[i], order);
        if (orderType != null) {
          JsonObject orderObject = new JsonObject();
          orderObject.addProperty(columns[i].getColumnMetadata().getName(),
              orderType.name().toLowerCase());
          orderArray.add(orderObject);
        }
      }
    }
    if (order != null && orderArray.size() == 0) {
      removeOrderArray(baseObject);
    }
  }

  protected int getLimitSize(Limit limit) {
    if (limit == null)
      return 0;
    LimitMetadata metadata = limit.getLimitMetadata();
    return metadata.getValue();
  }

  protected JsonObject translate2TermsAggs(Column column, JsonObject jsonObject,
      int size, Order order) {
    JsonObject aggsObject = new JsonObject();
    jsonObject.add("aggs", aggsObject);
    JsonObject aggregation = new JsonObject();
    translateTermsAggregation(getOperandName(column.getOperand()), aggregation,
        size, order);
    aggsObject.add(getOperandName(column.getOperand()), aggregation);
    return aggregation;
  }

  protected void translateFunctionAggregation(
      AggregationFunction aggregationFunction, JsonObject jsonObject) {
    JsonObject functionObject = new JsonObject();
    String functionName = getFunctionName(aggregationFunction);
    if (functionName == null)
      return;
    Operand operand = aggregationFunction.getParameters().get(0);
    functionObject.addProperty("field", getOperandName(operand));
    jsonObject.add(functionName, functionObject);
  }

  protected String getFunctionName(AggregationFunction aggregationFunction) {
    String functionName = aggregationFunction.getName();
    if (functionName.equals(Count.FUNCTION_NAME)) {
      Count count = (Count) aggregationFunction;
      if (count.isDistinct())
        return "cardinality";
      return null;
    }
    return functionName;
  }

  protected void translateTermsAggregation(String fieldName,
      JsonObject jsonObject, int size, Order order) {
    JsonObject termsObject = new JsonObject();
    termsObject.addProperty("field", fieldName);
    if (size != 0)
      termsObject.addProperty("size", size);
    JsonArray orderArray = translateTermsOrder(fieldName, order);
    if (orderArray != null)
      termsObject.add("order", orderArray);
    jsonObject.add("terms", termsObject);
  }

  protected JsonArray translateTermsOrder(String fieldName, Order order) {
    if (order == null)
      return null;
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    OrderType[] orderTypes = orderImpl.getOrderTypes();
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < columns.length; i++) {
      if (columns[i].getOperand() instanceof Count) {
        Count count = (Count) columns[i].getOperand();
        String name = getOperandName(count.getParameters().get(0));
        if (name.equals(fieldName)) {
          add2OrderArray("_count", orderTypes[i], jsonArray);
          break;
        }
      } else {
        String tmp = getOperandName(columns[i].getOperand());
        if (tmp.equals(fieldName)) {
          add2OrderArray("_term", orderTypes[i], jsonArray);
          break;
        }
      }
    }
    if (jsonArray.size() == 0)
      return null;
    return jsonArray;
  }

  protected void add2OrderArray(String field, OrderType orderType,
      JsonArray jsonArray) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty(field, orderType.name().toLowerCase());
    jsonArray.add(jsonObject);
  }

  protected JsonArray getOrderArray(JsonObject aggregation) {
    JsonObject termsObject = (JsonObject) aggregation.get("terms");
    JsonArray orderArray = (JsonArray) termsObject.get("order");
    if (orderArray == null) {
      orderArray = new JsonArray();
      termsObject.add("order", orderArray);
    }
    return orderArray;
  }

  protected void removeOrderArray(JsonObject aggregation) {
    JsonObject termsObject = (JsonObject) aggregation.get("terms");
    termsObject.remove("order");
  }

  protected OrderType getOrderType(Column column, Order order) {
    OrderImpl orderImpl = (OrderImpl) order;
    Column[] columns = orderImpl.getOrderColumns();
    OrderType[] orderTypes = orderImpl.getOrderTypes();
    String columnName = column.getColumnMetadata().getName();
    for (int i = 0; i < columns.length; i++) {
      String tmp = columns[i].getColumnMetadata().getName();
      if (tmp.equals(columnName)) {
        return orderTypes[i];
      }
    }
    return null;
  }

  protected void translate2DistinctAggregations(
      ColumnsRecordSetOperator columnsRecordSetOperator, JsonObject jsonObject,
      Limit limit, Order order) {
    JsonObject baseObject = jsonObject;
    int size = getLimitSize(limit);
    for (Column column : columnsRecordSetOperator.getColumns().getColumns()) {
      baseObject = translate2TermsAggs(column, baseObject, size, order);
      size = 0;
    }
  }

  protected void translateColumnAggregations(
      ColumnsRecordSetOperator columnsRecordSetOperator,
      JsonObject jsonObject) {
    for (Column column : columnsRecordSetOperator.getColumns().getColumns()) {
      JsonObject aggsObject = new JsonObject();
      jsonObject.add("aggs", aggsObject);
      if (column.getOperand() instanceof AggregationFunction) {
        JsonObject aggregation = new JsonObject();
        translateFunctionAggregation((AggregationFunction) column.getOperand(),
            aggregation);
        aggsObject.add(column.getColumnMetadata().getName(), aggregation);
      }
    }
  }

  protected void translate2CommonQuery(SelectorImpl selector,
      JsonObject jsonObject) {
    JsonObject queryObject = new JsonObject();
    jsonObject.add("query", queryObject);

    if (selector.getHaving() != null) {
      JsonElement jsonElement;
      if (selector.getWhere() != null) {
        jsonElement = translateWhereClause(selector.getWhere(), queryObject,
            true);
      } else {
        jsonElement = new JsonObject();
        queryObject.add("bool", jsonElement);
      }
      translateHavingClause((HavingImpl) selector.getHaving(), jsonElement);
    } else {
      translateWhereClause(selector.getWhere(), queryObject, false);
    }
  }

  protected boolean isLogicExpression(Operand operand) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC)
        return true;
    }
    return false;
  }

  protected JsonElement translateWhereClause(Condition condition,
      JsonElement jsonElement, boolean hasHaving) {
    if (condition == null) {
      if (!hasHaving) {
        JsonObject matchAll = new JsonObject();
        putObject(jsonElement, "match_all", matchAll);
      }
      return jsonElement;
    }
    Operand operand = condition.getOperand();
    if (isLogicExpression(operand)) {
      return shellLogicExpression(operand, jsonElement);
    }
    if (hasHaving) {
      return shellHasHaving(operand, jsonElement);
    }
    translateOperand(operand, jsonElement, false);
    return jsonElement;
  }

  protected JsonElement shellLogicExpression(Operand operand,
      JsonElement jsonElement) {
    JsonObject shellObject = new JsonObject();
    translateOperand(operand, shellObject, false);
    for (Map.Entry<String, JsonElement> entry : shellObject.entrySet()) {
      putObject(jsonElement, entry.getKey(), entry.getValue());
      return entry.getValue();
    }
    throw new IllegalArgumentException("Invalid operand!");
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

  protected JsonElement shellHasHaving(Operand operand,
      JsonElement jsonElement) {
    JsonObject boolObject = new JsonObject();
    JsonObject mustObject = new JsonObject();
    translateOperand(operand, mustObject, false);
    putObject(boolObject, "must", mustObject);
    putObject(jsonElement, "bool", boolObject);
    return boolObject;
  }

  protected void translateOperand(Operand operand, JsonElement jsonElement,
      boolean having) {
    if (operand instanceof AbstractOperationExpression) {
      AbstractOperationExpression expression = (AbstractOperationExpression) operand;
      if (expression.getExpressionType() == ExpressionType.LOGIC) {
        translateLogicExpression(expression, jsonElement, having);
      } else if (expression.getExpressionType() == ExpressionType.RELATION) {
        translateRelationExpression(expression, jsonElement, having);
      } else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
        throw new MoqlTranslationException(StringFormater
            .format("The expression '{}' does not support!",
                expression.getExpressionType().toString()));
      }
    } else if (operand instanceof ParenExpression) {
      ParenExpression parenExpression = (ParenExpression) operand;
      translateParenExpression(parenExpression, jsonElement, having);
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
      AbstractOperationExpression expression, JsonElement jsonElement,
      boolean having) {
    JsonObject boolObject = new JsonObject();
    if (expression.getOperator() == LogicOperator.NOT) {
      translateNotExpression((NotExpression) expression, boolObject, having);
    } else {
      if (expression.getOperator() == LogicOperator.AND) {
        String operator = "must";
        if (having) {
          operator = "filter";
        }
        translateLogicBinaryExpression(operator, expression.getLeftOperand(),
            expression.getRightOperand(), boolObject, having);
      } else {
        translateLogicBinaryExpression("should", expression.getLeftOperand(),
            expression.getRightOperand(), boolObject, having);
      }
    }
    putObject(jsonElement, "bool", boolObject);
  }

  protected void translateNotExpression(NotExpression expression,
      JsonElement jsonElement, boolean having) {
    JsonObject not = new JsonObject();
    if (expression.getRightOperand() instanceof IsExpression) {
      IsExpression isExpression = (IsExpression) expression.getRightOperand();
      translateIsExpression(isExpression.getLeftOperand(), jsonElement, true);
    } else {
      translateOperand(expression.getRightOperand(), not, having);
      putObject(jsonElement, "must_not", not);
    }
  }

  protected void translateLogicBinaryExpression(String operator,
      Operand lOperand, Operand rOperand, JsonElement jsonElement,
      boolean having) {
    JsonArray logicArray = new JsonArray();
    translateOperand(lOperand, logicArray, having);
    translateOperand(rOperand, logicArray, having);
    putObject(jsonElement, operator, logicArray);
  }

  protected void translateRelationExpression(
      AbstractOperationExpression expression, JsonElement jsonElement,
      boolean having) {
    if (expression.getOperator() == RelationOperator.EQ) {
      translateEQExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement, having);
    } else if (expression.getOperator() == RelationOperator.GT) {
      translateLGExpression("gt", expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.GE) {
      translateLGExpression("gte", expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.LT) {
      translateLGExpression("lt", expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.LE) {
      translateLGExpression("lte", expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.NE) {
      translateNEExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.BETWEEN) {
      translateBetweenExpression((BetweenExpression) expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.LIKE) {
      translateLikeExpression(expression.getLeftOperand(),
          expression.getRightOperand(), jsonElement);
    } else if (expression.getOperator() == RelationOperator.IN) {
      translateInExpression((InExpression) expression, jsonElement);
    } else if (expression.getOperator() == RelationOperator.IS) {
      translateIsExpression(expression.getLeftOperand(), jsonElement, false);
    } else if (expression.getOperator() == RelationOperator.EXISTS) {
      throw new UnsupportedOperationException(
          "Does't support 'exists' operator.Please use 'is' operator!");
    } else {
      translateOperand(expression.getRightOperand(), jsonElement, having);
    }
  }

  protected void translateParenExpression(ParenExpression expression,
      JsonElement jsonElement, boolean having) {
    translateOperand(expression.getOperand(), jsonElement, having);
  }

  protected void translateEQExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonElement, boolean having) {
    JsonObject eq = new JsonObject();
    eq.addProperty(getOperandName(lOperand), getOperandName(rOperand));
    putObject(jsonElement, "term", eq);
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
    } else if (operand instanceof Function) {
      return operand.toString();
    }
    return name;
  }

  protected void translateLGExpression(String operator, Operand lOperand,
      Operand rOperand, JsonElement jsonElement) {
    JsonObject range = new JsonObject();
    JsonObject cmp = new JsonObject();
    cmp.addProperty(operator, getOperandName(rOperand));
    range.add(getOperandName(lOperand), cmp);
    putObject(jsonElement, "range", range);
  }

  protected void translateNEExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonElement) {
    JsonObject boolObject = new JsonObject();
    JsonObject notObject = new JsonObject();
    JsonObject term = new JsonObject();
    term.addProperty(getOperandName(lOperand), getOperandName(rOperand));
    notObject.add("term", term);
    boolObject.add("must_not", notObject);
    putObject(jsonElement, "bool", boolObject);

  }

  protected void translateBetweenExpression(BetweenExpression expression,
      JsonElement jsonElement) {
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
    putObject(jsonElement, "range", range);
  }

  protected void translateLikeExpression(Operand lOperand, Operand rOperand,
      JsonElement jsonElement) {
    JsonObject regex = new JsonObject();
    regex.addProperty(getOperandName(lOperand),
        LikeExpression.translatePattern2Regex(getOperandName(rOperand)));
    putObject(jsonElement, "regexp", regex);
  }

  protected void translateInExpression(InExpression expression,
      JsonElement jsonElement) {
    JsonObject terms = new JsonObject();
    JsonArray array = new JsonArray();
    for (Operand rOperand : expression.getrOperands()) {
      array.add(getOperandName(rOperand));
    }
    terms.add(getOperandName(expression.getLeftOperand()), array);
    putObject(jsonElement, "terms", terms);
  }

  protected void translateIsExpression(Operand lOperand,
      JsonElement jsonElement, boolean isNot) {
    if (!isNot) {
      JsonObject boolObject = new JsonObject();
      JsonObject notObject = new JsonObject();
      JsonObject exists = new JsonObject();
      exists.addProperty("field", getOperandName(lOperand));
      putObject(notObject, "exists", exists);
      putObject(boolObject, "must_not", notObject);
      putObject(jsonElement, "bool", boolObject);
    } else {
      JsonObject shouldObject = new JsonObject();
      JsonObject exists = new JsonObject();
      exists.addProperty("field", getOperandName(lOperand));
      putObject(shouldObject, "exists", exists);
      putObject(jsonElement, "should", shouldObject);
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

  protected void translateHavingClause(HavingImpl having,
      JsonElement jsonElement) {
    JsonObject filterObject = new JsonObject();
    translateOperand(having.getCondition().getOperand(), filterObject, true);
    putObject(jsonElement, "filter", filterObject);
  }

  protected String translate2Sql(SetlectorImpl setlector) {
    throw new UnsupportedOperationException("pending...");
  }

  @Override public String translate2Condition(Filter filter) {
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

  @Override public List<FunctionTranslator> getFunctionTranslators() {
    return null;
  }
}
