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

import org.apache.commons.lang3.Validate;

/**
 * @author Tang Tadin
 */
public class GeneralFeatureDecisionor implements FeatureDecisionor {

  public static final int DEFAULT_FREQUENCY = 1000;

  public static final double DEFAULT_RATIO = 0.2;

  protected int frequency = DEFAULT_FREQUENCY;

  protected double ratio = DEFAULT_RATIO;

  public GeneralFeatureDecisionor() {
  }

  public GeneralFeatureDecisionor(int frequency, double ratio) {
    Validate.isTrue(frequency > 0, "frequency should be bigger than 10!");
    Validate.isTrue(ratio > 0 && ratio < 1,
        "ratio should be bigger than 0 and less than 1!");
    this.frequency = frequency;
    this.ratio = ratio;
  }

  @Override
  public boolean isReady(int rowCount) {
    // TODO Auto-generated method stub
    if (rowCount % frequency == 0)
      return true;
    return false;
  }

  @Override
  public boolean isDimension(double ratioOfVR) {
    // TODO Auto-generated method stub
    Validate.isTrue(ratio > 0 && ratio < 1,
        "ratioOfVR should be bigger than 0 and less than 1!");
    if (ratioOfVR > ratio)
      return false;
    return true;
  }

}
