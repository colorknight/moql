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

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.core.Condition;
import org.datayoo.moql.core.Join;
import org.datayoo.moql.core.Queryable;
import org.datayoo.moql.metadata.JoinMetadata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tang Tadin
 */
public class LeftJoin extends AbstractJoin {

  protected EntityMap rNullEntityMap;

  public LeftJoin(JoinMetadata joinMetadata,
      Queryable<? extends Object> queryable,
      Queryable<? extends Object> queryable2, Condition on) {
    super(joinMetadata, queryable, queryable2, on);
    // TODO Auto-generated constructor stub
    if (rTableName == null)
      rNullEntityMap = ((Join) rQueryable).getNullEntityMap();
  }

  @Override
  protected List<EntityMap> join() {
    // TODO Auto-generated method stub
    if (!rQueryable.iterator().hasNext()) {
      return justLeftOut();
    }
    List<EntityMap> result = new LinkedList<EntityMap>();
    EntityMap entityMap;
    for (Iterator<? extends Object> lit = lQueryable.iterator(); lit.hasNext(); ) {
      Object lObj = lit.next();
      EntityMap lEntityMap = null;
      if (lTableName == null) {
        lEntityMap = (EntityMap) lObj;
      }
      boolean bMatch = false;
      for (Iterator<? extends Object> rit = rQueryable.iterator(); rit.hasNext(); ) {
        Object rObj = rit.next();
        entityMap = new EntityMapImpl();
        if (lTableName == null) {
          entityMap.putAll(lEntityMap);
        } else {
          entityMap.putEntity(lTableName, lObj);
        }
        if (rTableName == null) {
          entityMap.putAll((EntityMap) rObj);
        } else {
          entityMap.putEntity(rTableName, rObj);
        }
        if (on != null) {
          if (on.isMatch(entityMap)) {
            bMatch = true;
            result.add(entityMap);
          }
        } else {
          result.add(entityMap);
          bMatch = true;
        }
      }
      if (!bMatch) {
        entityMap = new EntityMapImpl();
        if (lTableName == null) {
          entityMap.putAll(lEntityMap);
        } else {
          entityMap.putEntity(lTableName, lObj);
        }
        if (rTableName == null) {
          entityMap.putAll(rNullEntityMap);
        } else {
          entityMap.putEntity(rTableName, null);
        }
        result.add(entityMap);
      }
    }
    return result;
  }

}
