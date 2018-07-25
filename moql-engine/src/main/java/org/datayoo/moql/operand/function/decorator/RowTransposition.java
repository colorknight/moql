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

package org.datayoo.moql.operand.function.decorator;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.Operand;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;
import org.datayoo.moql.core.Columns;
import org.datayoo.moql.core.RecordSetImpl;
import org.datayoo.moql.core.RecordSetMetadata;
import org.datayoo.moql.core.group.GroupKey;
import org.datayoo.moql.metadata.ColumnMetadata;
import org.datayoo.moql.util.StringFormater;

import java.util.*;

/**
 * @author Tang Tadin
 */
public class RowTransposition extends DecorateFunction {

  public static final String FUNCTION_NAME = "rowTransposition";

  protected String headerColumn;

  protected int headerColumnIndex;

  protected String[] valueColumns;

  protected int[] valueColumnIndexes;

  protected String valueColumnsName = "META";

  protected String[] groupColumns;

  protected int[] groupColumnIndexes;

  protected String[] transposedColumns;

  protected Map<String, Integer> transposedColumnMap = new HashMap<String, Integer>();

  protected GroupKey tempGroupKey = new GroupKey();

  public RowTransposition(List<Operand> parameters) {
    super(FUNCTION_NAME, parameters.size(), parameters);
    if (parameters.size() == 0) {
      throw new IllegalArgumentException(
          "Invalid function format! The format should be 'rowTranspose(headerColumn[,valueColumns,valueColumnsName,groupColumns])'");
    }
    // TODO Auto-generated constructor stub
    headerColumn = (String) parameters.get(0).operate(null);
    Validate.notEmpty(headerColumn, "rowColumn is empty!");
    if (parameters.size() > 1) {
      String valueColumnsString = (String) parameters.get(1).operate(null);
      if (valueColumnsString != null && valueColumnsString.length() > 0)
        valueColumns = valueColumnsString.split(",");
    }
    if (parameters.size() > 2) {
      String valueColumnsName = (String) parameters.get(2).operate(null);
      if (valueColumnsName != null && valueColumnsName.length() > 0) {
        this.valueColumnsName = valueColumnsName;
      }
    }
    if (parameters.size() > 3) {
      String groupColumnsString = (String) parameters.get(3).operate(null);
      if (groupColumnsString != null && groupColumnsString.length() > 0)
        groupColumns = groupColumnsString.split(",");
    }
    checkInvalidate();
  }

  protected void checkInvalidate() {
    if (isGroupColumn(headerColumn)) {
      throw new IllegalArgumentException(
          "The headerColumn is in groupColumns!");
    }
    if (isValueColumn(headerColumn)) {
      throw new IllegalArgumentException(
          "The headerColumn is in valueColumns!");
    }
    if (groupColumns != null) {
      for (int i = 0; i < groupColumns.length; i++) {
        if (isValueColumn(groupColumns[i]))
          throw new IllegalArgumentException(
              "The headerColumn is in valueColumns!");
      }
    }
  }

  protected boolean isValueColumn(String columnName) {
    if (valueColumns == null)
      return false;
    for (int i = 0; i < valueColumns.length; i++) {
      if (valueColumns[i].equals(columnName))
        return true;
    }
    return false;
  }

  @Override public RecordSet decorate(RecordSet recordSet, Columns columns) {
    // TODO Auto-generated method stub
    if (recordSet.getRecords().size() == 0)
      return null;
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    extractHeaderColumnIndex(recordSetDefinition);
    extractGroupColumnIndexes(recordSetDefinition);
    if (valueColumns == null) {
      extractValueColumns(recordSetDefinition);
    } else {
      extractValueColumnIndexes(recordSetDefinition);
    }
    extractTransposedColumns(recordSet);
    return transpose(recordSet);
  }

  protected RecordSet transpose(RecordSet recordSet) {
    List<TransposedRecord> transRecords = new LinkedList<TransposedRecord>();
    Map<GroupKey, TransposedRecord> cache = new HashMap<GroupKey, TransposedRecord>();
    TransposedRecord transRecord = null;
    for (Object[] record : recordSet.getRecords()) {
      if (groupColumns != null) {
        fillTempGroupKey(record);
        transRecord = cache.get(tempGroupKey);
        if (transRecord == null) {
          GroupKey groupKey = new GroupKey(tempGroupKey.getGroups());
          transRecord = new TransposedRecord(groupKey);
          transRecords.add(transRecord);
          cache.put(groupKey, transRecord);
        }
      } else {
        if (transRecords.size() == 0) {
          transRecord = new TransposedRecord(null);
          transRecords.add(transRecord);
        }
      }
      transRecord.transpose(record);
    }
    return packRecordSet(recordSet.getStart(), recordSet.getEnd(),
        transRecords);
  }

  protected void extractHeaderColumnIndex(
      RecordSetDefinition recordSetDefinition) {
    headerColumnIndex = recordSetDefinition.getColumnIndex(headerColumn);
    if (headerColumnIndex == -1) {
      throw new IllegalArgumentException(StringFormater
          .format("RecordSet has no column named '{}'", headerColumn));
    }
  }

  protected void extractGroupColumnIndexes(
      RecordSetDefinition recordSetDefinition) {
    if (groupColumns == null)
      return;
    groupColumnIndexes = new int[groupColumns.length];
    for (int i = 0; i < groupColumns.length; i++) {
      int index = recordSetDefinition.getColumnIndex(groupColumns[i]);
      if (index == -1)
        throw new IllegalArgumentException(StringFormater
            .format("RecordSet has no column named '{}'", groupColumns[i]));
      groupColumnIndexes[i] = index;
    }
  }

