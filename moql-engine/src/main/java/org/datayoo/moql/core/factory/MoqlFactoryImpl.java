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
package org.datayoo.moql.core.factory;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.*;
import org.datayoo.moql.core.*;
import org.datayoo.moql.core.combination.RecordSetCombinationFactory;
import org.datayoo.moql.core.group.GroupRecordSetOperator;
import org.datayoo.moql.core.join.JoinFactory;
import org.datayoo.moql.core.table.CommonTable;
import org.datayoo.moql.core.table.SelectorTable;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.operand.OperandFactory;
import org.datayoo.moql.operand.expression.ParenExpression;
import org.datayoo.moql.operand.expression.logic.LogicExpressionFactory;
import org.datayoo.moql.operand.expression.relation.RelationExpressionFactory;
import org.datayoo.moql.operand.factory.OperandFactoryImpl;
import org.datayoo.moql.operand.selector.ColumnSelectorOperand;
import org.datayoo.moql.operand.selector.ValueSelectorOperand;
import org.datayoo.moql.util.StringFormater;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class MoqlFactoryImpl implements MoqlFactory {

  protected OperandFactory operandFactory = OperandFactoryImpl
      .createOperandFactory();

  protected static MoqlFactory moqlFactory;

  protected MoqlFactoryImpl() {
  }

  public static synchronized MoqlFactory createSelectorFactory() {
    if (moqlFactory == null) {
      moqlFactory = new MoqlFactoryImpl();
    }
    return moqlFactory;
  }

  @Override
  public Column createColumn(ColumnMetadata columnMetadata,
      boolean justUsed4Order) throws MoqlException {
    // TODO Auto-generated method stub
    Operand operand = operandFactory.createOperand(columnMetadata.getValue());
    return new ColumnImpl(columnMetadata, operand, justUsed4Order);
  }

  @Override
  public Filter createFilter(ConditionMetadata conditionMetadata)
      throws MoqlException {
    // TODO Auto-generated method stub
    return createCondition(conditionMetadata, null);
  }

  protected Condition createCondition(ConditionMetadata conditionMetadata,
      SelectorImpl selector) throws MoqlException {
    OperationMetadata operationMetadata = conditionMetadata.getOperation();
    Operand operand = createOperand(operationMetadata, selector);
    return new ConditionImpl(conditionMetadata, operand);
  }

  protected Operand createOperand(OperationMetadata operationMetadata,
      SelectorImpl selector) throws MoqlException {
    Operand operand = null;
    if (operationMetadata instanceof LogicOperationMetadata) {
      operand = createLogicOperand((LogicOperationMetadata) operationMetadata,
          selector);
    } else if (operationMetadata instanceof RelationOperationMetadata) {
      operand = createRelationOperand(
          (RelationOperationMetadata) operationMetadata, selector);
    } else if (operationMetadata instanceof ParenMetadata) {
      ParenMetadata parenMetadata = (ParenMetadata) operationMetadata;
      operand = createOperand(parenMetadata.getOperand(), selector);
      operand = new ParenExpression(operand);
    }
    return operand;
  }

  protected Operand createLogicOperand(
      LogicOperationMetadata logicOperationMetadata, SelectorImpl selector)
      throws MoqlException {
    Operand lOperand = null;
    if (!LogicExpressionFactory.isUnary(logicOperationMetadata.getOperator())) {
      OperationMetadata lOperationMetadata = logicOperationMetadata
          .getLeftOperand();
      lOperand = createOperand(lOperationMetadata, selector);
    }
    Operand rOperand = createOperand(logicOperationMetadata.getRightOperand(),
        selector);
    return LogicExpressionFactory.createLogicExpression(
        logicOperationMetadata.getOperator(), lOperand, rOperand);
  }

  protected Operand createRelationOperand(
      RelationOperationMetadata relationOperationMetadata, SelectorImpl selector)
      throws MoqlException {
    Operand lOperand = null;
    if (!RelationExpressionFactory.isUnary(relationOperationMetadata
        .getOperator())) {
      lOperand = operandFactory.createOperand(relationOperationMetadata
          .getLeftOperand());
    }
    Operand rOperand = null;
    if (relationOperationMetadata.getNestedSelector() != null) {
      if (selector == null) {
        throw new MoqlException("Doesn't support nested selector in condition!");
      }
      Selector nestedSelector = createSelector(relationOperationMetadata
          .getNestedSelector());
      selector.getNestedTableSelectors().add(nestedSelector);
      rOperand = new ColumnSelectorOperand(nestedSelector);
    } else {
      rOperand = operandFactory.createOperand(relationOperationMetadata
          .getRightOperand());
    }
    return RelationExpressionFactory.createRelationExpression(
        relationOperationMetadata.getOperator(), lOperand, rOperand);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.moql.core.SelectorFactory#createSelectors(java.util.List)
   */
  @Override
  public Selector createSelector(SelectorDefinition selectorDefinition)
      throws MoqlException {
    Selector selector;
    if (selectorDefinition instanceof SelectorMetadata) {
      selector = baseCreateSelector((SelectorMetadata) selectorDefinition);
    } else {
      selector = baseCreateSetlector((SetlectorMetadata) selectorDefinition);
    }
    return selector;
  }

  protected Selector baseCreateSelector(SelectorMetadata selectorMetadata)
      throws MoqlException {
    SelectorImpl selector = new SelectorImpl(selectorMetadata);
    Tables tables = createTables(selectorMetadata.getTables(), selector);
    selector.setTables(tables);
    Columns columns = createColumns(selectorMetadata.getColumns(),
        selectorMetadata.getOrderBy(), selector);
    RecordSetOperator recordSetOperator;
    if (selectorMetadata.getGroupBy() == null
        || selectorMetadata.getGroupBy().size() == 0) {
      recordSetOperator = new ColumnsRecordSetOperator(
          selectorMetadata.getCache(), columns);
    } else {
      recordSetOperator = new GroupRecordSetOperator(
          selectorMetadata.getCache(), columns, selectorMetadata.getGroupBy(),
          this);
    }
    selector.setRecordSetOperator(recordSetOperator);
    if (selectorMetadata.getWhere() != null) {
      Condition condition = createCondition(selectorMetadata.getWhere(),
          selector);
      selector.setWhere(condition);
    }
    if (selectorMetadata.getHaving() != null) {
      Condition condition = createCondition(selectorMetadata.getHaving(),
          selector);
      Having having = new HavingImpl(condition);
      selector.setHaving(having);
    }
    if (selectorMetadata.getOrderBy() != null
        && selectorMetadata.getOrderBy().size() != 0) {
      Order order = new OrderImpl(columns, selectorMetadata.getOrderBy());
      selector.setOrder(order);
    }

    if (selectorMetadata.getLimit() != null) {
      Limit limit = new LimitImpl(selectorMetadata.getLimit());
      selector.setLimit(limit);
    }
    if (selectorMetadata.getDecorateBy() != null
        && selectorMetadata.getDecorateBy().size() != 0) {
      Decorator decorator = new DecoratorImpl(selectorMetadata.getDecorateBy(),
          operandFactory);
      selector.setDecorator(decorator);
    }
    return selector;
  }

  protected Tables createTables(TablesMetadata tablesMetadata,
      SelectorImpl selector) throws MoqlException {
    List<QueryableMetadata> queryableMetadatas = tablesMetadata.getTables();
    if (queryableMetadatas.size() == 1) {
      if (queryableMetadatas.get(0) instanceof TableMetadata) {
        Table table = createTable((TableMetadata) queryableMetadatas.get(0),
            selector);
        return new SingleTableTables(tablesMetadata, table);
      } else {
        Join join = createJoin((JoinMetadata) queryableMetadatas.get(0),
            selector);
        return new JoinTables(tablesMetadata, join);
      }
    } else {
      Join join = createJoin(queryableMetadatas, selector);
      return new JoinTables(tablesMetadata, join);
    }
  }

  protected Table createTable(TableMetadata tableMetadata, SelectorImpl selector)
      throws MoqlException {
    if (tableMetadata.getNestedSelector() != null) {
      Selector nestedSelector = createSelector(tableMetadata
          .getNestedSelector());
      nestedSelector.setAlias(tableMetadata.getName());
      selector.getNestedTableSelectors().add(nestedSelector);
      return new SelectorTable(tableMetadata, nestedSelector);
    } else {
      return new CommonTable(tableMetadata);
    }
  }

  protected Join createJoin(JoinMetadata joinMetadata, SelectorImpl selector)
      throws MoqlException {
    Queryable<? extends Object> lQueryable;
    Queryable<? extends Object> rQueryable;
    Condition on = null;
    if (joinMetadata.getLQueryable() instanceof JoinMetadata) {
      lQueryable = createJoin((JoinMetadata) joinMetadata.getLQueryable(),
          selector);
    } else {
      lQueryable = createTable((TableMetadata) joinMetadata.getLQueryable(),
          selector);
    }
    if (joinMetadata.getRQueryable() instanceof JoinMetadata) {
      rQueryable = createJoin((JoinMetadata) joinMetadata.getRQueryable(),
          selector);
    } else {
      rQueryable = createTable((TableMetadata) joinMetadata.getRQueryable(),
          selector);
    }
    if (joinMetadata.getOn() != null) {
      on = createCondition(joinMetadata.getOn(), selector);
    }
    return JoinFactory.createJoin(joinMetadata, lQueryable, rQueryable, on);
  }

  protected Join createJoin(List<QueryableMetadata> queryableMetadatas,
      SelectorImpl selector) throws MoqlException {
    Queryable<? extends Object> lQueryable = null;
    Queryable<? extends Object> rQueryable = null;
    QueryableMetadata lQueryableMetadata = null;

    for (QueryableMetadata queryableMetadata : queryableMetadatas) {
      if (lQueryable == null) {
        if (queryableMetadata instanceof TableMetadata) {
          lQueryable = createTable((TableMetadata) queryableMetadata, selector);
        } else {
          lQueryable = createJoin((JoinMetadata) queryableMetadata, selector);
        }
        lQueryableMetadata = queryableMetadata;
        continue;
      }
      if (queryableMetadata instanceof TableMetadata) {
        rQueryable = createTable((TableMetadata) queryableMetadata, selector);
      } else {
        rQueryable = createJoin((JoinMetadata) queryableMetadata, selector);
      }
      JoinMetadata joinMetadata = new JoinMetadata(JoinType.INNER,
          lQueryableMetadata, queryableMetadata);
      lQueryable = JoinFactory.createJoin(joinMetadata, lQueryable, rQueryable,
          null);
      lQueryableMetadata = joinMetadata;
    }
    return (Join) lQueryable;
  }

  protected Columns createColumns(ColumnsMetadata columnsMetadata,
      List<OrderMetadata> orderMetadatas, SelectorImpl selector)
      throws MoqlException {
    List<Column> columns = new LinkedList<Column>();
    for (ColumnMetadata columnMetadata : columnsMetadata.getColumns()) {
      Column column = createColumn(columnMetadata,
          selector.getNestedColumnSelectors());
      columns.add(column);
    }
    // insert the column used only for order
    if (orderMetadatas != null) {
      String column;
      for (OrderMetadata orderMetadata : orderMetadatas) {
        column = orderMetadata.getColumn();
        try {
          int index = Integer.valueOf(column);
          if (index > columns.size() || index < 0) {
            throw new MoqlException(StringFormater.format(
                "Order index {} is out of boundary!", index));
          }
          continue;
        } catch (Throwable t) {
        }

        if (findColumnMetadata(column, columnsMetadata.getColumns()) == null) {
          // if (columnsMetadata.isDistinct()
          // || ((SelectorMetadata) selector.getSelectorDefinition())
          // .getGroupBy().size() != 0) {
          // throw new MoqlException(
          // "Order by column must appear in SELECT list when SELECT used GROUP BY or DISTINCT!");
          // }
          ColumnMetadata columnMetadata = new ColumnMetadata(column, column);
          Operand operand = operandFactory.createOperand(columnMetadata
              .getValue());
          columns.add(new ColumnImpl(columnMetadata, operand, true));
        }
      }
    }
    return new ColumnsImpl(columnsMetadata, columns);
  }

  protected ColumnMetadata findColumnMetadata(String name,
      List<ColumnMetadata> columnMetadatas) {
    for (ColumnMetadata columnMetadata : columnMetadatas) {
      if (columnMetadata.getName().equals(name))
        return columnMetadata;
    }
    return null;
  }

  protected Column createColumn(ColumnMetadata columnMetadata,
      List<Selector> nestedColumnSelectors) throws MoqlException {
    if (columnMetadata.getNestedSelector() == null) {
      Operand operand = null;
      operand = operandFactory.createOperand(columnMetadata.getValue());
      return new ColumnImpl(columnMetadata, operand);
    } else {
      Selector nestedSelector = createSelector(columnMetadata
          .getNestedSelector());
      nestedSelector.setAlias(columnMetadata.getName());
      nestedColumnSelectors.add(nestedSelector);
      Operand operand = new ValueSelectorOperand(nestedSelector);
      return new ColumnImpl(columnMetadata, operand);
    }
  }

  protected Selector baseCreateSetlector(SetlectorMetadata setlectorMetadata)
      throws MoqlException {
    SetlectorImpl setlector = new SetlectorImpl(setlectorMetadata);
    RecordSetCombination combination = RecordSetCombinationFactory
        .createRecordSetCombination(setlectorMetadata.getCombinationType(),
            setlectorMetadata.getColumns());
    setlector.setCombination(combination);

    for (SelectorDefinition selectorDefinition : setlectorMetadata.getSets()) {
      Selector selector = createSelector(selectorDefinition);
      setlector.getNestedSelectors().add(selector);
      setlector.getSets().add(selector);
    }

    if (setlectorMetadata.getOrderBy() != null
        && setlectorMetadata.getOrderBy().size() != 0) {
      ColumnsMetadata columnsMetadata = getColumnsMetadata(setlectorMetadata);
      Columns columns = createColumns(columnsMetadata, setlector);
      Order order = new OrderImpl(columns, setlectorMetadata.getOrderBy());
      setlector.setOrder(order);
    }
    if (setlectorMetadata.getLimit() != null) {
      Limit limit = new LimitImpl(setlectorMetadata.getLimit());
      setlector.setLimit(limit);
    }
    if (setlectorMetadata.getDecorateBy() != null
        && setlectorMetadata.getDecorateBy().size() != 0) {
      Decorator decorator = new DecoratorImpl(
          setlectorMetadata.getDecorateBy(), operandFactory);
      setlector.setDecorator(decorator);
    }
    return setlector;
  }

  protected ColumnsMetadata getColumnsMetadata(
      SelectorDefinition selectorDefinition) {
    if (selectorDefinition instanceof SelectorMetadata) {
      return ((SelectorMetadata) selectorDefinition).getColumns();
    } else {
      SetlectorMetadata setlectorMetadata = (SetlectorMetadata) selectorDefinition;
      if (setlectorMetadata.getColumns().getColumns().size() == 0) {
        SelectorDefinition lSelectorDefinition = setlectorMetadata.getSets()
            .get(0);
        return getColumnsMetadata(lSelectorDefinition);
      } else {
        return setlectorMetadata.getColumns();
      }
    }
  }

  protected Columns createColumns(ColumnsMetadata columnsMetadata,
      SetlectorImpl setlector) throws MoqlException {
    List<Column> columns = new LinkedList<Column>();
    for (ColumnMetadata columnMetadata : columnsMetadata.getColumns()) {
      Column column = createColumn(columnMetadata, new LinkedList<Selector>());
      columns.add(column);
    }
    return new ColumnsImpl(columnsMetadata, columns);
  }

  class SelectorBean {
    protected SelectorDefinition definition;
    protected Selector selector;

    public SelectorBean(SelectorDefinition definition) {
      Validate.notNull(definition, "Parameter 'definition' is null!");
      this.definition = definition;
    }

    public SelectorDefinition getDefinition() {
      return definition;
    }

    public Selector getSelector() {
      return selector;
    }

    public void setSelector(Selector selector) {
      Validate.notNull(selector, "Parameter 'selector' is null!");
      this.selector = selector;
    }
  }

  public OperandFactory getOperandFactory() {
    return operandFactory;
  }

  public void setOperandFactory(OperandFactory operandFactory) {
    Validate.notNull(operandFactory, "Parameter 'operandFactory' is null!");
    this.operandFactory = operandFactory;
  }

}
