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
package org.moql;

/**
 * 
 * @author Tang Tadin
 *
 */
public class MoqlException extends Exception {
    private static final long serialVersionUID = 1L;
	private String exceptionCode;

	/**
	 * @param message
	 * @param cause
	 */
	public MoqlException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
		if (cause instanceof MoqlException) {
			exceptionCode = ((MoqlException) cause).exceptionCode;
		}
	}

	/**
	 * @param message
	 */
	public MoqlException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public MoqlException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
		if (cause instanceof MoqlException) {
			exceptionCode = ((MoqlException) cause).exceptionCode;
		}
	}

	public String getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
}
