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
public class FullJoin extends AbstractJoin {

  protected EntityMap lNullEntityMap;

  protected EntityMap rNullEntityMap;

  public FullJoin(JoinMetadata joinMetadata,
      Queryable<? extends Object> queryable,
      Queryable<? extends Object> queryable2, Condition on) {
    super(joinMetadata, queryable, queryable2, on);
    // TODO Auto-generated constructor stub
    if (lTableName == null)
      lNullEntityMap = ((Join) lQueryable).getNullEntityMap();
    if (rTableName == null)
      rNullEntityMap = ((Join) rQueryable).getNullEntityMap();
  }

  @Override
  protected List<EntityMap> join() {
    // TODO Auto-generated method stub
    if (!rQueryable.iterator().hasNext()) {
      return justLeftOut();
    }
    if (!lQueryable.iterator().hasNext()) {
      return justRightOut();
    }
    List<EntityMap> result = new LinkedList<EntityMap>();
    EntityMap entityMap;
    byte[] flags = new byte[getRQueryableSize()];

    for (Iterator<? extends Object> lit = lQueryable.iterator(); lit.hasNext(); ) {
      Object lObj = lit.next();
      EntityMap lEntityMap = null;
      if (lTableName == null) {
        lEntityMap = (EntityMap) lObj;
      }
      boolean bMatch = false;
      int i = 0;
      for (Iterator<? extends Object> rit = rQueryable.iterator(); rit.hasNext(); i++) {
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
            flags[i] = 1;
            result.add(entityMap);
          }
        } else {
          bMatch = true;
          flags[i] = 1;
          result.add(entityMap);
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
    doLeftNull(result, flags);
    return result;
  }

  protected int getRQueryableSize() {
    int size = 0;
    for (Iterator<? extends Object> rit = rQueryable.iterator(); rit.hasNext(); ) {
      rit.next();
      size++;
    }
    return size;
  }

  protected void doLeftNull(List<EntityMap> result, byte[] flags) {
    int i = 0;
    for (Iterator<? extends Object> rit = rQueryable.iterator(); rit.hasNext(); i++) {
      Object rObj = rit.next();
      if (flags[i] == 1)
        continue;
      EntityMap entityMap = new EntityMapImpl();
      if (rTableName == null) {
        entityMap.putAll((EntityMap) rObj);
      } else {
        entityMap.putEntity(rTableName, rObj);
      }
      if (lTableName == null) {
        entityMap.putAll(lNullEntityMap);
      } else {
        entityMap.putEntity(lTableName, null);
      }
      result.add(entityMap);
    }
  }

}
