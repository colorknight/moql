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

import org.moql.core.*;
import org.moql.core.group.GroupRecordSetOperator;
import org.moql.metadata.LimitMetadata;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SQLServerTranslator extends MoqlGrammarTranslator {
	
	@Override
	@SuppressWarnings({ "rawtypes" })
	protected String translateCache(Cache cache) {
		// TODO Auto-generated method stub
		return "";
	}
	
	protected String translate2Sql(SelectorImpl selector) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translate2SelectClause(selector.getRecordSetOperator(), selector.getLimit()));
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
		return sbuf.toString();
	}
	
	protected String translate2SelectClause(RecordSetOperator recordSetOperator, Limit limit) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("select ");
		if (recordSetOperator.getCache() != null)
			sbuf.append(translateCache(recordSetOperator.getCache()));
		if (recordSetOperator.getColumns().getColumnsMetadata().isDistinct()) {
			sbuf.append("distinct ");
		}
		if (limit != null)
			sbuf.append(translate2LimitClause(limit));
		sbuf.append(translateColumns(recordSetOperator.getColumns()));
		return sbuf.toString();
	}
	
	protected String translate2LimitClause(Limit limit) {
		StringBuffer sbuf = new StringBuffer();
		LimitMetadata limitMetadata = limit.getLimitMetadata();
		sbuf.append("top ");
		sbuf.append(limitMetadata.getValue());
		if (limitMetadata.isPercent()) {
			sbuf.append("%");
		}
		sbuf.append(" ");
		return sbuf.toString();
	}
	
}
