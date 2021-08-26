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
package org.datayoo.moql;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public interface EntityMap extends Serializable {

	Object getEntity(String entityName);
	
	/**
	 * 
	 * @param entityName 不能包括保留字'.'
	 * @param entity
	 */
	Object putEntity(String entityName, Object entity);
	
	Set<MapEntry<String, Object>> entrySet();
	
	Object removeEntity(String entityName);
	
	void putAll(EntityMap entityMap);
	
	void putAll(Map<String, Object> map);
	
}
