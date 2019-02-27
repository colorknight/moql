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

import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class EntityMapImpl implements EntityMap {

	protected Map<String, Object> entityMap = new HashMap<String, Object>();
	
	public EntityMapImpl() {}
	
	public EntityMapImpl(Map<String, Object> map) {
		Validate.notNull(map, "Parameter 'map' is null!");
		entityMap = map;
	}
	
	public EntityMapImpl(EntityMap entityMap) {
		putAll(entityMap);
	}
	
	@Override
	public Set<MapEntry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		Set<MapEntry<String, Object>> entrySet = new HashSet<MapEntry<String, Object>>();
		for(Map.Entry<String, Object> entry : entityMap.entrySet()) {
			MapEntryImpl<String, Object> mEntry = new MapEntryImpl<String, Object>(entry.getKey());
			mEntry.setValue(entry.getValue());
			entrySet.add(mEntry);
		}
		return entrySet;
	}

	@Override
	public Object getEntity(String entityName) {
		// TODO Auto-generated method stub
		return entityMap.get(entityName);
	}

	@Override
	public Object putEntity(String entityName, Object entity) {
		// TODO Auto-generated method stub
		Validate.notEmpty(entityName, "Parameter 'entityName' is empty!");
		return entityMap.put(entityName, entity);
	}

	@Override
	public Object removeEntity(String entityName) {
		// TODO Auto-generated method stub
		Validate.notEmpty(entityName, "Parameter 'entityName' is empty!");
		return entityMap.remove(entityName);
	}

	@Override
	public void putAll(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Validate.notNull(entityMap, "Parameter 'entityMap' is null!");
		for(MapEntry<String, Object> entry : entityMap.entrySet()) {
			this.entityMap.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void putAll(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Validate.notNull(map, "Parameter 'map' is null!");
		entityMap.putAll(map);
	}

	
}
