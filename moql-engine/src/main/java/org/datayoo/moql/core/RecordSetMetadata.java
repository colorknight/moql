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
package org.datayoo.moql.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.RecordSetDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 * 
 */
public class RecordSetMetadata implements RecordSetDefinition {

  protected List<ColumnDefinition> columns;

  protected List<ColumnDefinition> groups = new ArrayList<ColumnDefinition>();

  public RecordSetMetadata(List<ColumnDefinition> columns,
      List<ColumnDefinition> groups) {
    Validate.notEmpty(columns, "Parameter 'columns' is empty!");
    this.columns = columns;
    if (groups != null)
      this.groups = groups;
  }

  @Override
  public List<ColumnDefinition> getColumns() {
    // TODO Auto-generated method stub
    return columns;
  }

  @Override
  public int getColumnIndex(String column) {
    // TODO Auto-generated method stub
    return getIndex(column, columns);
  }

  protected int getIndex(String column, List<ColumnDefinition> columns) {
    int index = -1;
    int i = 0;
    for (ColumnDefinition columnDefinition : columns) {
      if (column.equals(columnDefinition.getName())) {
        index = i;
        break;
      }
      i++;
    }
    return index;
  }

  @Override
  public List<ColumnDefinition> getGroupColumns() {
    // TODO Auto-generated method stub
    return groups;
  }

  @Override
  public boolean isGroupColumn(String column) {
    // TODO Auto-generated method stub
    for (ColumnDefinition columnDefinition : groups) {
      if (column.equals(columnDefinition.getName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<ColumnDefinition> getNonGroupColumns() {
    // TODO Auto-generated method stub
    List<ColumnDefinition> nonGroupColumns = new ArrayList<ColumnDefinition>(
        columns.size());
    for (ColumnDefinition column : columns) {
      if (!isGroupColumn(column.getName()))
        nonGroupColumns.add(column);
    }
    return nonGroupColumns;
  }

}
