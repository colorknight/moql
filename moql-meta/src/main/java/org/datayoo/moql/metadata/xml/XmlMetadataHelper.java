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
package org.datayoo.moql.metadata.xml;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.util.StringFormater;
import org.datayoo.moql.xml.XmlAccessException;
import org.datayoo.moql.xml.XmlElementFormater;
import org.dom4j.*;

import java.util.*;

/**
 * 
 * @author Tang Tadin
 * 
 */
class XmlMetadataHelper {

  public static final String SELECTOR_ELEMENT = "selector";

  public static final String SETLECTOR_ELEMENT = "setlector";

  public static final String FILTER_ELEMENT = "filter";

  public static final String CACHE_ELEMENT = "cache";

  public static final String LIMIT_ELEMENT = "limit";

  public static final String COLUMNS_ELEMENT = "columns";

  public static final String COLUMN_ELEMENT = "column";

  public static final String TABLES_ELEMENT = "tables";

  public static final String TABLE_ELEMENT = "table";

  public static final String JOIN_ELEMENT = "join";

  public static final String ON_ELEMENT = "on";

  public static final String SETS_ELEMENT = "sets";

  public static final String WHERE_ELEMENT = "where";

  public static final String GROUPBY_ELEMENT = "groupby";

  public static final String GROUP_ELEMENT = "group";

  public static final String HAVING_ELEMENT = "having";

  public static final String ORDERBY_ELEMENT = "orderby";

  public static final String ORDER_ELEMENT = "order";

  public static final String NOT_ELEMENT = "not";

  public static final String AND_ELEMENT = "and";

  public static final String OR_ELEMENT = "or";

  public static final String PAREN_ELEMENT = "paren";

  public static final String UNARY_ELEMENT = "unary";

  public static final String BINARY_ELEMENT = "binary";

  // Attribute

  public static final String COMBINATION_ATTRIBUTE = "combination";

  public static final String SIZE_ATTRIBUTE = "size";

  public static final String WASHOUT_ATTRIBUTE = "washout";

  public static final String VALUE_ATTRIBUTE = "value";

  public static final String DISTINCT_ATTRIBUTE = "distinct";

  public static final String NAME_ATTRIBUTE = "name";

  public static final String OPERAND_ATTRIBUTE = "operand";

  public static final String LEFT_OPERAND_ATTRIBUTE = "loperand";

  public static final String RIGHT_OPERAND_ATTRIBUTE = "roperand";

  public static final String OPERATOR_ATTRIBUTE = "operator";

  public static final String COLUMN_ATTRIBUTE = "column";

  public static final String MODE_ATTRIBUTE = "mode";

  public static final String OFFSET_ATTRIBUTE = "offset";

  protected Map<String, XmlElementFormater<Object>> extendedElementFormaters = new HashMap<String, XmlElementFormater<Object>>();

  public SelectorDefinition readSelectorDefinition(Element element)
      throws XmlAccessException {
    Validate.notNull(element, "Parameter 'element' is null!");
    if (!element.getName().equals(SELECTOR_ELEMENT)
        && !element.getName().equals(SETLECTOR_ELEMENT)) {
      throw new IllegalArgumentException(StringFormater.format(
          "Invalid element '{}'!", element.getName()));
    }

    if (element.getName().equals(SELECTOR_ELEMENT)) {
      return readSelectorMetadata(element);
    } else {
      return readSetlectorMetadata(element);
    }
  }

  protected String getAttribute(Element element, String attribute,
      boolean option) throws XmlAccessException {
    Attribute attr = (Attribute) element.attribute(attribute);
    if (attr != null) {
      return attr.getValue();
    }
    if (option)
      return null;
    throw new XmlAccessException(StringFormater.format(
        "There is no attribute '{}' in element '{}'!", attribute,
        element.getName()));
  }

  protected String getElementText(Element element, String textElement,
      boolean option) throws XmlAccessException {
    Element el = (Element) element.element(textElement);
    if (el != null) {
      return el.getTextTrim();
    }
    if (option)
      return null;
    throw new XmlAccessException(StringFormater.format(
        "There is no element '{}' in element '{}'!", textElement,
        element.getName()));
  }

