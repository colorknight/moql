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
package org.moql.sql;

import org.apache.commons.lang.Validate;
import org.moql.Filter;
import org.moql.Operand;
import org.moql.Selector;
import org.moql.core.*;
import org.moql.core.group.GroupRecordSetOperator;
import org.moql.core.table.SelectorTable;
import org.moql.metadata.*;
import org.moql.operand.expression.AbstractOperationExpression;
import org.moql.operand.expression.ExpressionType;
import org.moql.operand.expression.ParenExpression;
import org.moql.operand.expression.arithmetic.ArithmeticOperator;
import org.moql.operand.expression.logic.LogicOperator;
import org.moql.operand.expression.logic.NotExpression;
import org.moql.operand.expression.relation.BetweenExpression;
import org.moql.operand.expression.relation.ExistsExpression;
import org.moql.operand.expression.relation.InExpression;
import org.moql.operand.expression.relation.RelationOperator;
import org.moql.operand.function.AbstractFunction;
import org.moql.operand.selector.ColumnSelectorOperand;
import org.moql.operand.selector.ValueSelectorOperand;
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
public class MoqlGrammarTranslator implements SqlTranslator {
	
	protected Map<String, FunctionTranslator> functionTranslators = new HashMap<String, FunctionTranslator>();
	
	public String translate2Sql(Selector selector) {
		Validate.notNull(selector, "selector is null!");
		if (selector instanceof SelectorImpl) {
			return translate2Sql((SelectorImpl)selector);
		} else {
			return translate2Sql((SetlectorImpl)selector);
		}
		
	}
	
