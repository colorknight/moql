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
package org.moql.core.group;

import org.moql.EntityMap;
import org.moql.core.Column;
import org.moql.core.EntityOperator;

/**
 * @author Tang Tadin
 */
public class GroupRecord implements EntityOperator<Object[]> {

  protected Object[] groupKeys;

  protected int[] groupColumnIndexes;

  protected Column[] columns;

  public GroupRecord(Object[] groupKeys, int[] groupColumnIndexes,
      Column[] columns) {
    this.groupKeys = groupKeys;
    this.groupColumnIndexes = groupColumnIndexes;
    this.columns = columns;
    if (groupKeys.length != groupColumnIndexes.length)
      throw new IllegalArgumentException(
          "The array of groupKeys and the groupColumnIndexes should be equal！");
  }

  @Override
  public Object[] getValue() {
    // TODO Auto-generated method stub
    Object[] record = new Object[columns.length];
    for (int i = 0; i < columns.length; i++) {
      if (columns[i] != null) {
        record[i] = columns[i].getValue();
      }
    }
    for (int i = 0; i < groupColumnIndexes.length; i++) {
      record[groupColumnIndexes[i]] = groupKeys[i];
    }
    return record;
  }

  @Override
  public void operate(EntityMap entityMap) {
    // TODO Auto-generated method stub
    for (int i = 0; i < columns.length; i++) {
      if (columns[i] != null) {
        columns[i].operate(entityMap);
      }
    }
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    for (int i = 0; i < columns.length; i++) {
      if (columns[i] != null) {
        columns[i].clear();
      }
    }
  }
}
