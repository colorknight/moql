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
package org.moql.core.join;

import org.apache.commons.lang.Validate;
import org.moql.DataSetMap;
import org.moql.EntityMap;
import org.moql.EntityMapImpl;
import org.moql.core.Condition;
import org.moql.core.Join;
import org.moql.core.Queryable;
import org.moql.core.Table;
import org.moql.metadata.JoinMetadata;

import java.util.Iterator;
import java.util.List;

/**
 * @author Tang Tadin
 */
public abstract class AbstractJoin implements Join {
	
	protected JoinMetadata joinMetadata;
	
	protected Queryable<? extends Object> lQueryable;
	
	protected Queryable<? extends Object> rQueryable;
	
	protected String lTableName;
	
	protected String rTableName;
	
	protected Condition on;
	
	protected List<EntityMap> result;
	
	protected EntityMap nullEntityMap;
	
	public AbstractJoin(JoinMetadata joinMetadata, Queryable<? extends Object> lQueryable,
			Queryable<? extends Object> rQueryable, Condition on) {
		Validate.notNull(joinMetadata, "Parameter 'joinMetadata' is null!");
		Validate.notNull(lQueryable, "Parameter 'lQueryable' is null!");
		Validate.notNull(rQueryable, "Parameter 'rQueryable' is null!");
		
		this.joinMetadata = joinMetadata;
		this.lQueryable = lQueryable;
		this.rQueryable= rQueryable;
		this.on = on;
		lTableName = getTableName(lQueryable);
		rTableName = getTableName(rQueryable);
		initNullEntityMap();
	}
	
	protected String getTableName(Queryable<? extends Object> queryable) {
		if (queryable instanceof Table) {
			return ((Table)queryable).getTableMetadata().getName();
		}
		return null;
	}
	
	protected void initNullEntityMap() {
		nullEntityMap = new EntityMapImpl();
		if (lTableName == null) {
			nullEntityMap.putAll(((Join)lQueryable).getNullEntityMap());
		} else {
			nullEntityMap.putEntity(lTableName, null);
		}
		if (rTableName == null) {
			nullEntityMap.putAll(((Join)rQueryable).getNullEntityMap());
		} else {
			nullEntityMap.putEntity(rTableName, null);
		}
	}

	@Override
	public JoinMetadata getJoinMetadata() {
		// TODO Auto-generated method stub
		return joinMetadata;
	}

	@Override
	public Queryable<? extends Object> getLeftQueryable() {
		// TODO Auto-generated method stub
		return lQueryable;
	}

	@Override
	public Queryable<? extends Object> getRightQueryable() {
		// TODO Auto-generated method stub
		return rQueryable;
	}

	public Condition getOn() {
		return on;
	}

	/* (non-Javadoc)
	 * @see org.moql.core.Queryable#bind(org.moql.DataSetMap)
	 */
	@Override
	public void bind(DataSetMap dataSetMap) {
		// TODO Auto-generated method stub
		lQueryable.bind(dataSetMap);
		rQueryable.bind(dataSetMap);
		result = join();
	}
	
	protected abstract List<EntityMap> join();

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<EntityMap> iterator() {
		// TODO Auto-generated method stub
		return result.iterator();
	}

	/* (non-Javadoc)
	 * @see org.moql.core.Join#getNullEntityMap()
	 */
	@Override
	public EntityMap getNullEntityMap() {
		// TODO Auto-generated method stub
		return nullEntityMap;
	}
	
}
