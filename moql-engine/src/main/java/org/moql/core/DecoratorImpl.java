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
package org.moql.core;

import org.apache.commons.lang.Validate;
import org.moql.MoqlException;
import org.moql.Operand;
import org.moql.RecordSet;
import org.moql.metadata.DecorateMetadata;
import org.moql.operand.OperandFactory;
import org.moql.operand.function.decorator.DecorateFunction;
import org.moql.util.StringFormater;

import java.util.LinkedList;
import java.util.List;

public class DecoratorImpl implements Decorator {
	
	protected List<DecorateMetadata> decorateMetadatas;
	
	protected List<DecorateFunction> decorators = new LinkedList<DecorateFunction>();
	
	public DecoratorImpl(List<DecorateMetadata> decorateMetadatas, OperandFactory operandFactory) {
		Validate.notEmpty(decorateMetadatas, "decorateMetadatas is empty!");
		Validate.notNull(operandFactory, "operandFactory is null!");
		
		initialize(decorateMetadatas, operandFactory);
		this.decorateMetadatas = decorateMetadatas;
	}
	
	protected void initialize(List<DecorateMetadata> decorateMetadatas, OperandFactory operandFactory) {
		for(DecorateMetadata decorateMetadata : decorateMetadatas) {
			try {
				Operand operand = operandFactory.createOperand(decorateMetadata.getDecorator());
				if (!(operand instanceof DecorateFunction)) {
					throw new IllegalArgumentException(StringFormater.format(
							"The operand '{}' is not a decorator!", decorateMetadata.getDecorator()));
				}
				DecorateFunction decorator = (DecorateFunction)operand;
				decorators.add(decorator);
			} catch (MoqlException e) {
				// TODO Auto-generated catch block
				throw new IllegalArgumentException(StringFormater.format(
						"Can't create decorator of '{}'!", decorateMetadata.getDecorator()), e);
			}
		}
	}

	@Override
	public RecordSet decorate(RecordSet recordSet, Columns columns) {
		// TODO Auto-generated method stub
		for(DecorateFunction decorator : decorators) {
			recordSet = decorator.decorate(recordSet, columns);
		}
		return recordSet;
	}

	@Override
	public List<DecorateMetadata> getDecorateMetadatas() {
		// TODO Auto-generated method stub
		return decorateMetadatas;
	}

}
