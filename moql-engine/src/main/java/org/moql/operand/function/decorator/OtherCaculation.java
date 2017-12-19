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
package org.moql.operand.function.decorator;

import org.apache.commons.lang.ObjectUtils;
import org.moql.*;
import org.moql.core.Column;
import org.moql.core.Columns;
import org.moql.core.RecordSetImpl;
import org.moql.operand.function.AggregationFunction;
import org.moql.operand.function.Count;
import org.moql.operand.function.Sum;
import org.moql.operand.function.factory.FunctionFactory;
import org.moql.operand.function.factory.FunctionFactoryImpl;
import org.moql.operand.variable.SingleVariable;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Tang Tadin
 */
public class OtherCaculation extends DecorateFunction {

  public static final String FUNCTION_NAME = "otherCaculation";

  protected OtherAlias[] otherAliases;

  protected int[] groupIndexes;

  protected OtherRecord[] otherRecords;

  protected FunctionFactory functionFactory;

  protected boolean firstTime = true;

  public OtherCaculation(List<Operand> parameters) {
    super(FUNCTION_NAME, parameters.size(), parameters);
    // TODO Auto-generated constructor stub
    initializeOtherAlias();
    groupIndexes = new int[otherAliases.length];
    otherRecords = new OtherRecord[otherAliases.length];
    functionFactory = FunctionFactoryImpl.createFunctionFactory();
  }

  protected void initializeOtherAlias() {
    otherAliases = new OtherAlias[parameters.size()];
    int i = 0;
    OtherAlias lastOtherAlias = null;
    for (Operand parameter : parameters) {
      Object obj = parameter.operate(null);
      String value = null;
      if (obj != null) {
        value = obj.toString();
      }
      if (value != null) {
        String[] kv = value.split(":");
        if (kv.length != 2) {
          throw new IllegalArgumentException(
              "The parameter's format should be 'alias:limit'!");
        }
        kv[0] = kv[0].trim();
        kv[1] = kv[1].trim();
        if (kv[0].length() == 0 || kv[1].length() == 0) {
          throw new IllegalArgumentException(
              "The parameter's format should be 'alias:limit'!");
        }
        otherAliases[i] = new OtherAlias(kv[0], Integer.valueOf(kv[1]));
        lastOtherAlias = otherAliases[i];
      }
      i++;
    }
    if (lastOtherAlias == null) {
      throw new IllegalArgumentException("Other alias is empty!");
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
    initializeOtherRecords(recordSetDefinition, columns);
  }

  protected void initializeGroupIndexes(RecordSetDefinition recordSetDefinition) {
    List<ColumnDefinition> columnDefinitions = recordSetDefinition
        .getGroupColumns();
    if (columnDefinitions.size() == 0 && otherAliases.length != 1) {
      throw new IllegalArgumentException(
          "The count of aliases should be one when the record set has no group!");
    } else if (columnDefinitions.size() != otherAliases.length)
      throw new IllegalArgumentException(
          "The count of aliases and the columnes in group caculation is not match!");
    int i = 0;
    for (ColumnDefinition column : columnDefinitions) {
      groupIndexes[i++] = recordSetDefinition.getColumnIndex(column.getName());
    }
  }

  protected void initializeOtherRecords(
      RecordSetDefinition recordSetDefinition, Columns columns) {
    for (int i = 0; i < otherRecords.length; i++) {
      if (otherAliases[i] != null) {
        otherRecords[i] = new OtherRecord(i + 1, columns.getColumns().size(),
            otherAliases[i].limit);
        otherRecords[i].initializeOperands(recordSetDefinition, columns);
      }
    }
  }

  protected RecordSet caculate(RecordSet recordSet) {
    List<Object[]> records = new LinkedList<Object[]>();
    int index = 0;
    for (Object[] record : recordSet.getRecords()) {
      if (!firstTime) {
        index = whichOtherRecordGroupChanged(record);
        if (index + 1 < otherRecords.length) {
          buildRecords(index + 1, records);
        }
        intitializeOtherRecordGroups(index, record);
      } else {
        intitializeOtherRecordGroups(0, record);
        firstTime = false;
      }
      if (!caculate(record, recordSet))
        records.add(record);
    }
    buildRecords(0, records);
    return new RecordSetImpl(recordSet.getRecordSetDefinition(),
        recordSet.getStart(), recordSet.getEnd(), records);
  }

  protected void intitializeOtherRecordGroups(int startIndex, Object[] record) {
    for (int i = startIndex; i < otherRecords.length; i++) {
      if (otherRecords[i] != null) {
        otherRecords[i].initializeGroups(record);
      }
    }
  }

  protected int whichOtherRecordGroupChanged(Object[] record) {
    for (int i = 0; i < otherRecords.length; i++) {
      if (otherRecords[i] != null) {
        if (otherRecords[i].isGroupChanged(record))
          return i;
      }
    }
    return 0;
  }

  protected boolean caculate(Object[] record, RecordSet recordSet) {
    EntityMap entityMap = new EntityMapImpl(recordSet.toMap(record));
    for (int i = 0; i < otherRecords.length; i++) {
      if (otherRecords[i] != null) {
        if (otherRecords[i].caculate(entityMap))
          return true;
      }
    }
    return false;
  }

  protected void buildRecords(int index, List<Object[]> records) {
    for (int i = otherRecords.length - 1; i >= index; i--) {
      if (otherRecords[i] != null) {
        Object[] record = otherRecords[i].buildRecordAndClear();
        records.add(record);
      }
    }
  }

  protected class OtherAlias {

    protected String alias;

    protected int limit;

    public OtherAlias(String alias, int limit) {
      this.alias = alias;
      this.limit = limit;
    }
  }

  protected class OtherRecord {

    protected Object[] groups;

    protected Operand[] operands;

    protected int limit = 0;

    protected int offset = 0;

    public OtherRecord(int groupSize, int operandSize, int limit) {
      groups = new Object[groupSize];
      operands = new Operand[operandSize];
      this.limit = limit;
    }

    public void initializeGroups(Object[] record) {
      for (int i = 0; i < groups.length; i++) {
        groups[i] = record[groupIndexes[i]];
      }
      offset++;
    }

    public boolean isGroupChanged(Object[] record) {
      for (int i = 0; i < groups.length; i++) {
        if (!ObjectUtils.equals(groups[i], record[groupIndexes[i]]))
          return true;
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

    public boolean caculate(EntityMap entityMap) {
      if (offset <= limit)
        return false;
      for (int i = 0; i < operands.length; i++) {
        if (operands[i] != null)
          operands[i].operate(entityMap);
      }
      return true;
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
        for (; i < groups.length-1; i++) {
          record[groupIndexes[i]] = groups[i];
        }
      }
      if (i < otherAliases.length) {
        record[groupIndexes[i]] = otherAliases[i].alias;
        i++;
      }
      for (; i < otherAliases.length; i++) {
        record[groupIndexes[i]] = "-";
      }
    }
  }

}
