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
package org.datayoo.moql.data;

import org.apache.commons.lang.Validate;

import java.util.*;

/**
 * @author Tang Tadin
 */
public abstract class DataPlanningUtils {

  public static List<ColumnFeature> analyzeColumns(List<Object[]> recordSet,
      String[] columnHeaders, FeatureDecisionor featureDecisionor) {
    Validate.notEmpty(recordSet, "recordSet is empty!");
    if (featureDecisionor == null)
      featureDecisionor = new GeneralFeatureDecisionor();
    ColumnFeature[] columns = createColumnFeatures(recordSet.get(0),
        columnHeaders, featureDecisionor);
    caculate(recordSet, columns);
    return Arrays.asList(columns);
  }

  protected static ColumnFeature[] createColumnFeatures(Object[] record,
      String[] columnHeaders, FeatureDecisionor featureDecisionor) {
    ColumnFeature[] columns = new ColumnFeature[record.length];
    if (columnHeaders != null) {
      Validate.isTrue(record.length == columnHeaders.length,
          "columnHeaders is invalid!");
      for (int i = 0; i < record.length; i++) {
        columns[i] = new ColumnFeature(columnHeaders[i], featureDecisionor);
      }
    } else {
      for (int i = 0; i < record.length; i++) {
        columns[i] = new ColumnFeature(String.valueOf(i), featureDecisionor);
      }
    }
    return columns;
  }
  
  protected static void caculate(List<Object[]> recordSet, ColumnFeature[] columns) {
    for(Object[] record : recordSet) {
      for(int i = 0; i < record.length; i++) {
        columns[i].addValue(record[i]);
      }
    }
  }

  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  public static List<ColumnFeature> analyzeColumns(
      List<Map<String, Object>> recordSet, FeatureDecisionor featureDecisionor) {
    Validate.notEmpty(recordSet, "recordSet is empty!");
    if (featureDecisionor == null)
      featureDecisionor = new GeneralFeatureDecisionor();
    Map<String, ColumnFeature> columnFeatureMap = createColumnFeatures(recordSet.get(0),
        featureDecisionor);
    caculate(recordSet, columnFeatureMap);
    return new ArrayList(columnFeatureMap.values());
  }
  
  protected static Map<String, ColumnFeature> createColumnFeatures(Map<String, Object> record,
      FeatureDecisionor featureDecisionor) {
    Map<String, ColumnFeature> columnFeatureMap = new HashMap<String, ColumnFeature>();
    for(String columnName : record.keySet()) {
      ColumnFeature columnFeature = new ColumnFeature(columnName, featureDecisionor);
      columnFeatureMap.put(columnName, columnFeature);
    }
    return columnFeatureMap;
  }
  
  protected static void caculate(List<Map<String, Object>> recordSet, Map<String, ColumnFeature> columnFeatureMap) {
    for(Map<String, Object> record : recordSet) {
      for(Map.Entry<String, Object> entry : record.entrySet()) {
        ColumnFeature columnFeature = columnFeatureMap.get(entry.getKey());
        if (columnFeature != null) {
          columnFeature.addValue(entry.getValue());
        }
      }
    }
  }
  
  public static List<ColumnFeature> getDimensionColumns(List<ColumnFeature> columnFeatures) {
    List<ColumnFeature> result = new ArrayList<ColumnFeature>(columnFeatures.size());
    for (ColumnFeature columnFeature : columnFeatures) {
      if (columnFeature.isDimension())
        result.add(columnFeature);
    }
    return result;
  }

}
