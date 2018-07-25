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

import org.datayoo.moql.util.ClassLoaderUtils;
import org.datayoo.moql.util.ResourceUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class DefaultResourceLoader implements ResourceLoader {

	private ClassLoader classLoader;

	/**
	 * Create a new DefaultResourceLoader.
	 * <p>
	 * ClassLoader access will happen using the thread context class loader at
	 * the time of this ResourceLoader's initialization.
	 */
	public DefaultResourceLoader() {
		this.classLoader = ClassLoaderUtils.getDefaultClassLoader();
	}

	/**
	 * Create a new DefaultResourceLoader.
	 * 
	 * @param classLoader
	 *            the ClassLoader to load class path resources with, or
	 *            <code>null</code> for using the thread context class loader
	 *            at the time of actual resource access
	 */
	public DefaultResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Specify the ClassLoader to load class path resources with, or
	 * <code>null</code> for using the thread context class loader at the time
	 * of actual resource access.
	 * <p>
	 * The default is that ClassLoader access will happen using the thread
	 * context class loader at the time of this ResourceLoader's initialization.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Return the ClassLoader to load class path resources with, or
	 * <code>null</code> if using the thread context class loader on actual
	 * access (applying to the thread that constructs the ClassPathResource
	 * object).
	 * <p>
	 * Will get passed to ClassPathResource's constructor for all
	 * ClassPathResource objects created by this resource loader.
	 */
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public Resource getResource(String location) {
		int index = location.indexOf(ResourceUtils.PREFIX_SEPARATOR);
		//	The location is a relative or absolute file path.
		if (index == -1 || index == 1) { 
			return new FileSystemResource(location);
		}
		if (location.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
			return new ClassPathResource(location
					.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length()),
					getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return new ClassPathResource(location, getClassLoader());
			}
		}
	}
}
