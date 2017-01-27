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
import org.moql.metadata.SelectorMetadata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * 
 * @author Tang Tadin
 *
 */
public class SelectorImpl implements Selector {
  
  protected String alias;
	
	protected SelectorMetadata selectorMetadata;
	
	protected RecordSetOperator recordSetOperator;
	
	protected Tables tables;
	
	protected Condition where;
	
	protected Having having;
	
	protected Order order;
	
	protected Limit limit;
	
	protected Decorator decorator;
	
	protected List<Selector> nestedTableSelectors = new LinkedList<Selector>();
	
	protected List<Selector> nestedColumnSelectors = new LinkedList<Selector>();
	
	protected SelectorContext selectorContext;
	
	protected RecordSet recentRecordSet;
	
	public SelectorImpl(SelectorMetadata selectorMetadata) {
		Validate.notNull(selectorMetadata, "Parameter 'selectorMetadata' is null!");
		this.selectorMetadata = selectorMetadata;
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
		RecordSet recordSet = recordSetOperator.getValue();
		if (having != null) {
			recordSet = having.decorate(recordSet, recordSetOperator.getColumns());
		}
		if (order != null) {
			recordSet = order.decorate(recordSet, recordSetOperator.getColumns());
		}
		if (limit != null) {
			recordSet = limit.decorate(recordSet, recordSetOperator.getColumns());
		}
		if (decorator != null) {
			recordSet = decorator.decorate(recordSet, recordSetOperator.getColumns());
		}
		recentRecordSet = recordSet;
	}
	
	protected void fillTableSelectors(DataSetMap dataSetMap) {
		for(Selector selector : nestedTableSelectors) {
			dataSetMap.putDataSet(selector.getAlias(), selector.getRecordSet());
		}
	}

	@Override
	public SelectorContext getSelectorContext() {
		// TODO Auto-generated method stub
		return selectorContext;
	}

	@Override
	public SelectorDefinition getSelectorDefinition() {
		// TODO Auto-generated method stub
		return selectorMetadata;
	}

	@Override
	public synchronized void clear() {
		// TODO Auto-generated method stub
		recentRecordSet = null;
		recordSetOperator.clear();
		for(Selector selector : nestedTableSelectors) {
			selector.clear();
		}
		for(Selector selector : nestedColumnSelectors) {
			selector.clear();
		}
	}

	@Override
	public synchronized void select(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		selectByNestedSelectors(dataSetMap);
		if (nestedTableSelectors.size() != 0) {
			dataSetMap = new DataSetMapImpl(dataSetMap);
			fillTableSelectors(dataSetMap);
		}
		innerSelect(dataSetMap);
	}
	
	protected void selectByNestedSelectors(DataSetMap dataSetMap) {
		for(Selector selector : nestedTableSelectors) {
			selector.select(dataSetMap);
		}
		for(Selector selector : nestedColumnSelectors) {
			selector.select(dataSetMap);
		}
	}
	
	protected void innerSelect(DataSetMap dataSetMap) {
		tables.bind(dataSetMap);
		for(Iterator<EntityMap> it = tables.iterator(); it.hasNext();) {
			EntityMap entityMap = it.next();
			if (where != null) {
				if (!where.isMatch(entityMap)) {
					continue;
				}
			}
			recordSetOperator.operate(entityMap);
			recentRecordSet = null;
		}
	}
	
	@Override
	public void setSelectorContext(SelectorContext context) {
		// TODO Auto-generated method stub
		Validate.notNull(context, "Parameter 'context' is null!");
		this.selectorContext = context;
	}

	/**
	 * @return the limit
	 */
	public Limit getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	/**
	 * @return the where
	 */
	public Condition getWhere() {
		return where;
	}

	/**
	 * @param where the where to set
	 */
	public void setWhere(Condition where) {
		this.where = where;
	}

	/**
	 * @return the having
	 */
	public Having getHaving() {
		return having;
	}

	/**
	 * @param having the having to set
	 */
	public void setHaving(Having having) {
		this.having = having;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * @return the nestedTableSelectors
	 */
	public List<Selector> getNestedTableSelectors() {
		return nestedTableSelectors;
	}

	public void setNestedTableSelectors(List<Selector> nestedTableSelectors) {
		if (nestedTableSelectors == null)
			nestedTableSelectors = new LinkedList<Selector>();
		this.nestedTableSelectors = nestedTableSelectors;
	}

	/**
	 * @return the nestedColumnSelectors
	 */
	public List<Selector> getNestedColumnSelectors() {
		return nestedColumnSelectors;
	}

	/**
	 * @param nestedColumnSelectors the nestedColumnSelectors to set
	 */
	public void setNestedColumnSelectors(List<Selector> nestedColumnSelectors) {
		if (nestedColumnSelectors == null)
			nestedColumnSelectors = new LinkedList<Selector>();
		this.nestedColumnSelectors = nestedColumnSelectors;
	}

	/**
	 * @return the recordSetOperator
	 */
	public RecordSetOperator getRecordSetOperator() {
		return recordSetOperator;
	}

	public void setRecordSetOperator(RecordSetOperator recordSetOperator) {
		Validate.notNull(recordSetOperator, "Parameter 'recordSetOperator' is null!");
		this.recordSetOperator = recordSetOperator;
	}

	/**
	 * @return the tables
	 */
	public Tables getTables() {
		return tables;
	}
	
	public void setTables(Tables tables) {
		Validate.notNull(tables, "Parameter 'tables' is null!");
		this.tables = tables;
	}

	public Decorator getDecorator() {
		return decorator;
	}

	public void setDecorator(Decorator decorator) {
		this.decorator = decorator;
	}

}

