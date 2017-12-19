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
package org.moql;

import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SelectorContextImpl implements SelectorContext {

	protected Map<String, Object> properties = new HashMap<String, Object>();
	
	public SelectorContextImpl() {}
	
	public SelectorContextImpl(SelectorContext selectorContext) {
		putAll(selectorContext);
	}
	
	@Override
	public Set<MapEntry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		Set<MapEntry<String, Object>> entrySet = new HashSet<MapEntry<String, Object>>();
		for(Map.Entry<String, Object> entry : properties.entrySet()) {
			MapEntryImpl<String, Object> mEntry = new MapEntryImpl<String, Object>(entry.getKey());
			mEntry.setValue(entry.getValue());
			entrySet.add(mEntry);
		}
		return entrySet;
	}

	@Override
	public Object getProperty(String name) {
		// TODO Auto-generated method stub
		return properties.get(name);
	}

	@Override
	public Object setProperty(String name, Object value) {
		// TODO Auto-generated method stub
		Validate.notEmpty(name, "Parameter 'name' is empty!");
		return properties.put(name, value);
	}
	
	@Override
	public void putAll(SelectorContext context) {
		// TODO Auto-generated method stub
		Validate.notNull(context, "Parameter 'context' is null!");
		for(MapEntry<String, Object> entry : context.entrySet()) {
			this.properties.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void putAll(Map<String, Object> map) {
		// TODO Auto-generated method stub
		Validate.notNull(map, "Parameter 'map' is null!");
		properties.putAll(map);
	}

}
