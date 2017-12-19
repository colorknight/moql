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

package org.moql.io;

import org.moql.util.PathUtils;
import org.moql.util.ResourceUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author Tang Tadin
 *
 */
public class UrlResource extends AbstractResource {

	/**
	 * Original URL, used for actual access.
	 */
	private final URL url;

	/**
	 * Canonical URL (with normalized path), used for comparisons.
	 */
	private final URL canonicalUrl;

	/**
	 * Original URI, if available; used for URI and File access.
	 */
	private final URI uri;


	/**
	 * Create a new UrlResource.
	 * @param url a URL
	 */
	public UrlResource(URL url) {
		this.url = url;
		this.canonicalUrl = getCanonicalUrl(this.url, url.toString());
		this.uri = null;
	}

	/**
	 * Create a new UrlResource.
	 * @param uri a URI
	 * @throws MalformedURLException if the given URL path is not valid
	 */
	public UrlResource(URI uri) throws MalformedURLException {
		this.url = uri.toURL();
		this.canonicalUrl = getCanonicalUrl(this.url, uri.toString());
		this.uri = uri;
	}

	/**
	 * Create a new UrlResource.
	 * @param path a URL path
	 * @throws MalformedURLException if the given URL path is not valid
	 */
	public UrlResource(String path) throws MalformedURLException {
		this.url = new URL(path);
		this.canonicalUrl = getCanonicalUrl(this.url, path);
		this.uri = null;
	}

	/**
	 * Determine a canonical URL for the given original URL.
	 * @param originalUrl the original URL
	 * @param originalPath the original URL path
	 * @return the cleaned URL
	 */
	private URL getCanonicalUrl(URL originalUrl, String originalPath) {
		try {
		    return new URL(PathUtils.toCanonicalPath(originalPath));
		} catch (MalformedURLException ex) {
			// Canonical URL path cannot be converted to URL
			// -> take original URL.
			return originalUrl;
		}
	}


	/**
	 * opens an InputStream for the given URL.
	 * It sets the "UseCaches" flag to <code>false</code>,
	 * mainly to avoid jar file locking on Windows.
	 */
	public InputStream getInputStream() throws IOException {
		if (url.getProtocol().equals(ResourceUtils.URL_PROTOCOL_FILE)) {
		    return new FileInputStream(url.getPath());
		} else {
		    URLConnection con = this.url.openConnection();
		    con.setUseCaches(false);
		    return con.getInputStream();
		}
	}

	public URL getURL() throws IOException {
		return this.url;
	}

	/**
	 * This implementation returns the underlying URI directly,
	 * if possible.
	 */
	public URI getURI() throws IOException {
		if (this.uri != null) {
		    return this.uri;
		} else {
		    return super.getURI();
		}
	}

	/**
	 * This implementation returns a File reference for the underlying URL/URI,
	 * provided that it refers to a file in the file system.
	 */
	public File getFile() throws IOException {
		if (this.uri != null) {
		    return ResourceUtils.getFile(this.uri);
		}
		else {
		    return ResourceUtils.getFile(this.url);
		}
	}

	/**
	 * determines the underlying File (or jar file, in case of a resource in a jar/zip).
	 */
	@Override
	protected File getFile2Check() throws IOException {
		if (ResourceUtils.isJarURL(this.url)) {
			URL actualUrl = ResourceUtils.extractJarFileURL(this.url);
			return ResourceUtils.getFile(actualUrl);
		}
		else {
			return getFile();
		}
	}

	/**
	 * returns the name of the file that this URL refers to.
	 */
	public String getResourceName() {
	    return new File(this.url.getFile()).getName();
	}

	public String getResourceLocation() {
		return this.url.toString();
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof UrlResource) {
			UrlResource ur = (UrlResource)obj;
			return canonicalUrl.equals(ur.canonicalUrl);
		}
		return false;
	}

	public int hashCode() {
		return this.canonicalUrl.hashCode();
	}
	
	/**
	 * opens an OutputStream for the given URL.
	 * It sets the "UseCaches" flag to <code>false</code>,
	 * mainly to avoid jar file locking on Windows.
	 */
	public OutputStream getOutputStream() throws IOException {
	    // TODO Auto-generated method stub
		if (url.getProtocol().equals(ResourceUtils.URL_PROTOCOL_FILE)) {
		    return new FileOutputStream(url.getPath());
		} else {
		    URLConnection con = this.url.openConnection();
		    con.setUseCaches(false);
		    return con.getOutputStream();
		}
	}

}