  protected SelectorMetadata readSelectorMetadata(Element element)
      throws XmlAccessException {
    SelectorMetadata selector = new SelectorMetadata();
    readSelectorMetadata(element, selector);
    return selector;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected void readSelectorMetadata(Element element, SelectorMetadata selector)
      throws XmlAccessException {

    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(CACHE_ELEMENT)) {
        CacheMetadata cache = readCacheMetadata(el);
        selector.setCache(cache);
      } else if (el.getName().equals(COLUMNS_ELEMENT)) {
        ColumnsMetadata columns = readColumnsMetadata(el);
        selector.setColumns(columns);
      } else if (el.getName().equals(TABLES_ELEMENT)) {
        TablesMetadata tables = readTablesMetadata(el);
        selector.setTables(tables);
      } else if (el.getName().equals(WHERE_ELEMENT)) {
        ConditionMetadata where = innerReadConditionMetadata(el);
        selector.setWhere(where);
      } else if (el.getName().equals(GROUPBY_ELEMENT)) {
        List<GroupMetadata> groups = readGroupBy(el);
        selector.setGroupBy(groups);
      } else if (el.getName().equals(HAVING_ELEMENT)) {
        ConditionMetadata having = innerReadConditionMetadata(el);
        selector.setHaving(having);
      } else if (el.getName().equals(ORDERBY_ELEMENT)) {
        List<OrderMetadata> orders = readOrderBy(el);
        selector.setOrderBy(orders);
      } else if (el.getName().equals(LIMIT_ELEMENT)) {
        LimitMetadata limit = readLimitMetadata(el);
        selector.setLimit(limit);
      }
    }
  }

  protected CacheMetadata readCacheMetadata(Element element)
      throws XmlAccessException {
    String value = getAttribute(element, SIZE_ATTRIBUTE, false);
    CacheMetadata cacheMetadata = new CacheMetadata(Integer.valueOf(value));
    value = getAttribute(element, WASHOUT_ATTRIBUTE, true);
    if (value != null) {
      cacheMetadata.setWashoutStrategy(WashoutStrategy.valueOf(value));
    }
    return cacheMetadata;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected ColumnsMetadata readColumnsMetadata(Element element)
      throws XmlAccessException {
    ColumnsMetadata columnsMetadata = new ColumnsMetadata();
    String value = getAttribute(element, DISTINCT_ATTRIBUTE, true);
    if (value != null) {
      columnsMetadata.setDistinct(Boolean.valueOf(value));
    }
    List<ColumnMetadata> columns = new LinkedList<ColumnMetadata>();
    for (Iterator it = element.elementIterator(COLUMN_ELEMENT); it.hasNext();) {
      Element el = (Element) it.next();
      ColumnMetadata column = readColumnMetadata(el);
      columns.add(column);
    }
    columnsMetadata.setColumns(columns);
    return columnsMetadata;
  }

  protected ColumnMetadata readColumnMetadata(Element element)
      throws XmlAccessException {
    ColumnMetadata column;
    String name = getAttribute(element, NAME_ATTRIBUTE, false);
    String value = getAttribute(element, VALUE_ATTRIBUTE, true);
    if (value == null) {
      SelectorDefinition nestedSelector = readColumnSelectorMetadata(element);
      column = new ColumnMetadata(name, nestedSelector);
    } else {
      column = new ColumnMetadata(name, value);
    }
    return column;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected SelectorDefinition readColumnSelectorMetadata(Element element)
      throws XmlAccessException {
    List elements = element.elements();
    if (elements == null || elements.size() == 0) {
      throw new XmlAccessException(StringFormater.format(
          "Invalid element '{}' has no selector or setlector element!",
          element.getName()));
    }
    Element el = (Element) elements.get(0);
    if (el.getName().equals(SELECTOR_ELEMENT)) {
      SelectorMetadata columnSelector = new SelectorMetadata();
      readSelectorMetadata(el, columnSelector);
      return columnSelector;
    } else {
      SetlectorMetadata columnSelector = new SetlectorMetadata();
      readSetlectorMetadata(el, columnSelector);
      return columnSelector;
    }
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected TablesMetadata readTablesMetadata(Element element)
      throws XmlAccessException {
    List<QueryableMetadata> tables = new LinkedList<QueryableMetadata>();
    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(TABLE_ELEMENT)) {
        TableMetadata tableMetadata = readTableMetadata(el);
        tables.add(tableMetadata);
      } else if (el.getName().equals(JOIN_ELEMENT)) {
        JoinMetadata joinMetadata = readJoinMetadata(el);
        tables.add(joinMetadata);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid element '{}' in element '{}'!", element.getName(),
            TABLES_ELEMENT));
      }
    }
    return new TablesMetadata(tables);
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected TableMetadata readTableMetadata(Element element)
      throws XmlAccessException {
    TableMetadata table;
    String name = getAttribute(element, NAME_ATTRIBUTE, false);
    String value = getAttribute(element, VALUE_ATTRIBUTE, true);
    if (value == null) {
      List elements = element.elements();
      if (elements == null || elements.size() == 0) {
        throw new XmlAccessException(
            StringFormater
                .format(
                    "Invalid element '{}' has no selector,setlector or reference element!",
                    element.getName()));
      }
      Element el = (Element) elements.get(0);
      if (el.getName().equals(SELECTOR_ELEMENT)) {
        SelectorMetadata nestedSelector = new SelectorMetadata();
        readSelectorMetadata(el, nestedSelector);
        table = new TableMetadata(name, nestedSelector);
      } else {
        SetlectorMetadata nestedSelector = new SetlectorMetadata();
        readSetlectorMetadata(el, nestedSelector);
        table = new TableMetadata(name, nestedSelector);
      }
    } else {
      table = new TableMetadata(name, value);
    }
    return table;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected JoinMetadata readJoinMetadata(Element element)
      throws XmlAccessException {
    String value = getAttribute(element, MODE_ATTRIBUTE, false);
    JoinType joinType = JoinType.valueOf(value);
    QueryableMetadata lQueryable = null;
    QueryableMetadata rQueryable = null;
    ConditionMetadata condition = null;

    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(TABLE_ELEMENT)) {
        if (lQueryable == null)
          lQueryable = readTableMetadata(el);
        else
          rQueryable = readTableMetadata(el);
      } else if (el.getName().equals(JOIN_ELEMENT)) {
        if (lQueryable == null)
          lQueryable = readJoinMetadata(el);
        else
          rQueryable = readJoinMetadata(el);
      } else if (el.getName().equals(ON_ELEMENT)) {
        condition = innerReadConditionMetadata(el);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid element '{}' in element '{}'!", element.getName(),
            TABLES_ELEMENT));
      }
    }
    JoinMetadata join = new JoinMetadata(joinType, lQueryable, rQueryable);
    if (condition != null)
      join.setOn(condition);
    return join;
  }

  protected ConditionMetadata innerReadConditionMetadata(Element element)
      throws XmlAccessException {
    OperationMetadata operation = readOperationMetadata(element);
    return new ConditionMetadata(operation);
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected OperationMetadata readOperationMetadata(Element element)
      throws XmlAccessException {
    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(AND_ELEMENT)) {
        return readLogicOperationMetadata(el);
      } else if (el.getName().equals(OR_ELEMENT)) {
        return readLogicOperationMetadata(el);
      } else if (el.getName().equals(NOT_ELEMENT)) {
        return readLogicOperationMetadata(el);
      } else if (el.getName().equals(PAREN_ELEMENT)) {
        return readParenMetadata(el);
      } else if (el.getName().equals(UNARY_ELEMENT)) {
        return readUnaryRelationOperationMetadata(el);
      } else if (el.getName().equals(BINARY_ELEMENT)) {
        return readBinaryRelationOperationMetadata(el);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid operation element '{}' in condition element '{}'!",
            el.getName(), element.getName()));
      }
    }
    throw new XmlAccessException(StringFormater.format(
        "Invalid condition element '{}' !", element.getName()));
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected LogicOperationMetadata readLogicOperationMetadata(Element element)
      throws XmlAccessException {
    int size = 1;
    String operator;
    if (element.getName().equals(AND_ELEMENT)
        || element.getName().equals(OR_ELEMENT)) {
      size = 2;
      operator = element.getName();
    } else {
      operator = element.getName();
    }
    OperationMetadata[] operands = new OperationMetadata[size];
    int inx = 0;
    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(AND_ELEMENT)) {
        operands[inx] = readLogicOperationMetadata(el);
      } else if (el.getName().equals(OR_ELEMENT)) {
        operands[inx] = readLogicOperationMetadata(el);
      } else if (el.getName().equals(NOT_ELEMENT)) {
        operands[inx] = readLogicOperationMetadata(el);
      } else if (el.getName().equals(PAREN_ELEMENT)) {
        operands[inx] = readParenMetadata(el);
      } else if (el.getName().equals(UNARY_ELEMENT)) {
        operands[inx] = readUnaryRelationOperationMetadata(el);
      } else if (el.getName().equals(BINARY_ELEMENT)) {
        operands[inx] = readBinaryRelationOperationMetadata(el);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid operation element '{}' !", el.getName()));
      }
      if (++inx == size)
        break;
    }
    if (inx == 0) {
      throw new XmlAccessException(StringFormater.format(
          "Invalid operation element '{}' !", element.getName()));
    }
    if (size == 1) {
      return new LogicOperationMetadata(operator, operands[0]);
    } else {
      return new LogicOperationMetadata(operator, operands[0], operands[1]);
    }
  }

  protected ParenMetadata readParenMetadata(Element element)
      throws XmlAccessException {
    OperationMetadata operationMetadata = readOperationMetadata(element);
    return new ParenMetadata(operationMetadata);
  }

  protected RelationOperationMetadata readBinaryRelationOperationMetadata(
      Element element) throws XmlAccessException {
    RelationOperationMetadata relation;
    String operator = getAttribute(element, OPERATOR_ATTRIBUTE, false);
    String lOperand = getAttribute(element, LEFT_OPERAND_ATTRIBUTE, false);
    String rOperand = getAttribute(element, RIGHT_OPERAND_ATTRIBUTE, true);
    if (rOperand == null) {
      SelectorDefinition nestedSelector = readColumnSelectorMetadata(element);
      relation = new RelationOperationMetadata(operator, lOperand,
          nestedSelector);
    } else {
      relation = new RelationOperationMetadata(operator, lOperand, rOperand);
    }
    return relation;
  }

  protected RelationOperationMetadata readUnaryRelationOperationMetadata(
      Element element) throws XmlAccessException {
    RelationOperationMetadata relation;
    String operator = getAttribute(element, OPERATOR_ATTRIBUTE, false);
    String operand = getAttribute(element, OPERAND_ATTRIBUTE, true);
    if (operand == null) {
      Element el = element.element(SELECTOR_ELEMENT);
      SelectorDefinition nestedSelector = readColumnSelectorMetadata(el);
      relation = new RelationOperationMetadata(operator, nestedSelector);
    } else {
      relation = new RelationOperationMetadata(operator, operand);
    }
    return relation;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected List<GroupMetadata> readGroupBy(Element element)
      throws XmlAccessException {
    List<GroupMetadata> groups = new LinkedList<GroupMetadata>();
    for (Iterator it = element.elementIterator(GROUP_ELEMENT); it.hasNext();) {
      Element el = (Element) it.next();
      String column = getAttribute(el, COLUMN_ATTRIBUTE, false);
      GroupMetadata group = new GroupMetadata(column);
      groups.add(group);
    }
    return groups;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected List<OrderMetadata> readOrderBy(Element element)
      throws XmlAccessException {
    List<OrderMetadata> orders = new LinkedList<OrderMetadata>();
    for (Iterator it = element.elementIterator(ORDER_ELEMENT); it.hasNext();) {
      Element el = (Element) it.next();
      OrderMetadata order = readOrderMetadata(el);
      orders.add(order);
    }
    return orders;
  }

  protected OrderMetadata readOrderMetadata(Element element)
      throws XmlAccessException {
    String column = getAttribute(element, COLUMN_ATTRIBUTE, false);
    OrderMetadata order = new OrderMetadata(column);
    String value = getAttribute(element, MODE_ATTRIBUTE, true);
    if (value != null) {
      order.setOrderType(OrderType.valueOf(value));
    }
    return order;
  }

  protected LimitMetadata readLimitMetadata(Element element)
      throws XmlAccessException {
    String value = getAttribute(element, OFFSET_ATTRIBUTE, true);
    int startPos = 0;
    if (value != null) {
      startPos = Integer.valueOf(value);
    }
    value = getAttribute(element, VALUE_ATTRIBUTE, false);
    boolean percent = false;
    if (value.charAt(value.length() - 1) == '%') {
      value = value.substring(0, value.length() - 1);
      percent = true;
    }
    LimitMetadata limitMetadata = new LimitMetadata(startPos,
        Integer.valueOf(value), percent);
    return limitMetadata;
  }

  protected SetlectorMetadata readSetlectorMetadata(Element element)
      throws XmlAccessException {
    SetlectorMetadata setlector = new SetlectorMetadata();
    readSetlectorMetadata(element, setlector);
    return setlector;
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected void readSetlectorMetadata(Element element,
      SetlectorMetadata setlector) throws XmlAccessException {
    String value = getAttribute(element, COMBINATION_ATTRIBUTE, true);
    if (value != null)
      setlector.setCombinationType(CombinationType.valueOf(value));
    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(COLUMNS_ELEMENT)) {
        ColumnsMetadata columns = readColumnsMetadata(el);
        setlector.setColumns(columns);
      } else if (el.getName().equals(SETS_ELEMENT)) {
        List<SelectorDefinition> sets = readSets(el);
        setlector.setSets(sets);
      } else if (el.getName().equals(ORDERBY_ELEMENT)) {
        List<OrderMetadata> orders = readOrderBy(el);
        setlector.setOrderBy(orders);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid element '{}' in element '{}'!", el.getName(),
            element.getName()));
      }
    }
  }

  @SuppressWarnings({
    "rawtypes"
  })
  protected List<SelectorDefinition> readSets(Element element)
      throws XmlAccessException {
    List<SelectorDefinition> sets = new LinkedList<SelectorDefinition>();
    int i = 0;
    for (Iterator it = element.elementIterator(); it.hasNext();) {
      Element el = (Element) it.next();
      if (el.getName().equals(SELECTOR_ELEMENT)) {
        SelectorMetadata nestedSelector = new SelectorMetadata();
        readSelectorMetadata(el, nestedSelector);
        sets.add(nestedSelector);
      } else if (el.getName().equals(SETLECTOR_ELEMENT)) {
        SetlectorMetadata nestedSetlector = new SetlectorMetadata();
        readSetlectorMetadata(el, nestedSetlector);
        sets.add(nestedSetlector);
      } else {
        throw new XmlAccessException(StringFormater.format(
            "Invalid element '{}' in element '{}'!", el.getName(),
            element.getName()));
      }
      if (++i == 2)
        break;
    }
    if (i != 2) {
      throw new XmlAccessException(StringFormater.format(
          "Invalid element '{}'!", element.getName()));
    }
    return sets;
  }

  public ConditionMetadata readConditionMetadata(Element element)
      throws XmlAccessException {
    OperationMetadata operation = readOperationMetadata(element);
    return new ConditionMetadata(operation);
  }

  public Element writeSelectorDefinition(Element element,
      SelectorDefinition selectorDefinition) throws XmlAccessException {
    Validate.notNull(selectorDefinition,
        "Parameter 'selectorDefinition' is null!");
    if (selectorDefinition instanceof SelectorMetadata) {
      return writeSelectorMetadata(element,
          (SelectorMetadata) selectorDefinition);
    } else {
      return writeSetlectorMetadata(element,
          (SetlectorMetadata) selectorDefinition);
    }
  }

  protected Element createElement(Element element, String rootName) {
    Element elRoot = null;
    if (element != null) {
      elRoot = element.addElement(rootName);
    } else {
      Document doc = DocumentHelper.createDocument();
      elRoot = doc.addElement(rootName, "http://www.datayoo.org/schema/moql");
      elRoot.add(new Namespace("xsi",
          "http://www.w3.org/2001/XMLSchema-instance"));
      elRoot.addAttribute("xsi:schemaLocation",
          "http://www.datayoo.org/schema/moql selector-base.xsd");

    }
    return elRoot;
  }

  protected Element writeSelectorMetadata(Element element,
      SelectorMetadata selector) throws XmlAccessException {
    Element elSelector = createElement(element, SELECTOR_ELEMENT);
    writeNestedSelectorMetadataWithoutId(elSelector, selector);
    return elSelector;
  }

  protected void writeNestedSelectorMetadataWithoutId(Element element,
      SelectorMetadata selector) throws XmlAccessException {
    writeCacheMetadata(element, selector.getCache());
    writeColumnsMetadata(element, selector.getColumns());
    writeTablesMetadata(element, selector.getTables());
    if (selector.getWhere() != null) {
      innerWriteConditionMetadata(element, WHERE_ELEMENT, selector.getWhere());
    }
    if (selector.getGroupBy() != null) {
      writeGroupBy(element, selector.getGroupBy());
    }
    if (selector.getHaving() != null) {
      innerWriteConditionMetadata(element, HAVING_ELEMENT, selector.getHaving());
    }
    if (selector.getOrderBy() != null) {
      writeOrderBy(element, selector.getOrderBy());
    }
    if (selector.getLimit() != null)
      writeLimitMetadata(element, selector.getLimit());
  }

  protected void writeCacheMetadata(Element element, CacheMetadata cache) {
    Element elCache = element.addElement(CACHE_ELEMENT);
    elCache.addAttribute(SIZE_ATTRIBUTE, String.valueOf(cache.getSize()));
    if (cache.getWashoutStrategy() != WashoutStrategy.FIFO) {
      elCache
          .addAttribute(WASHOUT_ATTRIBUTE, cache.getWashoutStrategy().name());
    }
  }

  protected void writeColumnsMetadata(Element element, ColumnsMetadata columns)
      throws XmlAccessException {
    Element elColumns = element.addElement(COLUMNS_ELEMENT);
    if (columns.isDistinct()) {
      elColumns.addAttribute(DISTINCT_ATTRIBUTE,
          String.valueOf(columns.isDistinct()));
    }
    for (ColumnMetadata column : columns.getColumns()) {
      writeColumnMetadata(elColumns, column);
    }
  }

  protected void writeColumnMetadata(Element element, ColumnMetadata column)
      throws XmlAccessException {
    Element elColumn = element.addElement(COLUMN_ELEMENT);
    elColumn.addAttribute(NAME_ATTRIBUTE, column.getName());
    if (column.getNestedSelector() != null) {
      SelectorDefinition selectorDefinition = column.getNestedSelector();
      if (selectorDefinition instanceof SelectorMetadata) {
        Element elSelector = elColumn.addElement(SELECTOR_ELEMENT);
        writeNestedSelectorMetadataWithoutId(elSelector,
            (SelectorMetadata) selectorDefinition);
      } else {
        Element elSelector = elColumn.addElement(SETLECTOR_ELEMENT);
        writeSetlectorMetadataWithoutId(elSelector,
            (SetlectorMetadata) selectorDefinition);
      }
    } else {
      if (column.getValue() != null) {
        elColumn.addAttribute(VALUE_ATTRIBUTE, column.getValue());
      }
    }
  }

  protected void writeTablesMetadata(Element element, TablesMetadata tables)
      throws XmlAccessException {
    Element elTables = element.addElement(TABLES_ELEMENT);
    for (QueryableMetadata queryableMetadata : tables.getTables()) {
      if (queryableMetadata instanceof TableMetadata) {
        writeTableMetadata(elTables, (TableMetadata) queryableMetadata);
      } else {
        writeJoinMetadata(elTables, (JoinMetadata) queryableMetadata);
      }
    }
  }

  protected void writeTableMetadata(Element element, TableMetadata table)
      throws XmlAccessException {
    Element elTable = element.addElement(TABLE_ELEMENT);
    elTable.addAttribute(NAME_ATTRIBUTE, table.getName());
    if (table.getValue() != null) {
      elTable.addAttribute(VALUE_ATTRIBUTE, table.getValue());
    } else {
      SelectorDefinition nestedSelector = table.getNestedSelector();
      if (nestedSelector instanceof SelectorMetadata) {
        Element elSelector = elTable.addElement(SELECTOR_ELEMENT);
        writeNestedSelectorMetadataWithoutId(elSelector,
            (SelectorMetadata) nestedSelector);
      } else {
        Element elSelector = elTable.addElement(SETLECTOR_ELEMENT);
        writeSetlectorMetadataWithoutId(elSelector,
            (SetlectorMetadata) nestedSelector);
      }
    }
  }

  protected void writeJoinMetadata(Element element, JoinMetadata join)
      throws XmlAccessException {
    Element elJoin = element.addElement(JOIN_ELEMENT);
    elJoin.addAttribute(MODE_ATTRIBUTE, join.getJoinType().name());
    QueryableMetadata queryable = join.getLQueryable();
    if (queryable instanceof TableMetadata) {
      writeTableMetadata(elJoin, (TableMetadata) queryable);
    } else {
      writeJoinMetadata(elJoin, (JoinMetadata) queryable);
    }
    queryable = join.getRQueryable();
    if (queryable instanceof TableMetadata) {
      writeTableMetadata(elJoin, (TableMetadata) queryable);
    } else {
      writeJoinMetadata(elJoin, (JoinMetadata) queryable);
    }
    if (join.getOn() != null) {
      innerWriteConditionMetadata(elJoin, ON_ELEMENT, join.getOn());
    }
  }

  protected void innerWriteConditionMetadata(Element element,
      String elementName, ConditionMetadata condition)
      throws XmlAccessException {
    Element elCondition = element.addElement(elementName);
    writeOperationMetadata(elCondition, condition.getOperation());
  }

  protected Element writeOperationMetadata(Element element,
      OperationMetadata operation) throws XmlAccessException {
    if (operation instanceof LogicOperationMetadata) {
      return writeLogicOperationMetadata(element,
          (LogicOperationMetadata) operation);
    } else {
      return writeRelationOperationMetadata(element,
          (RelationOperationMetadata) operation);
    }
  }

  protected Element writeLogicOperationMetadata(Element element,
      LogicOperationMetadata operation) throws XmlAccessException {
    Element elLogic;
    if (operation.getOperator().equals(AND_ELEMENT)) {
      elLogic = element.addElement(AND_ELEMENT);
    } else if (operation.getOperator().equals(OR_ELEMENT)) {
      elLogic = element.addElement(OR_ELEMENT);
    } else {
      elLogic = element.addElement(NOT_ELEMENT);
    }
    if (operation.getOperatorType() == OperatorType.UNARY) {
      if (operation.getRightOperand() instanceof LogicOperationMetadata) {
        writeLogicOperationMetadata(elLogic,
            (LogicOperationMetadata) operation.getRightOperand());
      } else {
        writeRelationOperationMetadata(elLogic,
            (RelationOperationMetadata) operation.getRightOperand());
      }
    } else {
      if (operation.getLeftOperand() instanceof LogicOperationMetadata) {
        writeLogicOperationMetadata(elLogic,
            (LogicOperationMetadata) operation.getLeftOperand());
      } else if (operation.getLeftOperand() instanceof RelationOperationMetadata) {
        writeRelationOperationMetadata(elLogic,
            (RelationOperationMetadata) operation.getLeftOperand());
      } else {
        writeParenMetadata(elLogic, (ParenMetadata) operation.getLeftOperand());
      }
      if (operation.getRightOperand() instanceof LogicOperationMetadata) {
        writeLogicOperationMetadata(elLogic,
            (LogicOperationMetadata) operation.getRightOperand());
      } else if (operation.getRightOperand() instanceof RelationOperationMetadata) {
        writeRelationOperationMetadata(elLogic,
            (RelationOperationMetadata) operation.getRightOperand());
      } else {
        writeParenMetadata(elLogic, (ParenMetadata) operation.getRightOperand());
      }
    }
    return elLogic;
  }

  protected Element writeRelationOperationMetadata(Element element,
      RelationOperationMetadata operation) throws XmlAccessException {
    Element elRelation;
    boolean unary = false;
    if (operation.getOperatorType() == OperatorType.UNARY) {
      elRelation = element.addElement(UNARY_ELEMENT);
      unary = true;
    } else {
      elRelation = element.addElement(BINARY_ELEMENT);
      elRelation.addAttribute(LEFT_OPERAND_ATTRIBUTE,
          operation.getLeftOperand());
    }
    elRelation.addAttribute(OPERATOR_ATTRIBUTE, operation.getOperator());
    if (operation.getRightOperand() != null) {
      if (unary) {
        elRelation.addAttribute(OPERAND_ATTRIBUTE, operation.getRightOperand());
      } else {
        elRelation.addAttribute(RIGHT_OPERAND_ATTRIBUTE,
            operation.getRightOperand());
      }
    } else {
      SelectorDefinition selectorDefinition = operation.getNestedSelector();
      if (selectorDefinition instanceof SelectorMetadata) {
        Element elSelector = elRelation.addElement(SELECTOR_ELEMENT);
        writeNestedSelectorMetadataWithoutId(elSelector,
            (SelectorMetadata) selectorDefinition);
      } else {
        Element elSelector = elRelation.addElement(SETLECTOR_ELEMENT);
        writeSetlectorMetadataWithoutId(elSelector,
            (SetlectorMetadata) selectorDefinition);
      }
    }
    return elRelation;
  }

  protected Element writeParenMetadata(Element element, ParenMetadata operation)
      throws XmlAccessException {
    Element elParen = element.addElement(PAREN_ELEMENT);
    writeOperationMetadata(elParen, operation.getOperand());
    return elParen;
  }

  protected void writeGroupBy(Element element, List<GroupMetadata> groupBy) {
    Element elGroupBy = element.addElement(GROUPBY_ELEMENT);
    for (GroupMetadata group : groupBy) {
      Element elGroup = elGroupBy.addElement(GROUP_ELEMENT);
      elGroup.addAttribute(COLUMN_ATTRIBUTE, group.getColumn());
    }
  }

  protected void writeOrderBy(Element element, List<OrderMetadata> orderBy) {
    Element elOrderBy = element.addElement(ORDERBY_ELEMENT);
    for (OrderMetadata order : orderBy) {
      Element elOrder = elOrderBy.addElement(ORDER_ELEMENT);
      elOrder.addAttribute(COLUMN_ATTRIBUTE, order.getColumn());
      if (order.getOrderType() == OrderType.ASC) {
        elOrder.addAttribute(MODE_ATTRIBUTE, order.getOrderType().name());
      }
    }
  }

  protected void writeLimitMetadata(Element element, LimitMetadata limit) {
    Element elLimit = element.addElement(LIMIT_ELEMENT);
    if (limit.getOffset() != 0) {
      elLimit.addAttribute(OFFSET_ATTRIBUTE, String.valueOf(limit.getOffset()));
    }
    String value = String.valueOf(limit.getValue());
    if (limit.isPercent()) {
      value = value + '%';
    }
    elLimit.addAttribute(VALUE_ATTRIBUTE, value);
  }

  protected Element writeSetlectorMetadata(Element element,
      SetlectorMetadata setlector) throws XmlAccessException {
    Element elSetlector = createElement(element, SETLECTOR_ELEMENT);
    writeSetlectorMetadataWithoutId(elSetlector, setlector);
    return elSetlector;
  }

  protected void writeSetlectorMetadataWithoutId(Element element,
      SetlectorMetadata nestedSetlector) throws XmlAccessException {
    if (nestedSetlector.getCombinationType() != null)
      element.addAttribute(COMBINATION_ATTRIBUTE, nestedSetlector
          .getCombinationType().name());

    writeColumnsMetadata(element, nestedSetlector.getColumns());
    writeSets(element, nestedSetlector.getSets());
    if (nestedSetlector.getOrderBy() != null) {
      writeOrderBy(element, nestedSetlector.getOrderBy());
    }
  }

  protected void writeSets(Element element, List<SelectorDefinition> sets)
      throws XmlAccessException {
    Element elSets = element.addElement(SETS_ELEMENT);
    int i = 0;
    for (SelectorDefinition definition : sets) {
      if (definition instanceof SelectorMetadata) {
        SelectorMetadata nestedSelector = (SelectorMetadata) definition;
        Element elSelector = elSets.addElement(SELECTOR_ELEMENT);
        writeNestedSelectorMetadataWithoutId(elSelector, nestedSelector);
      } else {
        Element elNestedSetlector = elSets.addElement(SETLECTOR_ELEMENT);
        writeSetlectorMetadataWithoutId(elNestedSetlector,
            (SetlectorMetadata) definition);
      }
      if (++i == 2)
        break;
    }
  }

  public Element writeFilterMetadata(Element element,
      ConditionMetadata condition) throws XmlAccessException {
    Element elCondition = createElement(element, FILTER_ELEMENT);
    writeOperationMetadata(elCondition, condition.getOperation());
    return elCondition;
  }

}
