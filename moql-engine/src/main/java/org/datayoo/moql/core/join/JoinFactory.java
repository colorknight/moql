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
package org.datayoo.moql.core.join;

import org.datayoo.moql.core.Condition;
import org.datayoo.moql.core.Join;
import org.datayoo.moql.core.Queryable;
import org.datayoo.moql.metadata.JoinMetadata;
import org.datayoo.moql.metadata.JoinType;

/**
 * @author Tang Tadin
 */
public abstract class JoinFactory {

	public static Join createJoin(JoinMetadata joinMetadata,
			Queryable<? extends Object> lQueryable,
			Queryable<? extends Object> rQueryable, Condition on) {
		if (joinMetadata.getJoinType() == JoinType.FULL) {
			return new FullJoin(joinMetadata, lQueryable, rQueryable, on);
		} else if (joinMetadata.getJoinType() == JoinType.LEFT) {
			return new LeftJoin(joinMetadata, lQueryable, rQueryable, on);
		} else if (joinMetadata.getJoinType() == JoinType.RIGHT) {
			return new RightJoin(joinMetadata, lQueryable, rQueryable, on);
		} else {
			return new InnerJoin(joinMetadata, lQueryable, rQueryable, on);
		}
	}
}
