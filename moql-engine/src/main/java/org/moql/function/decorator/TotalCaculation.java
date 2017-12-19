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
package org.moql.function.decorator;

import org.apache.commons.lang.ObjectUtils;
import org.moql.*;
import org.moql.core.Column;
import org.moql.core.Columns;
import org.moql.core.RecordSetImpl;
import org.moql.operand.function.AggregationFunction;
import org.moql.operand.function.Count;
import org.moql.operand.function.Sum;
import org.moql.operand.function.decorator.DecorateFunction;
import org.moql.operand.function.factory.FunctionFactory;
import org.moql.operand.function.factory.FunctionFactoryImpl;
import org.moql.operand.variable.SingleVariable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tang Tadin
 */
public class TotalCaculation extends DecorateFunction {

  public static final String FUNCTION_NAME = "totalCaculation";

  protected String[] totalAliases;

  protected int[] groupIndexes;

  protected TotalRecord[] totalRecords;

  protected FunctionFactory functionFactory;

  protected boolean firstTime = true;

  public TotalCaculation(List<Operand> parameters) {
    super(FUNCTION_NAME, parameters.size(), parameters);
    // TODO Auto-generated constructor stub
    initializeTotalAlias();
    groupIndexes = new int[totalAliases.length];
    totalRecords = new TotalRecord[totalAliases.length];
    functionFactory = FunctionFactoryImpl.createFunctionFactory();
  }

  protected void initializeTotalAlias() {
    totalAliases = new String[parameters.size()];
    int i = 0;
    int length = 0;
    for (Operand parameter : parameters) {
      Object obj = parameter.operate(null);
      if (obj != null)
        totalAliases[i] = obj.toString();
      if (totalAliases[i] != null) {
        totalAliases[i].trim();
        if (totalAliases[i].length() == 0) {
          throw new IllegalArgumentException("Parameter couldn't be empty!");
        }
        if (totalAliases[i].length() > length)
          length = totalAliases[i].length();
      }
      i++;
    }
    if (length == 0) {
      throw new IllegalArgumentException("Total alias is empty!");
    }

  }

  @Override
  public RecordSet decorate(RecordSet recordSet, Columns columns) {
    // TODO Auto-generated method stub
    if (recordSet == null)
      return null;
    initialize(recordSet, columns);
    return caculate(recordSet);
  }

  protected void initialize(RecordSet recordSet, Columns columns) {
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    initializeGroupIndexes(recordSetDefinition);
    initializeTotalRecords(recordSetDefinition, columns);
  }

  protected void initializeGroupIndexes(RecordSetDefinition recordSetDefinition) {
    List<ColumnDefinition> columnDefinitions = recordSetDefinition
        .getGroupColumns();
    if (columnDefinitions.size() == 0 && totalAliases.length != 1) {
      throw new IllegalArgumentException(
          "The count of aliases should be one when the record set has no group!");
    } else if (columnDefinitions.size() != totalAliases.length)
      throw new IllegalArgumentException(
          "The count of aliases and the columnes in group caculation is not match!");
    int i = 0;
    for (ColumnDefinition column : columnDefinitions) {
      groupIndexes[i++] = recordSetDefinition.getColumnIndex(column.getName());
    }
  }

  protected void initializeTotalRecords(
      RecordSetDefinition recordSetDefinition, Columns columns) {
    for (int i = 0; i < totalRecords.length; i++) {
      if (totalAliases[i] != null) {
        totalRecords[i] = new TotalRecord(i, columns.getColumns().size());
        totalRecords[i].initializeOperands(recordSetDefinition, columns);
      }
    }
  }

  protected RecordSet caculate(RecordSet recordSet) {
    List<Object[]> records = new LinkedList<Object[]>();
    int index = 0;
    for (Object[] record : recordSet.getRecords()) {
      if (!firstTime) {
        index = whichTotalRecordGroupChanged(record);
        if (index != 0) {
          buildRecords(index, records);
        }
        intitializeTotalRecordGroups(index, record);
      } else {
        intitializeTotalRecordGroups(1, record);
        firstTime = false;
      }
      caculate(record, recordSet);
      records.add(record);
    }
    buildRecords(0, records);
    return new RecordSetImpl(recordSet.getRecordSetDefinition(),
        recordSet.getStart(), recordSet.getEnd(), records);
  }

