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
package org.moql.metadata.xml;

import java.io.InputStream;
import java.io.OutputStream;

import org.moql.SelectorDefinition;
import org.moql.metadata.ConditionMetadata;
import org.moql.xml.DefaultDocumentFormater;
import org.moql.xml.XmlAccessException;
import org.moql.xml.XmlDocumentFormater;

/**
 * 
 * @author Tang Tadin
 *
 */
public class XmlMetadataAccessor {
	
	protected XmlMetadataHelper metadataHelper = new XmlMetadataHelper();
	
	protected boolean validation = true;

    public SelectorDefinition importSelectorDefinition(InputStream is) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	return documentFormater.importObject(is);
    }
    
    public SelectorDefinition importSelectorDefinitionFromFile(String fileName) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	return documentFormater.importObjectFromFile(fileName);
    }
    
    public SelectorDefinition importSelectorDefinitionFromString(String xmlData) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	return documentFormater.importObjectFromString(xmlData);
    }
    
    public void exportSelectorDefinition(OutputStream os, SelectorDefinition selector) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	documentFormater.exportObject(os, selector);
    }
    
    public void exportSelectorsMetadataToFile(String fileName, SelectorDefinition selector) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	documentFormater.exportObjectToFile(fileName, selector);
    }
    
    public String exportSelectorsMetadataToString(SelectorDefinition selectors) throws XmlAccessException {
    	XmlDocumentFormater<SelectorDefinition> documentFormater = buildSelectorsDocumentFormater();
    	return documentFormater.exportObjectToString(selectors);
    }
    
    protected XmlDocumentFormater<SelectorDefinition> buildSelectorsDocumentFormater() {
    	DefaultDocumentFormater<SelectorDefinition> documentFormater = new DefaultDocumentFormater<SelectorDefinition>();
    	documentFormater.setValidation(validation);
    	SelectorFormater selectorFormater = new SelectorFormater();
    	selectorFormater.setMetadataHelper(metadataHelper);
    	documentFormater.setFormater(selectorFormater);
    	return documentFormater;
    }
    
    public ConditionMetadata importConditionMetadata(InputStream is) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	return documentFormater.importObject(is);
    }
    
    public ConditionMetadata importConditionMetadataFromFile(String fileName) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	return documentFormater.importObjectFromFile(fileName);
    }
    
    public ConditionMetadata importConditionMetadataFromString(String xmlData) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	return documentFormater.importObjectFromString(xmlData);
    }
    
    public void exportConditionMetadata(OutputStream os, ConditionMetadata selector) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	documentFormater.exportObject(os, selector);
    }
    
    public void exportConditionMetadataToFile(String fileName, ConditionMetadata selector) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	documentFormater.exportObjectToFile(fileName, selector);
    }
    
    public String exportConditionMetadataToString(ConditionMetadata selector) throws XmlAccessException {
    	XmlDocumentFormater<ConditionMetadata> documentFormater = buildConditionDocumentFormater();
    	return documentFormater.exportObjectToString(selector);
    }
    
    protected XmlDocumentFormater<ConditionMetadata> buildConditionDocumentFormater() {
    	DefaultDocumentFormater<ConditionMetadata> documentFormater = new DefaultDocumentFormater<ConditionMetadata>();
    	documentFormater.setValidation(validation);
    	FilterFormater conditionFormater = new FilterFormater();
    	conditionFormater.setMetadataHelper(metadataHelper);
    	documentFormater.setFormater(conditionFormater);
    	return documentFormater;
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
