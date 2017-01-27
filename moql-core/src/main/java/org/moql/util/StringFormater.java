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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Tang Tadin
 *
 */
public abstract class StringFormater {
	static final char PLACEHOLDER_START = '{';
	static final char PLACEHOLDER_STOP = '}';
	static final String PLACEHOLDER_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';
	
	static final char BRACKET_START = '[';
	static final char BRACKET_STOP = ']';
	static final String ELEMENT_SEPARATOR = ", ";

	/**
	 * 
	 * @param stringPattern	The string pattern which will be parsed and formatted!
	 *                The string "{}" is formatting anchor. 
	 * @param argArray	An array of arguments to be substituted in place of
	 *                formatting anchors
	 * @return The formatted message
	 */
	final public static String format(final String stringPattern,
			final Object... argArray) {
		if (stringPattern == null) {
			return null;
		}
		if (argArray == null) {
			return stringPattern;
		}
		int offset = 0;
		int index;
		StringBuffer sbuf = new StringBuffer(stringPattern.length() + 50);

		for (int i = 0; i < argArray.length; i++) {

			index = stringPattern.indexOf(PLACEHOLDER_STR, offset);

			if (index != -1) {
				if (index > 0 
						&& stringPattern.charAt(index - 1) == ESCAPE_CHAR) {
					if (!(index > 1 
							&& stringPattern.charAt(index - 1) == ESCAPE_CHAR)) {
						i--; // PLACEHOLDER_START was escaped, thus should not be incremented
						sbuf.append(stringPattern.substring(offset, index - 1));
						sbuf.append(PLACEHOLDER_START);
						offset = index + 1;
					} else {
						// The escape character preceding the delemiter start is
						// itself escaped: "abc x:\\{}"
						// we have to consume one backward slash
						sbuf.append(stringPattern.substring(offset, index - 1));
						deeplyAppendParameter(sbuf, argArray[i],
								new HashMap<Object, Object>());
						offset = index + 2;
					}
				} else {
					// normal case
					sbuf.append(stringPattern.substring(offset, index));
					deeplyAppendParameter(sbuf, argArray[i],
							new HashMap<Object, Object>());
					offset = index + 2;
				}
			} else {
				// no more variables
				if (offset == 0) { // this is a simple string
					return stringPattern;
				} else { // add the tail string which contains no variables and return
					// the result.
					sbuf.append(stringPattern.substring(offset, stringPattern.length()));
					return sbuf.toString();
				}
			}
		}
		// append the characters following the last {} pair.
		sbuf.append(stringPattern.substring(offset, stringPattern.length()));
		return sbuf.toString();
	}

	private static void deeplyAppendParameter(StringBuffer sbuf, Object o,
			Map<Object, Object> seenMap) {
		if (o == null) {
			sbuf.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			sbuf.append(o);
		} else {
			// check for primitive array types
			if (o instanceof boolean[]) {
				appendBooleanArray(sbuf, (boolean[]) o);
			} else if (o instanceof byte[]) {
				appendByteArray(sbuf, (byte[]) o);
			} else if (o instanceof char[]) {
				appendCharArray(sbuf, (char[]) o);
			} else if (o instanceof short[]) {
				appendShortArray(sbuf, (short[]) o);
			} else if (o instanceof int[]) {
				appendIntArray(sbuf, (int[]) o);
			} else if (o instanceof long[]) {
				appendLongArray(sbuf, (long[]) o);
			} else if (o instanceof float[]) {
				appendFloatArray(sbuf, (float[]) o);
			} else if (o instanceof double[]) {
				appendDoubleArray(sbuf, (double[]) o);
			} else {
				appendObjectArray(sbuf, (Object[]) o, seenMap);
			}
		}
	}

	private static void appendObjectArray(StringBuffer sbuf, Object[] a,
			Map<Object, Object> seenMap) {
		sbuf.append(BRACKET_START);
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				deeplyAppendParameter(sbuf, a[i], seenMap);
				if (i != len - 1)
					sbuf.append(ELEMENT_SEPARATOR);
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			sbuf.append("...");
		}
		sbuf.append(BRACKET_STOP);	//	break cycle dependency
	}

	private static void appendBooleanArray(StringBuffer sbuf, boolean[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendByteArray(StringBuffer sbuf, byte[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendCharArray(StringBuffer sbuf, char[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendShortArray(StringBuffer sbuf, short[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendIntArray(StringBuffer sbuf, int[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendLongArray(StringBuffer sbuf, long[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendFloatArray(StringBuffer sbuf, float[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

	private static void appendDoubleArray(StringBuffer sbuf, double[] a) {
		sbuf.append(BRACKET_START);
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(ELEMENT_SEPARATOR);
		}
		sbuf.append(BRACKET_STOP);
	}

}
