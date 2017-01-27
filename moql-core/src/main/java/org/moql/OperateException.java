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
 * @author Taiding Tang
 *
 */
public class OperateException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private long exceptionCode;

	/**
	 * @param message
	 * @param cause
	 */
	public OperateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public OperateException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public OperateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public long getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(long exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

}
