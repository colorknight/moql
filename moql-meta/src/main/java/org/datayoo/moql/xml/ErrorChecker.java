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
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Tang Tadin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ErrorChecker extends DefaultHandler {
	
    public void startElement(String uri,   String localName,
            String qName, Attributes attributes)  
            throws SAXException {

        System.out.println("tag: " + qName);
    }
    
	public void warning(SAXParseException e) throws SAXException {
		// you can choose not to handle it
	    throw new SAXException(getMessage("Warning", e));
	}
	
	public void error(SAXParseException e) throws SAXException {
	    throw new SAXException(getMessage("Error", e));
	}
	
	public void fatalError(SAXParseException e) throws SAXException {
		throw new SAXException(getMessage("Fatal Error", e));
	}
	
	private String getMessage(String level, SAXParseException e) {
		return StringFormater.format("Parsing {} \nLine: {}\nURI: {}\nMessage: {}",
				level, e.getLineNumber(), e.getSystemId(), e.getMessage());
	}

}
