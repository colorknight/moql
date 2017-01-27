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

import org.moql.RecordSet;
import org.moql.RecordSetDefinition;
import org.moql.core.RecordSetImpl;
import org.moql.metadata.ColumnMetadata;
import org.moql.metadata.ColumnsMetadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/**
 * 
 * @author Tang Tadin
 *
 */
public class Complementation extends AbstractRecordSetCombination {

	public Complementation(ColumnsMetadata columnsMetadata) {
		super(columnsMetadata);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected RecordSet combine(boolean all, RecordSet lRecordSet,
			RecordSet rRecordSet) {
		// TODO Auto-generated method stub
		int[] mappingIndexes = getColumnsMapping(
				lRecordSet.getRecordSetDefinition().getColumns(),
				rRecordSet.getRecordSetDefinition().getColumns());
		int[] indexes = createSequenceIndexes(
				lRecordSet.getRecordSetDefinition().getColumns().size());
		Comparator<Object[]> comparator = new RecordComparator(indexes, indexes);
		RecordTranslator translator = new RecordTranslator(mappingIndexes);
		
		List<Object[]> result = new ArrayList<Object[]>(
				lRecordSet.getRecordsCount());
		//	caculate the full set
		List<Object[]> full = new ArrayList<Object[]>(
				lRecordSet.getRecordsCount() + rRecordSet.getRecordsCount());
		full.addAll(lRecordSet.getRecords());
		for(Object[] record : rRecordSet.getRecords()) {
			full.add(translator.translate(record));
		}
		
		for(Object[] record : full) {
			if (!existRecord(record, lRecordSet.getRecords(), comparator)) {
				if (!all) {
					if (!existRecord(record, result, comparator))
						result.add(record);
				} else {
					result.add(record);
				}
			}
		}
		return new RecordSetImpl(lRecordSet.getRecordSetDefinition(),
				getStart(lRecordSet.getStart(), rRecordSet.getStart()),
				getEnd(lRecordSet.getEnd(), rRecordSet.getEnd()),
				result);
	}

	@Override
	protected RecordSet combine(boolean all, List<ColumnMetadata> columns,
			RecordSet lRecordSet, RecordSet rRecordSet) {
		// TODO Auto-generated method stub
		
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
				lRecordSet.getRecordsCount());
		//	caculate the full set
		List<Object[]> full = new ArrayList<Object[]>(
				lRecordSet.getRecordsCount() + rRecordSet.getRecordsCount());
		for(Object[] record : lRecordSet.getRecords()) {
			full.add(lTranslator.translate(record));
		}
		for(Object[] record : rRecordSet.getRecords()) {
			full.add(rTranslator.translate(record));
		}
		
		for(Object[] record : lRecordSet.getRecords()) {
			if (!existRecord(record, full, comparator)) {
				if (!all) {
					if (!existRecord(record, result, comparator))
						result.add(lTranslator.translate(record));
				} else {
					result.add(lTranslator.translate(record));
				}
			}
		}
		RecordSetDefinition recordSetDefinition = createRecordSetDefinition();
		return new RecordSetImpl(recordSetDefinition,
				getStart(lRecordSet.getStart(), rRecordSet.getStart()),
				getEnd(lRecordSet.getEnd(), rRecordSet.getEnd()),
				result);
	}

}
