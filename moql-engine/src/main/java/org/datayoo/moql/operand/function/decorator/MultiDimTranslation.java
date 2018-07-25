package org.datayoo.moql.operand.function.decorator;

import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.Operand;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;
import org.datayoo.moql.core.Column;
import org.datayoo.moql.core.Columns;
import org.datayoo.moql.core.RecordSetImpl;
import org.datayoo.moql.core.RecordSetMetadata;
import org.datayoo.moql.metadata.ColumnMetadata;
import org.datayoo.moql.util.StringFormater;

import java.util.*;

public class MultiDimTranslation extends DecorateFunction {

  public static final String FUNCTION_NAME = "multiDimTranslation";

  public static final String COLUMN_YAXIS = "YAXIS";

  protected boolean hasGroup = false;

  protected String[] xAxises;

  protected int[] xAxisIndexes;

  protected String[] yAxises;

  protected int[] yAxisIndexes;

  protected String[] measures;

  protected String[] measureMethods;

  protected int[] measureIndexes;

  public MultiDimTranslation(List<Operand> parameters) {
    super(FUNCTION_NAME, parameters);
    if (parameters.size() != 3) {
      throw new IllegalArgumentException(
          "Invalid function format! The format should be 'multiDimTranslation(xAxisColumns, yAxisColumns, measureColumns)'");
    }
    String parameter = (String) parameters.get(0).operate(null);
    if (parameter != null) {
      xAxises = parameter.split(",");
      trim(xAxises);
    }
    parameter = (String) parameters.get(1).operate(null);
    if (parameter != null) {
      yAxises = parameter.split(",");
      trim(yAxises);
    }
    parameter = (String) parameters.get(2).operate(null);
    if (parameter == null || parameter.isEmpty())
      throw new IllegalArgumentException("measureColumns are empty!");
    measures = parameter.split(",");
    trim(measures);
    if (xAxises == null && yAxises == null)
      throw new IllegalArgumentException(
          "xAxisColumns and yAxisColumns are both empty!");
    nameConflictValidate();
  }

  protected void trim(String[] columns) {
    if (columns == null)
      return;
    for (int i = 0; i < columns.length; i++) {
      columns[i] = columns[i].trim();
    }
  }

  protected void nameConflictValidate() {
    Set<String> columnNameSet = new HashSet<String>();
    if (xAxises != null)
      fillNameSet(xAxises, columnNameSet);
    if (yAxises != null)
      fillNameSet(yAxises, columnNameSet);
    fillNameSet(measures, columnNameSet);
  }

  protected void fillNameSet(String[] columnNames, Set<String> columnNameSet) {
    for (int i = 0; i < columnNames.length; i++) {
      if (!columnNameSet.add(columnNames[i]))
        throw new IllegalArgumentException(StringFormater
            .format("Column named '{}' duplicated!", columnNames[i]));
    }
  }

  @Override public RecordSet decorate(RecordSet recordSet, Columns columns) {
    initializeColumnIndexes(recordSet, columns);
    RecordSetMetadata recordSetMetadata = buildRecordSetMetadata(recordSet);
    return buildRecordSet(recordSet, recordSetMetadata);
  }

  protected void initializeColumnIndexes(RecordSet recordSet, Columns columns) {
    checkGroup(recordSet);
    initializeXAxisColumnIndexes(recordSet);
    initializeYAxisColumnIndexes(recordSet);
    initializeMeasureColumnIndexes(recordSet, columns);
  }

  protected void checkGroup(RecordSet recordSet) {
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    if (recordSetDefinition.getGroupColumns().size() != 0)
      hasGroup = true;
    else
      hasGroup = false;
  }

  protected void initializeXAxisColumnIndexes(RecordSet recordSet) {
    if (xAxises == null)
      return;
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    xAxisIndexes = new int[xAxises.length];
    for (int i = 0; i < xAxises.length; i++) {
      xAxisIndexes[i] = recordSetDefinition.getColumnIndex(xAxises[i]);
      if (xAxisIndexes[i] == -1)
        throw new IllegalArgumentException(StringFormater
            .format("There is no column named '{}' in record set!",
                xAxises[i]));
      if (hasGroup && !recordSetDefinition.isGroupColumn(xAxises[i]))
        throw new IllegalArgumentException(StringFormater
            .format("Column named '{}' isn't a group column!", xAxises[i]));
    }
  }

  protected void initializeYAxisColumnIndexes(RecordSet recordSet) {
    if (yAxises == null)
      return;
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    yAxisIndexes = new int[yAxises.length];
    for (int i = 0; i < yAxises.length; i++) {
      yAxisIndexes[i] = recordSetDefinition.getColumnIndex(yAxises[i]);
      if (yAxisIndexes[i] == -1)
        throw new IllegalArgumentException(StringFormater
            .format("There is no column named '{}' in record set!",
                yAxises[i]));
      if (hasGroup && !recordSetDefinition.isGroupColumn(yAxises[i]))
        throw new IllegalArgumentException(StringFormater
            .format("Column named '{}' isn't a group column!", yAxises[i]));
    }
  }

