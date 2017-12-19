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

package org.moql.util;

import org.apache.commons.lang.Validate;
import org.moql.NumberConvertable;

/**
 * Compare two objects for order. Returns a negative integer, zero, 
 * or a positive integer as this first object is less than, equal to, 
 * or greater than the second object.
 * 
 * @author Tang Tadin
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class CompareHelper {
	
	public static int compare(Object lOperand, Object rOperand) {
		if (lOperand == null && rOperand == null)
			return 0;
		Validate.notNull(lOperand, "Parameter 'lOperand' is null!");
		Validate.notNull(rOperand, "Parameter 'rOperand' is null!");
		if (lOperand instanceof Number) {
			return compareNumber((Number)lOperand, rOperand);
		}
		if (rOperand instanceof Number) {
			int ret = compareNumber((Number)rOperand, lOperand);
			return reverse(ret);
		}
		if (lOperand instanceof Comparable) {
			return compare((Comparable)lOperand, rOperand);
		}
		if (rOperand instanceof Comparable) {
			int ret = compare(rOperand, lOperand);
			return reverse(ret);
		}
		throw new IllegalArgumentException("Parameter 'lOperand' and 'rOperand' can not compare each other!");
	}

	protected static int compare(Comparable lOperand, Object rOperand) {
		if (lOperand instanceof String) {
			return lOperand.compareTo(rOperand.toString());
		}
		return lOperand.compareTo(rOperand);
	}
	
	protected static int compareNumber(Number lOperand, Object rOperand) {
		double lValue = lOperand.doubleValue();
		double rValue = 0;
		if (rOperand instanceof Number) {
			rValue = ((Number)rOperand).doubleValue();
		} else if (rOperand instanceof String) {
			rValue = (Double.valueOf((String)rOperand)).doubleValue();
		} else if (rOperand instanceof NumberConvertable) {
			rValue = ((NumberConvertable)rOperand).toNumber().doubleValue();
		} else {
			
		}
		return (int)(lValue - rValue);
	}
	
	protected static int reverse(int result) {
		if (result < 0)
			return 1;
		if (result > 0)
			return -1;
		return 0;
	}
	
}
