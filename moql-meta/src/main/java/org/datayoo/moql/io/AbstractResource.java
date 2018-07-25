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

import org.datayoo.moql.util.ResourceUtils;
import org.datayoo.moql.util.StringFormater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 */
public abstract class AbstractResource implements Resource {

	/**
	 * This implementation checks whether a File can be opened,
	 * falling back to whether an InputStream can be opened.
	 * This will cover both directories and content resources.
	 */
	@Override
	public boolean exists() {
		// Try file existence: can we find the file in the file system?
		try {
			return getFile().exists();
		} catch (IOException ex) {
			// Fall back to stream existence: can we open the stream?
			try {
				InputStream is = getInputStream();
				is.close();
				return true;
			} catch (Throwable isEx) {
				return false;
			}
		}
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	/**
	 * builds a URI based on the URL returned by {@link #getURL()}.
	 */
	@Override
	public URI getURI() throws IOException {
		URL url = getURL();
		try {
		    return ResourceUtils.toURI(url);
		} catch (URISyntaxException ex) {
		    IOException e = new IOException(
            StringFormater.format("Invalid URI '{}'!", url));
		    e.initCause(ex);
		    throw e;
		}
	}

	/**
	 * checks the last modified time of the underlying File
	 */
	@Override
	public long lastModified() throws IOException {
		long lastModified = getFile2Check().lastModified();
		if (lastModified == 0L) {
			throw new FileNotFoundException(StringFormater.format(
					"'{}' cannot be resolved in the file system for resolving its last-modified timestamp"
					, getResourceLocation()));
		}
		return lastModified;
	}

	/**
	 * Determine the File to use for timestamp checking.
	 * @return the File to use for timestamp checking (never <code>null</code>)
	 * @throws IOException if the resource cannot be resolved as absolute
	 * file path, i.e. if the resource is not available in a file system
	 */
	protected abstract File getFile2Check() throws IOException;

	@Override
	public String toString() {
		return getResourceLocation();
	}

}
