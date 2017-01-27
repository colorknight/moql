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

import org.moql.MapEntryImpl;
import org.moql.core.CacheElement;

import java.io.Serializable;
/**
 * 
 * @author Tang Tadin
 *
 */
public class CacheElementImpl extends MapEntryImpl<Object,Object> implements CacheElement, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    protected long lastAccessTime;
    protected long accessTimes;
    
    public CacheElementImpl(Object key) {
        super(key);
        hit();
    }
    
    public synchronized void hit() {
        lastAccessTime = System.currentTimeMillis();
        accessTimes++;
    }
    /**
     * @return the lastAccessTime
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    /**
     * @return the accessTimes
     */
    public long getAccessTimes() {
        return accessTimes;
    }
}
