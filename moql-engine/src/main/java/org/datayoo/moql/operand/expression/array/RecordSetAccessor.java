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
package org.datayoo.moql.operand.expression.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.util.StringFormater;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Tang Tadin
 */
public class RecordSetAccessor implements ArrayAccessor {

  @Override
  public Object getObject(Object array, Object index) {
    // TODO Auto-generated method stub
    RecordSet rs = (RecordSet) array;
    if (index instanceof Number) {
      return rs.getRecordAsMap(((Number) index).intValue());
    }
    if (index.getClass().equals(String.class)) {
      return rs.getColumn((String) index);
      //			return rs.getRecordAsMap(Integer.valueOf((String)index));
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      return rs.getRecordAsMap(inx.intValue());
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public void setObject(Object array, Object index, Object value) {
    throw new UnsupportedOperationException(
        "The array of RecordSet doesn't support set object!");
  }

  @Override
  public Object removeObject(Object array, Object value) {
    RecordSet rs = (RecordSet) array;
    Iterator<Object[]> it = rs.getRecords().iterator();
    while (it.hasNext()) {
      Object n = it.next();
      if (Objects.equals(n, value))
        it.remove();
    }
    return array;
  }

  @Override
  public OperandContextList toOperandContextList(Object array) {
    // TODO Auto-generated method stub
    RecordSet rs = (RecordSet) array;
    return new OperandContextArrayList(rs.getRecordsAsMaps());
  }

  @Override
  public int getSize(Object array) {
    RecordSet rs = (RecordSet) array;
    return rs.getRecordsCount();
  }
}
