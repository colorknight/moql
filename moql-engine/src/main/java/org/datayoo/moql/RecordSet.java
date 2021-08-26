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
package org.datayoo.moql;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public interface RecordSet extends Serializable {

  RecordSetDefinition getRecordSetDefinition();

  Date getStart();

  Date getEnd();

  int getRecordsCount();

  List<Object[]> getRecords();

  List<Map<String, Object>> getRecordsAsMaps();

  List<RecordNode> getRecordsAsNodes();

  List<RecordNode> getRecordNodesByColumns(String[] columns);

  Object[] getRecord(int index);

  List<Object> getColumn(String columnName);

  Map<String, Object> getRecordAsMap(int index);

  /**
   *
   * @param record a record of the recordset
   * @return the map with the column's name and
   * the column's value of the record
   */
  Map<String, Object> toMap(Object[] record);

}
