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
package org.datayoo.moql.sql;

import org.datayoo.moql.core.Cache;
import org.datayoo.moql.core.Limit;

/**
 * 
 * @author Tang Tadin
 *
 */
public class MySQLTranslator extends MoqlGrammarTranslator {
	
	@Override
	@SuppressWarnings({ "rawtypes" })
	protected String translateCache(Cache cache) {
		// TODO Auto-generated method stub
		return "";
	}
	
	protected String translate2LimitClause( Limit limit) {
		if (limit.getLimitMetadata().isPercent()) {
			throw new IllegalArgumentException("Limit clause doesn't supported percent!");
		}
		return super.translate2LimitClause(limit);
	}
	
}
