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
package org.moql.core.cache;

import org.apache.commons.lang.Validate;
import org.moql.core.Cache;
import org.moql.core.CacheElement;
import org.moql.core.WashoutExecutor;
import org.moql.metadata.CacheMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
/**
 * 
 * @author Tang Tadin
 *
 * @param <K>
 * @param <V>
 */
public class CacheImpl<K, V> implements Cache<K, V>{
	
	private static final Logger logger = LoggerFactory.getLogger(CacheImpl.class);
	
	protected List<CacheElement> listCache;
	
	protected Map<K,CacheElement> mapCache;
	
	protected CacheMetadata cacheMetadata;
	
	protected WashoutExecutor washoutExecutor;
	
	public CacheImpl(CacheMetadata cacheMetadata) {
		Validate.notNull(cacheMetadata, "Parameter 'cacheMetadata' is null!");
		this.cacheMetadata = cacheMetadata;
		washoutExecutor = WashoutExecutorFactory.createWashoutExecutor(cacheMetadata.getWashoutStrategy());
		initialize();
	}
	
	protected void initialize() {
		listCache = new LinkedList<CacheElement>();
		if (cacheMetadata.getSize() != CacheMetadata.INFINITE)
			mapCache = new HashMap<K,CacheElement>(cacheMetadata.getSize());
		else
			mapCache = new HashMap<K,CacheElement>();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public synchronized V get(Object key) {
		// TODO Auto-generated method stub
		CacheElement element = mapCache.get(key);
		if (element == null)
			return null;
		return (V)element.getValue();
	}

	@Override
	public CacheMetadata getCacheMetadata() {
		// TODO Auto-generated method stub
		return cacheMetadata;
	}

	@Override
	public WashoutExecutor getWashoutExecutor() {
		// TODO Auto-generated method stub
		return washoutExecutor;
	}

	@Override
	public synchronized boolean isFull() {
		// TODO Auto-generated method stub
		if (cacheMetadata.getSize() == CacheMetadata.INFINITE)
            return false;
        if (listCache.size() < cacheMetadata.getSize())
            return false;
        return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized V put(K key, V value) {
		// TODO Auto-generated method stub
        CacheElement oldElement = null;
        if (isFull()) {
            oldElement = washoutExecutor.washout(listCache);
            if (oldElement == null)
                return value;
            mapCache.remove(oldElement.getKey());
            if (logger.isDebugEnabled()) {
            	logger.debug("One cache element is washed out!");
            }
        }   
        CacheElement element = new CacheElementImpl(key);
        element.setValue(value);
        listCache.add(element);
        mapCache.put(key, element);
        if (oldElement != null)
        	return (V)oldElement.getValue();
        return null;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return listCache.size();
	}
	
	public void clear() {
		initialize();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<V> values() {
		// TODO Auto-generated method stub
		List<V> values = new ArrayList<V>(listCache.size());
		for(CacheElement cacheElement : listCache) {
			values.add((V)cacheElement.getValue());
		}
		return values;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> values(Cache.RecordConverter<V> converter) {
		// TODO Auto-generated method stub
		List<Object[]> values = new ArrayList<Object[]>(listCache.size());
		for(CacheElement cacheElement : listCache) {
			V value = (V)cacheElement.getValue();
			values.add(converter.convert(value));
		}
		return values;
	}

}
