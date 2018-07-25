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
package org.datayoo.moql.xml;

import org.datayoo.moql.util.StringFormater;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Tang Tadin
 */
public abstract class AbstractElementFormater<T> implements XmlElementFormater<T> {
	protected String[] supportElementNames = new String[0];

	/* (non-Javadoc)
	 * @see com.topsec.tsm.base.xml.XmlElementFormatable#canImport(org.dom4j.Element)
	 */
	public boolean canImport(Element element) {
		// TODO Auto-generated method stub
		for (int i = 0; i < supportElementNames.length; i++) {
			if (supportElementNames[i].equals(element.getName()))
				return true;
		}
		return false;
	}

	protected Element createRootElement(Element element, String rootName) {
		Element elRoot = null;
		if (element != null) {
			elRoot = element.addElement(rootName);
		} else {
			Document doc = DocumentHelper.createDocument();
			elRoot = doc.addElement(rootName);
		}
		return elRoot;
	}

	/* (non-Javadoc)
	 * @see com.topsec.tsm.base.xml.XmlElementFormatable#getSupportElementNames()
	 */
	public String[] getSupportElementNames() {
		// TODO Auto-generated method stub
		return supportElementNames;
	}

	protected String getAttribute(Element element, String attribute,
			boolean option) throws XmlAccessException {
		Attribute attr = (Attribute) element.attribute(attribute);
		if (attr != null) {
			return attr.getValue();
		}
		if (option)
			return null;
		throw new XmlAccessException(StringFormater.format(
				"There is no attribute '{}' in element '{}'!", attribute,
				element.getName()));
	}

	protected String getElementText(Element element, String textElement,
			boolean option) throws XmlAccessException {
		Element el = (Element) element.element(textElement);
		if (el != null) {
			return el.getTextTrim();
		}
		if (option)
			return null;
		throw new XmlAccessException(StringFormater.format(
				"There is no element '{}' in element '{}'!", textElement,
				element.getName()));
	}
}