  protected void intitializeTotalRecordGroups(int startIndex, Object[] record) {
    startIndex = startIndex > 0 ? startIndex : 1;
    for (int i = startIndex; i < totalRecords.length; i++) {
      if (totalRecords[i] != null) {
        totalRecords[i].initializeGroups(record);
      }
    }
  }

  protected int whichTotalRecordGroupChanged(Object[] record) {
    for (int i = 1; i < totalRecords.length; i++) {
      if (totalRecords[i] != null) {
        if (totalRecords[i].isGroupChanged(record))
          return i;
      }
    }
    return 0;
  }

  protected void caculate(Object[] record, RecordSet recordSet) {
    EntityMap entityMap = new EntityMapImpl(recordSet.toMap(record));
    for (int i = 0; i < totalRecords.length; i++) {
      if (totalRecords[i] != null) {
        totalRecords[i].caculate(entityMap);
      }
    }
  }

  protected void buildRecords(int index, List<Object[]> records) {
    for (int i = totalRecords.length - 1; i >= index; i--) {
      if (totalRecords[i] != null) {
        Object[] record = totalRecords[i].buildRecordAndClear();
        records.add(record);
      }
    }
  }

  protected class TotalRecord {

    protected Object[] groups;

    protected Operand[] operands;

    public TotalRecord(int groupSize, int operandSize) {
      if (groupSize != 0)
        groups = new Object[groupSize];
      operands = new Operand[operandSize];
    }

    public void initializeGroups(Object[] record) {
      if (groups != null) {
        for (int i = 0; i < groups.length; i++) {
          groups[i] = record[groupIndexes[i]];
        }
      }
    }

    public boolean isGroupChanged(Object[] record) {
      if (groups != null) {
        for (int i = 0; i < groups.length; i++) {
          if (!ObjectUtils.equals(groups[i], record[groupIndexes[i]]))
            return true;
        }
      }
      return false;
    }

    public void initializeOperands(RecordSetDefinition recordSetDefinition,
        Columns columns) {
      int i = 0;
      for (Column column : columns.getColumns()) {
        String columnName = column.getColumnMetadata().getName();
        if (!recordSetDefinition.isGroupColumn(columnName)) {
          operands[i++] = createOperand(columnName, column.getOperand());
        } else {
          operands[i++] = null;
        }
      }
    }

    protected Operand createOperand(String name, Operand operand) {
      if (!(operand instanceof AggregationFunction)) {
        return null;
      }
      AggregationFunction function = (AggregationFunction) operand;
      SingleVariable parameter = new SingleVariable(name);
      List<Operand> parameters = new LinkedList<Operand>();
      parameters.add(parameter);
      String functionName = function.getName();
      if (functionName.equals(Count.FUNCTION_NAME))
        functionName = Sum.FUNCTION_NAME;
      return functionFactory.createFunction(functionName, parameters);
    }

    public void caculate(EntityMap entityMap) {
      for (int i = 0; i < operands.length; i++) {
        if (operands[i] != null)
          operands[i].operate(entityMap);
      }
    }

    public Object[] buildRecordAndClear() {
      Object[] record = new Object[operands.length];
      for (int i = 0; i < operands.length; i++) {
        if (operands[i] != null) {
          record[i] = operands[i].getValue();
          operands[i].clear();
        }
      }
      fillGroups(record);
      return record;
    }

    protected void fillGroups(Object[] record) {
      int i = 0;
      if (groups != null) {
        for (; i < groups.length; i++) {
          record[groupIndexes[i]] = groups[i];
        }
      }
      if (i < totalAliases.length) {
        record[groupIndexes[i]] = totalAliases[i];
        i++;
      }
      for (; i < totalAliases.length; i++) {
        record[groupIndexes[i]] = "-";
      }
    }
  }

}
