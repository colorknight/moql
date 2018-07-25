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

package org.datayoo.moql.io;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class FileSystemResource extends AbstractResource {

	private final File file;

	/**
	 * Create a new FileSystemResource from a File handle.
	 * @param file a File handle
	 */
	public FileSystemResource(File file) {
		this.file = file;
	}

	/**
	 * Create a new FileSystemResource from a file path.
	 * @param path a file path
	 */
	public FileSystemResource(String path) {
		this.file = new File(path);
	}

	/**
	 * whether the underlying file exists.
	 */
	public boolean exists() {
		return this.file.exists();
	}

	/**
	 * whether the underlying file is marked as readable
	 */
	public boolean isReadable() {
		return (this.file.canRead() && !this.file.isDirectory());
	}

	/**
	 * Opens a FileInputStream for the underlying file.
	 */
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(this.file);
	}

	/**
	 * returns a URL for the underlying file.
	 */
	public URL getURL() throws IOException {
		return this.file.toURI().toURL();
	}

	/**
	 * returns a URI for the underlying file.
	 */
	public URI getURI() throws IOException {
		return this.file.toURI();
	}

	/**
	 * returns the underlying File reference.
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * returns the name of the file.
	 */
	@Override
	public String getResourceName() {
		return this.file.getName();
	}

	/**
	 * returns the absolute path of the file.
	 */
	@Override
	public String getResourceLocation() {
		return this.file.getAbsolutePath();
	}

	/* (non-Javadoc)
	 * @see org.moql.io.AbstractResource#getFile2Check()
	 */
	@Override
	protected File getFile2Check() throws IOException {
		// TODO Auto-generated method stub
		return this.file;
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof FileSystemResource) {
			FileSystemResource fsr = (FileSystemResource)obj;
			return fsr.file.equals(file);
		}
		return false;
	}

	public int hashCode() {
		return this.file.hashCode();
	}

	public OutputStream getOutputStream() throws IOException {
	    // TODO Auto-generated method stub
	    return new FileOutputStream(this.file);
	}

}
