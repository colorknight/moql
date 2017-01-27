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
package org.moql.xml;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.moql.io.DefaultResourceLoader;
import org.moql.io.Resource;
import org.moql.io.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author Tang Tadin
 */
public class DefaultDocumentFormater<T> implements XmlDocumentFormater <T>{
	private static final Logger logger = LoggerFactory
			.getLogger(DefaultDocumentFormater.class);
	private XmlElementFormater<T> formater;
	
	private String encoding = "UTF-8";
	
	private boolean validation = false;
	
	public void exportObjectToFile(String fileName, T object) throws XmlAccessException {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.debug("Export object '{}' to file '{}'!", formater
					.getClass().getCanonicalName(), fileName);
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(fileName);
		OutputStream fos = null;
		try {
			fos = resource.getOutputStream();
			exportObject(fos, object);
			logger.debug("Export object successed!");
		} catch (XmlAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new XmlAccessException(e);
		} finally {
			try {
				if (fos != null)
					fos.close();
				//resource.getOutputStream().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new XmlAccessException(e);
			}
		}
		logger.debug("Export object successed!");
	}

	public String exportObjectToString(T object) throws XmlAccessException {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.debug("Export object '{}'!", formater.getClass()
					.getCanonicalName());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			exportObject(bos, object);
			logger.debug("Export object successed!");
			return bos.toString();
		} catch (XmlAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new XmlAccessException(e);
		} finally {
			try {
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new XmlAccessException(e);
			}
		}
	}

	public void exportObject(OutputStream os, T object) throws XmlAccessException{
		OutputFormat format = OutputFormat.createPrettyPrint();
		if (encoding != null)
			format.setEncoding(encoding);
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(os, format);
			Element element = formater.exportObjectToElement(null, object);
			writer.write(element.getDocument());
		} catch (Exception e) {
			throw new XmlAccessException(e);
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new XmlAccessException(e);
			}
		}
	}

	public T importObjectFromFile(String fileName) throws XmlAccessException {
		if (logger.isDebugEnabled())
			logger.debug("Import object '{}' from file '{}'!", formater
					.getClass().getCanonicalName(), fileName);
		// TODO Auto-generated method stub
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(fileName);
		InputStream fis = null;
		try {
			fis = resource.getInputStream();
			return importObject(fis);
		} catch (XmlAccessException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new XmlAccessException(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new XmlAccessException(e);
			}
			if (fis != null)
				logger.debug("Import object successed!");
		}
	}

	public T importObjectFromString(String xmlData)
			throws XmlAccessException {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.debug("Import object '{}' from string!", formater.getClass()
					.getCanonicalName());
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(xmlData.getBytes());
		} catch (Exception e) {
			throw new XmlAccessException(e.getMessage(), e);
		}
		try {
			return importObject(bis);
		} catch (XmlAccessException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new XmlAccessException(e);
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new XmlAccessException(e);
			}
			if (bis != null)
				logger.debug("Import object successed!");
		}
	}

	public T importObject(InputStream is) throws XmlAccessException {
		SAXReader reader = new SAXReader();
		if (encoding != null)
			reader.setEncoding(encoding);
		Document document;
		try {
			if (validation) {
				// specify the schema to use
		        reader.setValidation(true);
		        reader.setFeature("http://apache.org/xml/features/validation/schema",true);
		        // add error handler which turns any errors into XML
		        ErrorChecker checker = new ErrorChecker();
		        reader.setErrorHandler(checker);
			}
			document = reader.read(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new XmlAccessException("Read failed!", e);
		}
		return formater.importObjectFromElement(document.getRootElement());
	}

	/**
	 * @return the formater
	 */
	public XmlElementFormater<T> getFormater() {
		return formater;
	}

	/**
	 * @param formater the formater to set
	 */
	public void setFormater(XmlElementFormater<T> formater) {
		this.formater = formater;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * @return the validation
	 */
	public boolean isValidation() {
		return validation;
	}

	/**
	 * @param validation the validation to set
	 */
	public void setValidation(boolean validation) {
		this.validation = validation;
	}
}
