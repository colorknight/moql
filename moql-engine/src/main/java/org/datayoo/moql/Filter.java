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
import java.util.List;

/**
 * @author Tang Tadin
 */
public interface Filter extends Serializable {

  boolean isMatch(EntityMap entityMap);

  boolean isMatch(Object[] entityArray);

  List<EntityMap> match(List<EntityMap> entityMaps);

  List<Object[]> matchArray(List<Object[]> entityArrays);

  /**
   * bind the index of entity
   */
  void bind(String[] entityNames);

  /**
   * whether the operand binded the index
   */
  boolean isBinded();

  /**
   * get or caculate the value from the given entity array. The entity array
   * is an array of entities. The order of entity is samed as entityNames inputed
   * in function bind.
   */
  List<Object[]> operate(List<Object[]> entityArrays);
}
