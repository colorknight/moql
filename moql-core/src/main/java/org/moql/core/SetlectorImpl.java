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
package org.moql.core;

import org.apache.commons.lang.Validate;
import org.moql.*;
import org.moql.metadata.SetlectorMetadata;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class SetlectorImpl implements Selector {

  protected String alias;

  protected SetlectorMetadata setlectorMetadata;

  protected RecordSetCombination combination;

  protected Order order;

  protected Limit limit;

  protected Decorator decorator;

  protected List<Selector> nestedSelectors = new LinkedList<Selector>();

  protected List<Selector> sets = new LinkedList<Selector>();

  protected SelectorContext selectorContext;

  protected RecordSet recentRecordSet;

  public SetlectorImpl(SetlectorMetadata setlectorMetadata) {
    Validate.notNull(setlectorMetadata,
        "Parameter 'setlectorMetadata' is null!");
    this.setlectorMetadata = setlectorMetadata;
  }

  @Override
  public void setAlias(String alias) {
    // TODO Auto-generated method stub
    Validate.notEmpty(alias, "alias is empty!");
    this.alias = alias;
  }

  @Override
  public String getAlias() {
    // TODO Auto-generated method stub
    return alias;
  }

  @Override
  public synchronized RecordSet getRecordSet() {
    // TODO Auto-generated method stub
    if (recentRecordSet != null)
      return recentRecordSet;
    caculateRecordSet();
    return recentRecordSet;
  }

  protected void caculateRecordSet() {
    RecordSet lRecordSet = sets.get(0).getRecordSet();
    RecordSet rRecordSet = sets.get(1).getRecordSet();
    RecordSet recordSet = combination.combine(lRecordSet, rRecordSet);
    if (order != null) {
      recordSet = order.decorate(recordSet, null);
    }
    if (limit != null) {
      recordSet = limit.decorate(recordSet, null);
    }
    if (decorator != null) {
      recordSet = decorator.decorate(recordSet, null);
    }
    recentRecordSet = recordSet;
  }

  /**
   * @return the nestedSelectors
   */
  public List<Selector> getNestedSelectors() {
    return nestedSelectors;
  }

  /**
   * @param nestedSelectors
   *          the nestedSelectors to set
   */
  public void setNestedSelectors(List<Selector> nestedSelectors) {
    if (nestedSelectors == null) {
      nestedSelectors = new LinkedList<Selector>();
    } else {
      if (nestedSelectors.size() > 2) {
        throw new IllegalArgumentException("Nested selector's count exceed 2!");
      }
    }
    this.nestedSelectors = nestedSelectors;
  }

  /**
   * @return the sets
   */
  public List<Selector> getSets() {
    return sets;
  }

  /**
   * @param sets
   */
  public void setSets(List<Selector> sets) {
    Validate.notEmpty(sets, "Parameter 'sets' is empty!");
    if (sets.size() != 2) {
      throw new IllegalArgumentException("Selector's count is not 2!");
    }
    this.sets = sets;
  }

  @Override
  public SelectorContext getSelectorContext() {
    // TODO Auto-generated method stub
    return selectorContext;
  }

  @Override
  public SelectorDefinition getSelectorDefinition() {
    // TODO Auto-generated method stub
    return setlectorMetadata;
  }

  @Override
  public synchronized void clear() {
    // TODO Auto-generated method stub
    recentRecordSet = null;
    for (Selector selector : nestedSelectors) {
      selector.clear();
    }
  }

  @Override
  public synchronized void select(DataSetMap dataSetMap) {
    // TODO Auto-generated method stub
    selectByNestedSelectors(dataSetMap);
  }

  protected void selectByNestedSelectors(DataSetMap dataSetMap) {
    for (Selector selector : nestedSelectors) {
      selector.select(dataSetMap);
    }
  }

  @Override
  public void setSelectorContext(SelectorContext context) {
    // TODO Auto-generated method stub
    Validate.notNull(context, "Parameter 'context' is null!");
    this.selectorContext = context;
  }

  /**
   * @return the combination
   */
  public RecordSetCombination getCombination() {
    return combination;
  }

  /**
   * @param combination
   *          the combination to set
   */
  public void setCombination(RecordSetCombination combination) {
    Validate.notNull(combination, "Parameter 'combination' is null!");
    this.combination = combination;
  }

  /**
   * @return the order
   */
  public Order getOrder() {
    return order;
  }

  /**
   * @param order
   *          the order to set
   */
  public void setOrder(Order order) {
    this.order = order;
  }

  /**
   * @return the limit
   */
  public Limit getLimit() {
    return limit;
  }

  /**
   * @param limit
   *          the limit to set
   */
  public void setLimit(Limit limit) {
    this.limit = limit;
  }

  public Decorator getDecorator() {
    return decorator;
  }

  public void setDecorator(Decorator decorator) {
    this.decorator = decorator;
  }
}
