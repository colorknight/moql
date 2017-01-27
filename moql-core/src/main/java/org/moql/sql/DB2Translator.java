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
package org.moql.sql;

import org.moql.core.Cache;
import org.moql.core.HavingImpl;
import org.moql.core.OrderImpl;
import org.moql.core.SelectorImpl;
import org.moql.core.Limit;
import org.moql.core.group.GroupRecordSetOperator;
/**
 * 
 * @author Tang Tadin
 *
 */
public class DB2Translator extends MoqlTranslator {
	@Override
	@SuppressWarnings({ "rawtypes" })
	protected String translateCache(Cache cache) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String translate2Sql(SelectorImpl selector) {
		// TODO Auto-generated method stub
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translate2SelectClause(selector.getRecordSetOperator()));
		sbuf.append(translate2FromClause(selector.getTables()));
		if (selector.getWhere() != null) {
			sbuf.append(translate2WhereClause(selector.getWhere()));
		}
		if (selector.getRecordSetOperator() instanceof GroupRecordSetOperator) {
			sbuf.append(translate2GroupbyClause((GroupRecordSetOperator)selector.getRecordSetOperator()));
		}
		if (selector.getHaving() != null) {
			sbuf.append(translate2HavingClause((HavingImpl)selector.getHaving()));
		}
		if (selector.getOrder() != null) {
			sbuf.append(translate2OrderbyClause((OrderImpl)selector.getOrder()));
		}
		if (selector.getLimit() != null) {
			sbuf.append(translate2FetchFirstClause(selector.getLimit()));
		}
		
		return sbuf.toString();
	}

	
	protected String translate2FetchFirstClause( Limit limit) {
		if (limit.getLimitMetadata().isPercent()) {
			throw new IllegalArgumentException("Fetch first clause doesn't supported percent!");
		}
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("fetch first ");
		sbuf.append(limit.getLimitMetadata().getValue());
		sbuf.append(" rows only ");
		return sbuf.toString();
	}
}