	protected String translate2Sql(SelectorImpl selector) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translate2SelectClause(selector.getRecordSetOperator()));
		sbuf.append(translate2FromClause(selector.getTables()));
		if (selector.getWhere() != null) {
			sbuf.append(translate2WhereClause(selector.getWhere()));
		}
		if (selector.getRecordSetOperator() instanceof GroupRecordSetOperator) {
			sbuf.append(translate2GroupbyClause((GroupRecordSetOperator)selector.getRecordSetOperator()));
		}
		if (selector.getHaving() != null) {
			sbuf.append(translate2HavingClause((HavingImpl)selector.getHaving()));
		}
		if (selector.getOrder() != null) {
			sbuf.append(translate2OrderbyClause((OrderImpl)selector.getOrder()));
		}
		if (selector.getLimit() != null) {
			sbuf.append(translate2LimitClause(selector.getLimit()));
		}
		return sbuf.toString();
	}
	
	protected String translate2Sql(SetlectorImpl setlector) {
		StringBuffer sbuf = new StringBuffer();
		SetlectorMetadata setlectorMetadata = (SetlectorMetadata)setlector.getSelectorDefinition();
		int i = 0; 
		for(Selector selector : setlector.getSets()) {
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
			sbuf.append(translate2OrderbyClause((OrderImpl)setlector.getOrder()));
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
		// TODO Auto-generated method stub
		Validate.notNull(filter, "filter is null!");
		return translateOperand(((Condition)filter).getOperand());
	}

	protected String translate2SelectClause(RecordSetOperator recordSetOperator) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("select ");
		if (recordSetOperator.getCache() != null)
			sbuf.append(translateCache(recordSetOperator.getCache()));
		if (recordSetOperator.getColumns().getColumnsMetadata().isDistinct()) {
			sbuf.append("distinct ");
		}
		sbuf.append(translateColumns(recordSetOperator.getColumns()));
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
	
	protected String translate2LimitClause(Limit limit) {
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
	
	protected String translateColumns(Columns columns) {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		for(Column column : columns.getColumns()) {
			if (column.isJustUsed4Order())
				continue;
			if (i != 0) {
				sbuf.append(", ");
			}
			
			sbuf.append(translateOperand(column.getOperand()).trim());
			if (column.getColumnMetadata().isHasAlias()) {
				sbuf.append(" ");
				sbuf.append(column.getColumnMetadata().getName());
			}
			i++;
		}
		sbuf.append(" ");
		return sbuf.toString();
	}
	
	protected String translate2FromClause(Tables tables) {
		StringBuffer sbuf = new StringBuffer();
		boolean multiTables = false;
		sbuf.append("from ");
		if (tables.getTablesMetadata().getTables().size() > 1) {
			multiTables = true;
		}
		sbuf.append(translateQueryable(tables.getQueryable(), multiTables));
		return sbuf.toString();
	}
	
	@SuppressWarnings({"rawtypes"})
	protected String translateQueryable(Queryable queryable, boolean multiTables) {
		if (queryable instanceof Table) {
			return translateTable((Table)queryable);
		} else {
			return translateJoin((Join)queryable, multiTables);
		}
	}
	
	protected String translateTable(Table table) {
		StringBuffer sbuf = new StringBuffer();
		TableMetadata tableMetadata = table.getTableMetadata();
		if (table instanceof SelectorTable) {
			SelectorTable selectorTable = (SelectorTable)table;
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
	
	protected String translateJoin(Join join, boolean multiTables) {
		StringBuffer sbuf = new StringBuffer();
		JoinMetadata joinMetadata = join.getJoinMetadata();
		String leftQuery = translateQueryable(join.getLeftQueryable(), multiTables); 
		if (!multiTables) {
			sbuf.append(leftQuery);
			sbuf.append(getJoinType(joinMetadata.getJoinType()));
			sbuf.append(" ");
		} else {
			sbuf.append(leftQuery.trim());
			sbuf.append(", ");
		}
		sbuf.append(translateQueryable(join.getRightQueryable(), multiTables));
		if (!multiTables && join.getOn() != null) {
			sbuf.append("on ");
			sbuf.append(translateOperand(join.getOn().getOperand()));
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
	
	protected String translate2WhereClause(Condition condition) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("where ");
		sbuf.append(translateOperand(condition.getOperand()));
		return sbuf.toString();
	}
	
	protected String translateOperand(Operand operand) {
		StringBuffer sbuf = new StringBuffer();
		if (operand instanceof AbstractOperationExpression) {
			AbstractOperationExpression expression = (AbstractOperationExpression)operand;
			if (expression.getExpressionType() == ExpressionType.LOGIC) {
				sbuf.append(translateLogicExpression(expression));
			} else if (expression.getExpressionType() == ExpressionType.RELATION) {
				sbuf.append(translateRelationExpression(expression));
			} else if (expression.getExpressionType() == ExpressionType.ARITHMETIC) {
				sbuf.append(translateArithmeticExpression(expression));
			} else {
				throw new IllegalArgumentException(StringFormater.format("Doesn't support operand with type '{}'!", expression.getExpressionType()));
			}
		} else if (operand instanceof ParenExpression) {
			ParenExpression parenExpression = (ParenExpression)operand;
			sbuf.append(translateParenExpression(parenExpression));
		} else if (operand instanceof ColumnSelectorOperand) {
			ColumnSelectorOperand selectorOperand = (ColumnSelectorOperand)operand;
			sbuf.append(translate2Sql(selectorOperand.getColumnSelector()));
		} else if (operand instanceof ValueSelectorOperand) {
			ValueSelectorOperand selectorOperand = (ValueSelectorOperand)operand;
			sbuf.append("(");
			sbuf.append(translate2Sql(selectorOperand.getValueSelector()).trim());
			sbuf.append(") ");
		} else if (operand instanceof AbstractFunction) {
			AbstractFunction function = (AbstractFunction)operand;
			sbuf.append(translateFunction(function));
		} else {
			sbuf.append(operand.getName());
			sbuf.append(" ");
		}
		return sbuf.toString();
	}
	
	protected String translateNotExpression(NotExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("not ");
		sbuf.append(translateOperand(expression.getRightOperand()));
		return sbuf.toString();
	}
	
	protected String translateBinaryExpression(String operator, Operand lOperand, Operand rOperand) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translateOperand(lOperand));
		sbuf.append(operator);
		sbuf.append(' ');
		sbuf.append(translateOperand(rOperand));
		return sbuf.toString();
	}
	
	protected String translateLogicExpression(AbstractOperationExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		if (expression.getOperator() == LogicOperator.NOT) {
			sbuf.append(translateNotExpression((NotExpression)expression));
		} else {
			if (expression.getOperator() == LogicOperator.AND)
				sbuf.append(translateBinaryExpression(
						"and", expression.getLeftOperand(), expression.getRightOperand()));
			else
				sbuf.append(translateBinaryExpression(
						"or", expression.getLeftOperand(), expression.getRightOperand()));
		}
		return sbuf.toString();
	}
	
	protected String translateRelationExpression(AbstractOperationExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		if (expression.getOperator() == RelationOperator.EQ) {
			sbuf.append(translateBinaryExpression(
					"=", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.GT) {
			sbuf.append(translateBinaryExpression(
					">", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.GE) {
			sbuf.append(translateBinaryExpression(
					">=", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.LT) {
			sbuf.append(translateBinaryExpression(
					"<", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.LE) {
			sbuf.append(translateBinaryExpression(
					"<=", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.NE) {
			sbuf.append(translateBinaryExpression(
					"<>", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.BETWEEN) {
			sbuf.append(translateBetweenExpression((BetweenExpression)expression));
		} else if (expression.getOperator() == RelationOperator.LIKE) {
			sbuf.append(translateBinaryExpression(
					"like", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.IN) {
			sbuf.append(translateInExpression((InExpression)expression));
		} else if (expression.getOperator() == RelationOperator.IS) {
			sbuf.append(translateBinaryExpression(
					"is", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == RelationOperator.EXISTS) {
			sbuf.append(translateExistsExpression((ExistsExpression)expression));
		} else {
			sbuf.append(translateOperand(expression.getRightOperand()));
		}
		
		return sbuf.toString();
	}
	
	protected String translateParenExpression(ParenExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("(");
		sbuf.append(translateOperand(expression.getOperand()).trim());
		sbuf.append(") ");
		return sbuf.toString();
	}
	
	protected String translateBetweenExpression(BetweenExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translateOperand(expression.getLeftOperand()));
		sbuf.append("between ");
		int i = 0;
		for(Operand rOperand : expression.getrOperands()) {
			sbuf.append(translateOperand(rOperand));
			if (i == 0) {
				sbuf.append("and ");
				i++;
			}
		}
		return sbuf.toString();
	}
	
	protected String translateInExpression(InExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translateOperand(expression.getLeftOperand()));
		sbuf.append("in (");
		int i = 0;
		for(Operand rOperand : expression.getrOperands()) {
			if (i != 0) {
				sbuf.append(", ");
			}
			sbuf.append(translateOperand(rOperand).trim());
			i++;
		}
		sbuf.append(") ");
		return sbuf.toString();
	}
	
	protected String translateExistsExpression(ExistsExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("exists(");
		sbuf.append(translateOperand(expression.getRightOperand()).trim());
		sbuf.append(") ");
		return sbuf.toString();
	}
	
	protected String translateArithmeticExpression(AbstractOperationExpression expression) {
		StringBuffer sbuf = new StringBuffer();
		if (expression.getOperator() == ArithmeticOperator.ADD) {
			sbuf.append(translateBinaryExpression(
					"+", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.SUBTRACT) {
			sbuf.append(translateBinaryExpression(
					"-", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.MULTIPLY) {
			sbuf.append(translateBinaryExpression(
					"*", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.DIVIDE) {
			sbuf.append(translateBinaryExpression(
					"/", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.MODULAR) {
			sbuf.append(translateBinaryExpression(
					"%", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.BITWISEAND) {
			sbuf.append(translateBinaryExpression(
					"&", expression.getLeftOperand(), expression.getRightOperand()));
		} else if (expression.getOperator() == ArithmeticOperator.BITWISEOR) {
			sbuf.append(translateBinaryExpression(
					"|", expression.getLeftOperand(), expression.getRightOperand()));
		} else {
			sbuf.append(translateBinaryExpression(
					"^", expression.getLeftOperand(), expression.getRightOperand()));
		} 
		return sbuf.toString();
	}
	
	protected String translateFunction(AbstractFunction function) {
		StringBuffer sbuf = new StringBuffer();
		FunctionTranslator functionTranslator = functionTranslators.get(function.getName());
		if (functionTranslator == null) {
			sbuf.append(function.toString());
		} else {
			sbuf.append(functionTranslator.translate(function));
		}
		sbuf.append(" ");
		return sbuf.toString();
	}
	
	protected String translate2GroupbyClause(GroupRecordSetOperator groupRecordSetOperator) {
		StringBuffer sbuf = new StringBuffer();
		List<GroupMetadata> groupMetadatas = groupRecordSetOperator.getGroupMetadatas();
		sbuf.append("group by ");
		int i = 0;
		for(Column column : groupRecordSetOperator.getGroupColumns()) {
			if (i != 0) {
				sbuf.append(",");
			}
			GroupMetadata groupMetadata = groupMetadatas.get(i);
			try {
				int index = Integer.valueOf(groupMetadata.getColumn());
				sbuf.append(index);
			} catch (Throwable t) {
				sbuf.append(translateOperand(column.getOperand()));
			}
			i++;
		}
		sbuf.append(" ");
		return sbuf.toString();
	}
	
	protected String translate2HavingClause(HavingImpl having) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("having ");
		sbuf.append(translateOperand(having.getCondition().getOperand()));
		return sbuf.toString();
	}
	
	protected String translate2OrderbyClause(OrderImpl order) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("order by ");
		Column[] columns = order.getOrderColumns();
		OrderType[] orderTypes = order.getOrderTypes();
		List<OrderMetadata> orderMetadatas = order.getOrderMetadatas();
		for(int i = 0; i < columns.length; i++) {
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
		functionTranslators.put(functionTranslator.getFunctionName(), functionTranslator);
	}

	@Override
	public void addAllFunctionTranslator(
			List<FunctionTranslator> functionTranslators) {
		// TODO Auto-generated method stub
		Validate.notNull(functionTranslators, "functionTranslators is null!");
		for(FunctionTranslator functionTranslator : functionTranslators) {
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
