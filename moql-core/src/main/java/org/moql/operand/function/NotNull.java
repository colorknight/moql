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
package org.moql.operand.function;

import org.moql.EntityMap;
import org.moql.Operand;
import org.moql.util.StringFormater;

import java.util.List;
/**
 * 
 * @author Tang Tadin
 *
 */
public class NotNull extends AggregationFunction {
	
	public static final String FUNCTION_NAME = "notNull";
	
	protected Operand operand;
	
	protected Object notNull;
	
	protected boolean first = true;
	
	protected Object defaultValue;
	
	public NotNull(List<Operand> parameters) {
		super(FUNCTION_NAME, VARIANT_PARAMETERS, parameters);
		// TODO Auto-generated constructor stub
		if (this.parameters.size() > 3)
      throw new IllegalArgumentException(StringFormater.format(
          "Function '{}' need 1 to 3 parameters!", name));
    if (parameters.size() > 1) {
      defaultValue = parameters.get(1).operate(null);
    }
    if (parameters.size() > 2) {
      Object object = parameters.get(2).operate(null);
      if (object instanceof Boolean) {
        first = ((Boolean)(object)).booleanValue();
      }
    }
    operand = parameters.get(0);
	}
	
	@Override
	public void increment(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = operand.operate(entityMap);
		if (obj == null) {
			return;
		}
		if (first && notNull == null) {
		  notNull = obj;
		} else {
		  notNull = obj;
		}
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
	  if (notNull == null)
	    return defaultValue;
		return notNull;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		notNull = null;
	}

}
