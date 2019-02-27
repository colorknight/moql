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
package org.datayoo.moql.core.table;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.MoqlRuntimeException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class
    /**
     * @author Tang Tadin
     */
ResultSetIterator implements Iterator<Object> {

  protected ResultSet resultSet;

  public ResultSetIterator(ResultSet resultSet) {
    Validate.notNull(resultSet, "Parameter 'resultSet' is null!");
    this.resultSet = resultSet;

  }

  @Override public boolean hasNext() {
    // TODO Auto-generated method stub
    try {
      return resultSet.next();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      throw new MoqlRuntimeException(e);
    }
  }

  @Override public Object next() {
    // TODO Auto-generated method stub
    Map<String, Object> record = new HashMap<String, Object>();
    try {
      ResultSetMetaData metadata = resultSet.getMetaData();
      for (int i = 1; i <= metadata.getColumnCount(); i++) {
        record.put(metadata.getCatalogName(i), resultSet.getObject(i));
      }
      return record;
    } catch (SQLException e) {
      throw new MoqlRuntimeException(e);
    }
  }

  @Override public void remove() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("");
  }

}
