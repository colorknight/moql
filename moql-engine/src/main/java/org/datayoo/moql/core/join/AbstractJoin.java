/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.core.join;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.DataSetMap;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.core.Condition;
import org.datayoo.moql.core.Join;
import org.datayoo.moql.core.Queryable;
import org.datayoo.moql.core.Table;
import org.datayoo.moql.metadata.JoinMetadata;

import java.util.Iterator;
import java.util.LinkedList;
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

  public AbstractJoin(JoinMetadata joinMetadata,
      Queryable<? extends Object> lQueryable,
      Queryable<? extends Object> rQueryable, Condition on) {
    Validate.notNull(joinMetadata, "Parameter 'joinMetadata' is null!");
    Validate.notNull(lQueryable, "Parameter 'lQueryable' is null!");
    Validate.notNull(rQueryable, "Parameter 'rQueryable' is null!");

    this.joinMetadata = joinMetadata;
    this.lQueryable = lQueryable;
    this.rQueryable = rQueryable;
    this.on = on;
    lTableName = getTableName(lQueryable);
    rTableName = getTableName(rQueryable);
    initNullEntityMap();
  }

  protected String getTableName(Queryable<? extends Object> queryable) {
    if (queryable instanceof Table) {
      return ((Table) queryable).getTableMetadata().getName();
    }
    return null;
  }

  protected void initNullEntityMap() {
    nullEntityMap = new EntityMapImpl();
    if (lTableName == null) {
      nullEntityMap.putAll(((Join) lQueryable).getNullEntityMap());
    } else {
      nullEntityMap.putEntity(lTableName, null);
    }
    if (rTableName == null) {
      nullEntityMap.putAll(((Join) rQueryable).getNullEntityMap());
    } else {
      nullEntityMap.putEntity(rTableName, null);
    }
  }

  protected List<EntityMap> justLeftOut() {
    List<EntityMap> result = new LinkedList<EntityMap>();
    for (Iterator<? extends Object> lit = lQueryable.iterator(); lit.hasNext(); ) {
      Object lObj = lit.next();
      EntityMap entityMap = new EntityMapImpl();
      if (lTableName == null) {
        entityMap.putAll((EntityMap) lObj);
      } else {
        entityMap.putEntity(lTableName, lObj);
      }
      result.add(entityMap);
    }
    return result;
  }

  protected List<EntityMap> justRightOut() {
    List<EntityMap> result = new LinkedList<EntityMap>();
    for (Iterator<? extends Object> lit = rQueryable.iterator(); lit.hasNext(); ) {
      Object lObj = lit.next();
      EntityMap entityMap = new EntityMapImpl();
      if (rTableName == null) {
        entityMap.putAll((EntityMap) lObj);
      } else {
        entityMap.putEntity(rTableName, lObj);
      }
      result.add(entityMap);
    }
    return result;
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
