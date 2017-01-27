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

import java.text.DecimalFormat;
import java.util.List;
/**
 * 
 * @author Tang Tadin
 *
 */
public class Trunc extends AbstractFunction {
	
	public static final String FUNCTION_NAME = "trunc";
	
	protected int precision = 0;
	
	protected DecimalFormat decimalFormat; 
	
	public Trunc(List<Operand> parameters) {
		super(FUNCTION_NAME, 2, parameters);
		// TODO Auto-generated constructor stub
		Object object = parameters.get(1).operate(null);
		precision = Integer.valueOf(object.toString());
		if (precision > 0) {
	    StringBuffer sbuf = new StringBuffer();
	    sbuf.append("0.0");
		  for(int i = 1; i < precision; i++) {
		    sbuf.append('0');
		  }
		  decimalFormat = new DecimalFormat(sbuf.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.moql.operand.function.AbstractFunction#innerOperate(org.moql.data.EntityMap)
	 */
	@Override
	protected Object innerOperate(EntityMap entityMap) {
		// TODO Auto-generated method stub
		Object obj = parameters.get(0).operate(entityMap);
		if (obj == null)
		  return "N/A";
		double val = 0;
		if (obj instanceof Number) {
		  Number num = (Number)obj;
		  val = num.doubleValue();
		} else {
		  val = Double.valueOf(obj.toString());
		}
		if ((long)val == val)
		  return String.valueOf((long)val);
		if (Double.isNaN(val))
		  return String.valueOf(val);
		return decimalFormat.format(val);
	}

}
