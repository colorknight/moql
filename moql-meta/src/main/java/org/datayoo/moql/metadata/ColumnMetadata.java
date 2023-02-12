/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.metadata;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.ColumnDefinition;
import org.datayoo.moql.SelectorDefinition;

import java.io.Serializable;

/**
 * @author Tang Tadin
 */
public class ColumnMetadata implements ColumnDefinition, Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  protected String name;

  protected String value;

  protected SelectorDefinition nestedSelector;

  protected CaseMetadata caseMetadata;

  public ColumnMetadata(String name, String value) {
    Validate.notEmpty(name, "Parameter 'name' is empty!");
    Validate.notEmpty(value, "Parameter 'value' is empty!");
    if (name.indexOf('(') != -1) {
      int index = name.indexOf(',');
      if (index != -1) {
        name = name.substring(0, index);
        name += ')';
      }
      name = name.replace(" ", "");
      name = name.replace('(', '$');
      name = name.replace(')', '$');
      name = name.replace('.', '_');
    }

    this.name = name;
    this.value = value;
  }

  public ColumnMetadata(String name, SelectorDefinition nestedSelector) {
    Validate.notEmpty(name, "Parameter 'name' is empty!");
    Validate.notNull(nestedSelector, "Parameter 'nestedSelector' is empty!");
    SelectorValidator.isValidateNestedColumnSelector(nestedSelector);
    this.name = name;
    this.nestedSelector = nestedSelector;
    this.value = "";  //pending... nestedColumnSelector.toString();
  }

  public ColumnMetadata(String name, CaseMetadata caseMetadata) {
    Validate.notEmpty(name, "Parameter 'name' is empty!");
    Validate.notNull(caseMetadata, "Parameter 'caseMetadata' is empty!");
    this.name = name;
    this.caseMetadata = caseMetadata;
  }

  public boolean isHasAlias() {
    return !name.equals(value);
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @return the selectorMetadata
   */
  public SelectorDefinition getNestedSelector() {
    return nestedSelector;
  }

  public CaseMetadata getCaseMetadata() {
    return caseMetadata;
  }
}
