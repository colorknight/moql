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
package org.datayoo.moql.core.combination;

import org.apache.commons.lang3.Validate;

import java.util.Comparator;

/**
 * @author Tang Tadin
 */
public class RecordComparator implements Comparator<Object[]> {

  protected int[] lIndexes;

  protected int[] rIndexes;

  public RecordComparator(int[] lIndexes, int[] rIndexes) {
    Validate.notNull(lIndexes, "Parameter 'lIndexes' is null!");
    Validate.notNull(rIndexes, "Parameter 'rIndexes' is null!");
    if (lIndexes.length != rIndexes.length) {
      throw new IllegalArgumentException(
          "The right indexes' size not equal the left one!");
    }
    this.lIndexes = lIndexes;
    this.rIndexes = rIndexes;
  }

  @Override
  @SuppressWarnings({ "rawtypes" })
  public int compare(Object[] o1, Object[] o2) {
    // TODO Auto-generated method stub
    int ret = 0;
    for (int i = 0; i < lIndexes.length; i++) {
      ret = compare((Comparable) o1[lIndexes[i]], (Comparable) o2[rIndexes[i]]);
      if (ret != 0)
        break;
    }
    return ret;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private int compare(Comparable cmp1, Comparable cmp2) {
    int ret = 0;
    if (cmp1 != null && cmp2 != null) {
      ret = cmp1.compareTo(cmp2);
    } else if (cmp1 == null && cmp2 != null) {
      ret = -1;
    } else if (cmp1 != null && cmp2 == null) {
      ret = 1;
    }
    return ret;
  }

}
