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

import org.apache.commons.lang.ArrayUtils;

/**
 * @author Tang Tadin
 */
public class GroupKey {
  
  protected Object[] groups;
  
  protected int hashCode;
  
  public GroupKey() {}
  
  public GroupKey(Object[] groups) {
    initialize(groups);
  }
  
  public void initialize(Object[] groups) {
    check(groups);
    this.groups = groups;
    hashCode = 0;
    for(int i = 0; i < groups.length; i++) {
      if (groups[i] != null) {
        hashCode += groups[i].hashCode();
      }
    }
  }
  
  protected void check(Object[] groups) {
    for(int i = 0; i < groups.length; i++) {
      if (groups[i].getClass().isArray())
        throw new IllegalArgumentException("Doesn't support grouping data by array!");
    }
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    if (!(obj instanceof GroupKey))
      return false;
    Object[] target = ((GroupKey)obj).groups;
    return ArrayUtils.isEquals(groups, target);
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return hashCode;
  }
  
  public Object[] getGroups() {
    return groups;
  }

}
