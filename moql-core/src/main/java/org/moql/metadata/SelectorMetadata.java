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
package org.moql.metadata;

import org.apache.commons.lang.Validate;
import org.moql.SelectorDefinition;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class SelectorMetadata implements SelectorDefinition, Serializable {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  protected CacheMetadata cache = CacheMetadata.DEFAULT_CACHE;

  protected ColumnsMetadata columns;

  protected TablesMetadata tables;

  protected ConditionMetadata where;

  protected List<GroupMetadata> groupBy;

  protected ConditionMetadata having;

  protected List<OrderMetadata> orderBy;

  protected LimitMetadata limit;

  protected List<DecorateMetadata> decorateBy;

  /**
   * @return the cache
   */
  public CacheMetadata getCache() {
    return cache;
  }

  /**
   * @param cache
   *          the cache to set
   */
  public void setCache(CacheMetadata cache) {
    Validate.notNull(cache, "Parameter 'cache' is null!");
    this.cache = cache;
  }

  /**
   * @return the columns
   */
  public ColumnsMetadata getColumns() {
    return columns;
  }

  /**
   * @param columns
   *          the columns to set
   */
  public void setColumns(ColumnsMetadata columns) {
    Validate.notNull(columns, "Parameter 'columns' is null!");
    this.columns = columns;
  }

  /**
   * @return the tables
   */
  public TablesMetadata getTables() {
    return tables;
  }

  /**
   * @param tables
   *          the tables to set
   */
  public void setTables(TablesMetadata tables) {
    Validate.notNull(tables, "Parameter 'tables' is null!");
    this.tables = tables;
  }

  /**
   * @return the where
   */
  public ConditionMetadata getWhere() {
    return where;
  }

  /**
   * @param where
   *          the where to set
   */
  public void setWhere(ConditionMetadata where) {
    this.where = where;
  }

  /**
   * @return the groupBy
   */
  public List<GroupMetadata> getGroupBy() {
    return groupBy;
  }

  /**
   * @param groupBy
   *          the groupBy to set
   */
  public void setGroupBy(List<GroupMetadata> groupBy) {
    this.groupBy = groupBy;
  }

  /**
   * @return the having
   */
  public ConditionMetadata getHaving() {
    return having;
  }

  /**
   * @param having
   *          the having to set
   */
  public void setHaving(ConditionMetadata having) {
    this.having = having;
  }

  /**
   * @return the orderBy
   */
  public List<OrderMetadata> getOrderBy() {
    return orderBy;
  }

  /**
   * @param orderBy
   *          the orderBy to set
   */
  public void setOrderBy(List<OrderMetadata> orderBy) {
    this.orderBy = orderBy;
  }

  /**
   * 
   * @return the limit
   */
  public LimitMetadata getLimit() {
    return limit;
  }

  /**
   * 
   * @param limit
   *          the limit of the result set
   */
  public void setLimit(LimitMetadata limit) {
    this.limit = limit;
  }

  /**
   * 
   * @return the decorateBy
   */
  public List<DecorateMetadata> getDecorateBy() {
    return decorateBy;
  }

  /**
   * 
   * @param decorateBy
   *          the decorator of the result set
   */
  public void setDecorateBy(List<DecorateMetadata> decorateBy) {
    this.decorateBy = decorateBy;
  }

}