  protected void initializeMeasureColumnIndexes(RecordSet recordSet,
      Columns columns) {
    if (measures == null)
      return;
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    measureMethods = new String[measures.length];
    measureIndexes = new int[measures.length];
    for (int i = 0; i < measures.length; i++) {
      measureIndexes[i] = recordSetDefinition.getColumnIndex(measures[i]);
      if (measureIndexes[i] == -1)
        throw new IllegalArgumentException(StringFormater
            .format("There is no column named '{}' in record set!",
                measures[i]));
      if (hasGroup) {
        if (recordSetDefinition.isGroupColumn(measures[i])) {
          throw new IllegalArgumentException(StringFormater
              .format("Column named '{}' is a group column!", measures[i]));
        } else {
          Column column = columns.getColumns().get(measureIndexes[i]);
          measureMethods[i] = column.getOperand().getName();
        }
      }
    }
  }

  protected RecordSetMetadata buildRecordSetMetadata(RecordSet recordSet) {
    List<ColumnDefinition> columns = new LinkedList<ColumnDefinition>();
    Set<String> nameSet = new HashSet<String>();
    ColumnMetadata column = new ColumnMetadata(COLUMN_YAXIS, COLUMN_YAXIS);
    columns.add(column);
    for (int i = 0; i < recordSet.getRecordsCount(); i++) {
      String xAxis = buildAxisColumnName(xAxisIndexes, recordSet.getRecord(i));
      String[] measureNames = buildMeasureColumnNames(xAxis);
      for (int j = 0; j < measureNames.length; j++) {
        if (!nameSet.add(measureNames[j])) {
          if (yAxises == null) {
            throw new IllegalArgumentException(StringFormater
                .format("Measure column named '{}' duplicated!",
                    measureNames[i]));
          }
        } else {
          column = new ColumnMetadata(measureNames[j], measureNames[j]);
          columns.add(column);
        }
      }
      if (xAxises == null)
        break;
    }
    return new RecordSetMetadata(columns, null);
  }

  protected RecordSet buildRecordSet(RecordSet recordSet,
      RecordSetMetadata recordSetMetadata) {
    Map<String, Integer> xAxisPosMap = buildXAxisPosMap(recordSetMetadata);
    Map<String, Integer> yAxisPosMap = new HashMap<String, Integer>();
    List<Object[]> records = new LinkedList<Object[]>();
    int yIndex = 0;
    Object[] record;
    Object[] srcRecord;
    for (int i = 0; i < recordSet.getRecordsCount(); i++) {
      srcRecord = recordSet.getRecord(i);
      String yAxisColumnName = buildAxisColumnName(yAxisIndexes, srcRecord);
      Integer pos = yAxisPosMap.get(yAxisColumnName);
      if (pos == null) {
        record = new Object[xAxisPosMap.size()];
        record[0] = yAxisColumnName;
        records.add(record);
        yAxisPosMap.put(yAxisColumnName, yIndex++);
      } else {
        record = records.get(pos);
      }
      fillMeasures(record, xAxisPosMap, srcRecord);
    }
    return new RecordSetImpl(recordSetMetadata, recordSet.getStart(),
        recordSet.getEnd(), records);
  }

  protected String buildAxisColumnName(int[] axisIndexes, Object[] record) {
    if (axisIndexes == null)
      return null;
    StringBuffer sbuf = new StringBuffer();
    for (int i = 0; i < axisIndexes.length; i++) {
      if (i != 0)
        sbuf.append('.');
      sbuf.append(record[axisIndexes[i]]);
    }
    return sbuf.toString();
  }

  protected String[] buildMeasureColumnNames(String axisColumnName) {
    String[] names = new String[measures.length];
    for (int i = 0; i < names.length; i++) {
      if (axisColumnName != null) {
        names[i] = StringFormater
            .format("{}.[{}:{}]", axisColumnName, measures[i],
                measureMethods[i] == null ? "" : measureMethods[i]);
      } else {
        names[i] = StringFormater.format("[{}:{}]", measures[i],
            measureMethods[i] == null ? "" : measureMethods[i]);
      }
    }
    return names;
  }

  protected Map<String, Integer> buildXAxisPosMap(
      RecordSetMetadata recordSetMetadata) {
    Map<String, Integer> xAxisPosMap = new HashMap<String, Integer>();
    List<ColumnDefinition> columns = recordSetMetadata.getColumns();
    int i = 0;
    for (ColumnDefinition columnDefinition : columns) {
      xAxisPosMap.put(columnDefinition.getName(), i++);
    }
    return xAxisPosMap;
  }

  protected void fillMeasures(Object[] targetRecord,
      Map<String, Integer> xAxisPosMap, Object[] sourceRecord) {
    String xAxisColumnName = buildAxisColumnName(xAxisIndexes, sourceRecord);
    String[] measureColumnNames = buildMeasureColumnNames(xAxisColumnName);

    for (int i = 0; i < measureColumnNames.length; i++) {
      Integer pos = xAxisPosMap.get(measureColumnNames[i]);
      if (pos == null)
        throw new IllegalArgumentException(StringFormater
            .format("There is no column named '{}'", measureColumnNames[i]));
      targetRecord[pos] = sourceRecord[measureIndexes[i]];
    }
  }

}
