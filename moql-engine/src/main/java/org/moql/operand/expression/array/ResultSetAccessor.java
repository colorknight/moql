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
package org.moql.operand.expression.array;

import org.moql.NumberConvertable;
import org.moql.OperateException;
import org.moql.operand.OperandContextLinkedList;
import org.moql.operand.OperandContextList;
import org.moql.util.StringFormater;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
public class ResultSetAccessor implements ArrayAccessor {

	@Override
	public Object getObject(Object array, Object index) {
		// TODO Auto-generated method stub
		ResultSet rs = (ResultSet)array;
		if (index instanceof Number) {
			return getObject(rs, ((Number)index).intValue());
		}
		if (index.getClass().equals(String.class)) {
			return getObject(rs, Integer.valueOf((String)index));
		}
		if (index instanceof NumberConvertable) {
			Number inx = ((NumberConvertable)index).toNumber();
			return getObject(rs, inx.intValue());
		}
		throw new IllegalArgumentException(StringFormater.format("Unsupport 'index' of class '{}'!", index.getClass().getName()));

	}
	
	protected Object getObject(ResultSet rs, int index) {
		int i = 0;
		try {
			while(rs.next()) {
				if (i++ == index) {
					return getRecord(rs);
				}
			}
			rs.first();
		} catch(SQLException e) {
			throw new OperateException(e);
		}
		throw new IndexOutOfBoundsException();
	}
	
	protected Map<String, Object> getRecord(ResultSet rs) throws SQLException {
		Map<String, Object> record = new HashMap<String, Object>();
		ResultSetMetaData metadata = rs.getMetaData();
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
			record.put(metadata.getCatalogName(i), rs.getObject(i));
		}
		return record;
	}

	@Override
	public OperandContextList toOperandContextList(Object array) {
		// TODO Auto-generated method stub
		ResultSet rs = (ResultSet)array;
		OperandContextList ctxList = new OperandContextLinkedList();
		try {
			while(rs.next()) {
				ctxList.add(getRecord(rs));
			}
		} catch(SQLException e) {
			throw new OperateException(e);
		}
		return ctxList;
	}

}
