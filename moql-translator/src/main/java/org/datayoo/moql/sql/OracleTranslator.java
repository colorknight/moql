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

import org.datayoo.moql.core.*;
import org.datayoo.moql.core.group.GroupRecordSetOperator;

/**
 * 
 * @author Tang Tadin
 *
 */
public class OracleTranslator extends MoqlGrammarTranslator {

	@Override
	@SuppressWarnings({ "rawtypes" })
	protected String translateCache(Cache cache) {
		// TODO Auto-generated method stub
		return "";
	}
	
	protected String translate2Sql(SelectorImpl selector) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(translate2SelectClause(selector.getRecordSetOperator()));
		sbuf.append(translate2FromClause(selector.getTables()));
		if (selector.getWhere() != null 
				|| selector.getLimit() != null) {
			sbuf.append(translate2WhereClause(selector.getWhere(), selector.getLimit()));
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
	
	protected String translate2WhereClause(Condition condition, Limit top) {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("where ");
		if (top != null)
			sbuf.append(translateWhereLimit(top));
		if (condition != null)
			sbuf.append(translateOperand(condition.getOperand()));
		return sbuf.toString();
	}

	protected String translateWhereLimit(Limit limit) {
		// TODO Auto-generated method stub
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("rownum <= ");
		sbuf.append(limit.getLimitMetadata().getValue());
		sbuf.append(" ");
		return sbuf.toString();
	}


}