  protected void extractValueColumns(RecordSetDefinition recordSetDefinition) {
    List<String> columns = new ArrayList<String>(
        recordSetDefinition.getColumns().size());
    List<Integer> indexes = new ArrayList<Integer>(columns.size());
    int i = -1;
    for (ColumnDefinition columnDefinition : recordSetDefinition.getColumns()) {
      i++;
      if (columnDefinition.getName().equals(headerColumn))
        continue;
      if (isGroupColumn(columnDefinition.getName()))
        continue;
      columns.add(columnDefinition.getName());
      indexes.add(i);
    }
    valueColumns = new String[columns.size()];
    columns.toArray(valueColumns);
    valueColumnIndexes = new int[indexes.size()];
    for (i = 0; i < valueColumns.length; i++) {
      valueColumnIndexes[i] = indexes.get(i);
    }
  }

  protected void extractValueColumnIndexes(
      RecordSetDefinition recordSetDefinition) {
    valueColumnIndexes = new int[valueColumns.length];
    for (int i = 0; i < valueColumns.length; i++) {
      int index = recordSetDefinition.getColumnIndex(valueColumns[i]);
      if (index == -1)
        throw new IllegalArgumentException(StringFormater
            .format("RecordSet has no column named '{}'", valueColumns[i]));
      valueColumnIndexes[i] = index;
    }
  }

  protected boolean isGroupColumn(String columnName) {
    if (groupColumns == null)
      return false;
    for (int i = 0; i < groupColumns.length; i++) {
      if (groupColumns[i].equals(columnName))
        return true;
    }
    return false;
  }

  protected void extractTransposedColumns(RecordSet recordSet) {
    List<String> values = new LinkedList<String>();
    for (Object[] record : recordSet.getRecords()) {
      Object obj = record[headerColumnIndex];
      String value;
      if (obj == null) {
        value = "NULL";
      } else {
        value = obj.toString();
      }
      if (!values.contains(value))
        values.add(value);
    }
    transposedColumns = new String[values.size()];
    values.toArray(transposedColumns);
    for (int i = 0; i < transposedColumns.length; i++) {
      transposedColumnMap.put(transposedColumns[i], i);
    }
  }

  protected void fillTempGroupKey(Object[] record) {
    Object[] values = new Object[groupColumnIndexes.length];
    for (int i = 0; i < groupColumnIndexes.length; i++) {
      values[i] = record[groupColumnIndexes[i]];
    }
    tempGroupKey.initialize(values);
  }

  protected RecordSet packRecordSet(Date start, Date stop,
      List<TransposedRecord> transRecords) {
    RecordSetMetadata recordSetMetadata = buildTransposedRecordSetMetadata(
        name);
    List<Object[]> records = toRecords(transRecords);
    return new RecordSetImpl(recordSetMetadata, start, stop, records);
  }

  protected RecordSetMetadata buildTransposedRecordSetMetadata(String name) {
    List<ColumnDefinition> columns = new LinkedList<ColumnDefinition>();
    List<ColumnDefinition> groups = new LinkedList<ColumnDefinition>();
    if (groupColumns != null) {
      for (int i = 0; i < groupColumns.length; i++) {
        ColumnDefinition column = new ColumnMetadata(groupColumns[i],
            groupColumns[i]);
        groups.add(column);
      }
    }
    columns.addAll(groups);
    if (valueColumns.length > 1) {
      ColumnDefinition column = new ColumnMetadata(valueColumnsName,
          valueColumnsName);
      columns.add(column);
    }
    for (int i = 0; i < transposedColumns.length; i++) {
      ColumnDefinition column = new ColumnMetadata(transposedColumns[i],
          transposedColumns[i]);
      columns.add(column);
    }
    return new RecordSetMetadata(columns, groups);
  }

  protected List<Object[]> toRecords(List<TransposedRecord> transRecords) {
    List<Object[]> records = new LinkedList<Object[]>();
    for (TransposedRecord transRecord : transRecords) {
      records.addAll(transRecord.toRecords());
    }
    return records;
  }

  protected class TransposedRecord {
    protected GroupKey groupKey;
    protected Object[][] records = new Object[valueColumns.length][];

    public TransposedRecord(GroupKey groupKey) {
      this.groupKey = groupKey;
      for (int i = 0; i < valueColumns.length; i++) {
        if (valueColumns.length > 1) {
          records[i] = new Object[transposedColumns.length + 1];
          records[i][0] = valueColumns[i];
        } else {
          records[i] = new Object[transposedColumns.length];
        }
      }
    }

    public void transpose(Object[] record) {
      String transposedColumn = record[headerColumnIndex].toString();
      int transposedColumnIndex = transposedColumnMap.get(transposedColumn);
      int offset = valueColumnIndexes.length > 1 ? 1 : 0;
      for (int i = 0; i < valueColumnIndexes.length; i++) {
        records[i][transposedColumnIndex
            + offset] = record[valueColumnIndexes[i]];
      }
    }

    public List<Object[]> toRecords() {
      List<Object[]> recordList = new ArrayList<Object[]>(records.length);
      int recordSize = records[0].length;
      if (groupKey != null) {
        recordSize += groupKey.getGroups().length;
      }
      int offset = 0;
      for (int i = 0; i < records.length; i++) {
        Object[] record = new Object[recordSize];
        if (groupKey != null) {
          System.arraycopy(groupKey.getGroups(), 0, record, 0,
              groupKey.getGroups().length);
          offset = groupKey.getGroups().length;
        }
        System.arraycopy(records[i], 0, record, offset, records[i].length);
        recordList.add(record);
      }
      return recordList;
    }

  }
}
