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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.moql.*;
import org.moql.core.cache.CacheImpl;
import org.moql.metadata.CacheMetadata;
import org.moql.operand.function.AggregationFunction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class ColumnsRecordSetOperator implements RecordSetOperator {

  protected Cache<ColumnRecord, Object[]> cache;

  protected Columns columns;

  protected boolean distinct = false;

  protected boolean aggregation = false;

  protected Date start = new Date();

  public ColumnsRecordSetOperator(CacheMetadata cacheMetadata, Columns columns)
      throws MoqlGrammarException {
    Validate.notNull(cacheMetadata, "Parameter 'cacheMetadata' is null!");
    Validate.notNull(columns, "Parameter 'columns' is null!");
    this.cache = new CacheImpl<ColumnRecord, Object[]>(cacheMetadata);
    this.columns = columns;
    this.distinct = columns.getColumnsMetadata().isDistinct();
    checkColumns();
  }

  protected void checkColumns() throws MoqlGrammarException {
    boolean hasNonAggregationFunction = false;
    boolean hasAggregationFunction = false;
    for (Column column : columns.getColumns()) {
      if (column.getOperand() instanceof AggregationFunction) {
        hasAggregationFunction = true;
      } else {
        hasNonAggregationFunction = true;
      }
    }
    if (hasNonAggregationFunction && hasAggregationFunction) {
      throw new MoqlGrammarException("The select clause without group clause!");
    }
    if (hasAggregationFunction)
      this.aggregation = true;
  }

  @Override
  @SuppressWarnings({
    "rawtypes"
  })
  public Cache getCache() {
    // TODO Auto-generated method stub
    return cache;
  }

  @Override
  public Columns getColumns() {
    // TODO Auto-generated method stub
    return columns;
  }

  @Override
  public RecordSet getValue() {
    // TODO Auto-generated method stub
    RecordSetDefinition recordSetDefinition = createRecordSetDefinition();
    if (!aggregation) {
      return new RecordSetImpl(recordSetDefinition, start, new Date(),
          cache.values());
    } else {
      Object[] record = columns.getValue();
      List<Object[]> records = new ArrayList<Object[]>();
      records.add(record);
      return new RecordSetImpl(recordSetDefinition, start, new Date(), records);
    }
  }

  protected RecordSetDefinition createRecordSetDefinition() {
    List<ColumnDefinition> columnDefinitions = new ArrayList<ColumnDefinition>();
    for (Column column : columns.getColumns()) {
      columnDefinitions.add(column.getColumnMetadata());
    }
    return new RecordSetMetadata(columnDefinitions, null);
  }

  @Override
  public void operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    columns.operate(entityMap);
    if (!aggregation) {
      Object[] record = columns.getValue();
      ColumnRecord columnRecord = new ColumnRecord(record);
      if (distinct) {
        if (cache.get(columnRecord) != null)
          return;
      }
      cache.put(columnRecord, record);
    }
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    cache.clear();
    columns.clear();
    start = new Date();
  }

  protected class ColumnRecord {
    protected Object[] record;
    protected int hashCode = 0;

    public ColumnRecord(Object[] record) {
      this.record = record;
      for (int i = 0; i < record.length; i++) {
        if (record[i] != null) {
          hashCode += record[i].hashCode();
        }
      }
    }

    @Override
    public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      if (!(obj instanceof ColumnRecord))
        return false;
      return ArrayUtils.isEquals(record, ((ColumnRecord) obj).record);
    }

    @Override
    public int hashCode() {
      // TODO Auto-generated method stub
      return hashCode;
    }
  }

}
