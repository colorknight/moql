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

import org.apache.commons.lang.Validate;
import org.datayoo.moql.metadata.ConditionMetadata;
import org.datayoo.moql.util.StringFormater;
import org.datayoo.moql.xml.AbstractElementFormater;
import org.datayoo.moql.xml.XmlAccessException;
import org.dom4j.Element;

/**
 * 
 * @author Tang Tadin
 *
 */
public class FilterFormater extends AbstractElementFormater<ConditionMetadata> {
	
	protected XmlMetadataHelper metadataHelper = new XmlMetadataHelper();
	
	{
		supportElementNames = new String[] {
			XmlMetadataHelper.FILTER_ELEMENT,
		};
	}
	
	@Override
	public Element exportObjectToElement(Element element, ConditionMetadata condition)
			throws XmlAccessException {
		// TODO Auto-generated method stub
		return metadataHelper.writeFilterMetadata(element, condition);
	}

	@Override
	public ConditionMetadata importObjectFromElement(Element element)
			throws XmlAccessException {
		// TODO Auto-generated method stub
		if (!canImport(element)) {
			throw new XmlAccessException(
					StringFormater
              .format("Can't import object from '{}' element!", element.getName()));
		}
		return metadataHelper.readConditionMetadata(element);
	}

	public XmlMetadataHelper getMetadataHelper() {
		return metadataHelper;
	}

	public void setMetadataHelper(XmlMetadataHelper metadataHelper) {
		Validate.notNull(metadataHelper, "Parameter 'metadataHelper' is null!");
		this.metadataHelper = metadataHelper;
	}

}
