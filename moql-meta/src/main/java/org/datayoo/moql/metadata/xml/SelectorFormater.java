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
package org.datayoo.moql.metadata.xml;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.util.StringFormater;
import org.datayoo.moql.xml.AbstractElementFormater;
import org.datayoo.moql.xml.XmlAccessException;
import org.dom4j.Element;

/**
 * 
 * @author Tang Tadin
 *
 */
public class SelectorFormater extends
    AbstractElementFormater<SelectorDefinition> {
	
	protected XmlMetadataHelper metadataHelper = new XmlMetadataHelper();
	
	{
		supportElementNames = new String[] {
			XmlMetadataHelper.SELECTOR_ELEMENT,XmlMetadataHelper.SETLECTOR_ELEMENT
		};
	}
	
	@Override
	public Element exportObjectToElement(Element element, SelectorDefinition selectorDefinition)
			throws XmlAccessException {
		// TODO Auto-generated method stub
		return metadataHelper.writeSelectorDefinition(element, selectorDefinition);
	}

	@Override
	public SelectorDefinition importObjectFromElement(Element element)
			throws XmlAccessException {
		// TODO Auto-generated method stub
		if (!canImport(element)) {
			throw new XmlAccessException(
					StringFormater
              .format("Can't import object from '{}' element!", element.getName()));
		}
		return metadataHelper.readSelectorDefinition(element);
	}

	public XmlMetadataHelper getMetadataHelper() {
		return metadataHelper;
	}

	public void setMetadataHelper(XmlMetadataHelper metadataHelper) {
		Validate.notNull(metadataHelper, "Parameter 'metadataHelper' is null!");
		this.metadataHelper = metadataHelper;
	}
}
