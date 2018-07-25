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

import org.apache.commons.lang.Validate;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class DataSetMapImpl implements DataSetMap {
	
	protected Map<String, Object> dataSetMap = new HashMap<String, Object>();
	
	public DataSetMapImpl(){}
	
	public DataSetMapImpl(DataSetMap dataSetMap) {
		putAll(dataSetMap);
	}
	
	@Override
	public Set<MapEntry<String, Object>> entrySet() {
		// TODO Auto-generated method stub
		Set<MapEntry<String, Object>> entrySet = new HashSet<MapEntry<String, Object>>();
		for(Map.Entry<String, Object> entry : dataSetMap.entrySet()) {
			MapEntryImpl<String, Object> mEntry = new MapEntryImpl<String, Object>(entry.getKey());
			mEntry.setValue(entry.getValue());
			entrySet.add(mEntry);
		}
		return entrySet;
	}

	@Override
	public Object getDataSet(String dataSetName) {
		// TODO Auto-generated method stub
		Validate.notEmpty(dataSetName, "Parameter 'dataSetName' is empty!");
		return dataSetMap.get(dataSetName);
	}

	@Override
	public Object putDataSet(String dataSetName, Object dataSet) {
		// TODO Auto-generated method stub
		Validate.notEmpty(dataSetName, "Parameter 'dataSetName' is empty!");
		Validate.notNull(dataSet, "Parameter 'dataSet' is null!");
		if (!isDataSet(dataSet)) {
			Validate.notNull(dataSet, "Parameter 'dataSet' must be an instance of Array, Map, Iterable, RowSet or ResultSet!");
		}
		return dataSetMap.put(dataSetName, dataSet);
	}
	
	@Override
	public Object removeDataSet(String dataSetName) {
		// TODO Auto-generated method stub
		Validate.notEmpty(dataSetName, "Parameter 'dataSetName' is empty!");
		return dataSetMap.remove(dataSetName);
	}


	protected boolean isDataSet(Object dataSet) {
		if (dataSet.getClass().isArray())
			return true;
		if (dataSet instanceof Map)
			return true;
		if (dataSet instanceof Iterable)
			return true;
		if (dataSet instanceof RecordSet)
			return true;
		if (dataSet instanceof ResultSet)
			return true;
		return false;
	}

	@Override
	public void putAll(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		Validate.notNull(dataSetMap, "Parameter 'dataSetMap' is null!");
		for(MapEntry<String, Object> entry : dataSetMap.entrySet()) {
			this.dataSetMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	
}
