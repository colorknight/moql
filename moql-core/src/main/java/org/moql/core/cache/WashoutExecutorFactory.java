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

import org.moql.core.WashoutExecutor;
import org.moql.metadata.WashoutStrategy;
/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class WashoutExecutorFactory {
	
	protected static WashoutExecutor fifo;
	
	protected static WashoutExecutor filo;
	
	protected static WashoutExecutor lfu;
	
	protected static WashoutExecutor lru;
	
	public static synchronized WashoutExecutor createWashoutExecutor(WashoutStrategy washoutStrategy) {
		if (washoutStrategy == WashoutStrategy.FILO) {
			if (filo == null) {
				filo = new FiloWashoutExecutor();
			}
			return fifo;
		} else if (washoutStrategy == WashoutStrategy.LFU) {
			if (lfu == null) {
				lfu = new LfuWashoutExecutor();
			}
			return lfu;
		} else if (washoutStrategy == WashoutStrategy.LRU) {
			if (lru == null) {
				lru = new LruWashoutExecutor();
			}
			return lru;
		} else {
			if (fifo == null) {
				fifo = new FifoWashoutExecutor();
			}
			return fifo;
		}
	}
}
