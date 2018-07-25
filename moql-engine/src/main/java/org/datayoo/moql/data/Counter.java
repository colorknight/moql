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

import java.io.Serializable;

/**
 * @author Tang Tadin
 */
public class Counter implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected int count;
  
  public Counter() {
  }
  
  public void increment() {
    count++;
  }

  public int getCount() {
    return count;
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return count;
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    if (!(obj instanceof Counter))
      return false;
    Counter counter = (Counter)obj;
    return count == counter.count;
  }

  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return String.valueOf(count);
  }
  
  
}
