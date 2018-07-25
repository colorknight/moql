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
package org.datayoo.moql.core.combination;

import org.datayoo.moql.RecordSet;
import org.datayoo.moql.RecordSetDefinition;
import org.datayoo.moql.core.RecordSetImpl;
import org.datayoo.moql.metadata.ColumnMetadata;
import org.datayoo.moql.metadata.ColumnsMetadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Tang Tadin
 *
 */
public class Union extends AbstractRecordSetCombination {
	
	public Union(ColumnsMetadata columnsMetadata) {
		super(columnsMetadata);
	}
	
	protected RecordSet combine(boolean all,
			RecordSet lRecordSet,
			RecordSet rRecordSet) {
		int[] mappingIndexes = getColumnsMapping(
				lRecordSet.getRecordSetDefinition().getColumns(),
				rRecordSet.getRecordSetDefinition().getColumns());
		int[] indexes = createSequenceIndexes(
				lRecordSet.getRecordSetDefinition().getColumns().size());
		Comparator<Object[]> comparator = new RecordComparator(indexes, mappingIndexes);
		RecordTranslator translator = new RecordTranslator(mappingIndexes);
		
		List<Object[]> result = new ArrayList<Object[]>(
				lRecordSet.getRecordsCount() + rRecordSet.getRecordsCount());
		result.addAll(lRecordSet.getRecords());
		
		if (!all) { 
			for(Object[] record : rRecordSet.getRecords()) {
				if (!existRecord(record, result, comparator)) {
					result.add(translator.translate(record));
				}
			}
		} else {
			for(Object[] record : rRecordSet.getRecords()) {
				result.add(translator.translate(record));
			}
		}
		return new RecordSetImpl(lRecordSet.getRecordSetDefinition(),
				getStart(lRecordSet.getStart(), rRecordSet.getStart()),
				getEnd(lRecordSet.getEnd(), rRecordSet.getEnd()),
				result);
	}
	
	
	
	protected RecordSet combine(boolean all, List<ColumnMetadata> columns,
			RecordSet lRecordSet,
			RecordSet rRecordSet) {
		int[] lMappingIndexes = getColumnsMapping(
				columns,
				lRecordSet.getRecordSetDefinition().getColumns());
		RecordTranslator lTranslator = new RecordTranslator(lMappingIndexes);
		int[] rMappingIndexes = getColumnsMapping(
				columns,
				rRecordSet.getRecordSetDefinition().getColumns());
		RecordTranslator rTranslator = new RecordTranslator(rMappingIndexes);
		Comparator<Object[]> comparator = new RecordComparator(lMappingIndexes, rMappingIndexes);
		List<Object[]> result = new ArrayList<Object[]>(
				lRecordSet.getRecordsCount() + rRecordSet.getRecordsCount());
		for(Object[] record : lRecordSet.getRecords()) {
			result.add(lTranslator.translate(record));
		}
		if (!all) { 
			for(Object[] record : rRecordSet.getRecords()) {
				if (!existRecord(record, result, comparator)) {
					result.add(rTranslator.translate(record));
				}
			}
		} else {
			for(Object[] record : rRecordSet.getRecords()) {
				result.add(rTranslator.translate(record));
			}
		}
		RecordSetDefinition recordSetDefinition = createRecordSetDefinition();
		return new RecordSetImpl(recordSetDefinition,
				getStart(lRecordSet.getStart(), rRecordSet.getStart()),
				getEnd(lRecordSet.getEnd(), rRecordSet.getEnd()),
				result);
	}

}
