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
package org.datayoo.moql.data;

import org.apache.commons.lang.Validate;
import org.datayoo.moql.SelectorConstants;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Tang Tadin
 */
public class ColumnFeature implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  protected String name;
  
  protected FeatureDecisionor featureDecisionor;

  protected Class<?> type;

  protected Map<Object, Counter> valueCounters = new HashMap<Object, Counter>();

  protected int count = 0;
  
  protected boolean dimension = true;
  
  public ColumnFeature(String name, FeatureDecisionor featureDecisionor) {
    Validate.notEmpty(name, "name is empty!");
    Validate.notNull(featureDecisionor, "featureDecisionor is empty!");
    this.name = name;
    this.featureDecisionor = featureDecisionor;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public synchronized Map<Object, Counter> getValueCounters() {
    return valueCounters;
  }
  
  public synchronized Set<Object> getValues() {
    return valueCounters.keySet();
  }

  public synchronized void addValue(Object value) {
    if (!dimension)
      return;
    if (value == null) {
      value = SelectorConstants.NULL;
    } else {
      setType(value);
    }
    Counter counter = valueCounters.get(value);
    if (counter == null) {
      counter = new Counter();
      counter.increment();
      valueCounters.put(value, counter);
    } else {
      counter.increment();
    }
    count++;
    if (featureDecisionor.isReady(count)) {
      dimension = featureDecisionor.isDimension(getRatioOfVR());
    }
  }
  /**
   * the ratio of values and rows
   * @return
   */
  public double getRatioOfVR() {
    return (double)valueCounters.size() / count;
  }
  
  protected void setType(Object value) {
    if (type != null && type != value.getClass()) {
      value.getClass().isAssignableFrom(type);
    } else {
      type = value.getClass();      
    }
  }
  
  public boolean isDimension() {
    return dimension;
  }

}
