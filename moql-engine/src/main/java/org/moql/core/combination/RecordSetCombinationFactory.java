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
package org.moql.core.combination;

import org.moql.core.RecordSetCombination;
import org.moql.metadata.ColumnsMetadata;
import org.moql.metadata.CombinationType;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class RecordSetCombinationFactory {

	public static RecordSetCombination createRecordSetCombination(CombinationType combinationType,
			ColumnsMetadata columnsMetadata) {
		
		if (combinationType == CombinationType.UNION) {
			return new Union(columnsMetadata);
		} else if (combinationType == CombinationType.INTERSECT) {
			return new Intersect(columnsMetadata);
		} else if (combinationType == CombinationType.EXCEPT) {
			return new Except(columnsMetadata);
		} else if (combinationType == CombinationType.SYMEXCEPT) {
			return new SymExcept(columnsMetadata);
		} else {
			return new Complementation(columnsMetadata);
		}
	}
}
