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

import org.moql.core.CacheElement;
import org.moql.core.WashoutExecutor;

import java.util.Collection;
import java.util.Iterator;
/**
 * 
 * @author Tang Tadin
 *
 */
//Least Recently Used
public class LruWashoutExecutor implements WashoutExecutor {

	@Override
	public CacheElement washout(Collection<CacheElement> collection) {
		// TODO Auto-generated method stub
		CacheElement lru = null;
        synchronized(collection) {
            lru = lru(collection);
            collection.remove(lru);
        }
        return lru;
	}

    private CacheElement lru(Collection<CacheElement> collection) {
        CacheElement lru = null;
        long accessTime = 0;
        for(Iterator<CacheElement> it = collection.iterator(); it.hasNext();) {
            CacheElement element = (CacheElement)it.next();
            if (element.getLastAccessTime() < accessTime || accessTime == 0) {
                accessTime = element.getLastAccessTime();
                lru = element;
            }
        }
        return lru;
    }
}
