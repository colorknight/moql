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
package org.datayoo.moql.operand.constant;

import org.apache.commons.lang3.Validate;

import java.io.UnsupportedEncodingException;

/**
 * @author Tang Tadin
 */
public class LongConstant extends AbstractConstant {

  {
    constantType = ConstantType.LONG;
  }

  public static Long ZERO = new Long(0);

  public LongConstant(String name) {
    Validate.notEmpty(name, "Parameter 'name' is empty!");
    try {
      this.name = new String(name.getBytes("utf-8"));
    } catch (UnsupportedEncodingException e) {
    }
    int radix = 10;
    if (name.charAt(0) == '+') {
      name = name.substring(1);
    }
    name = name.substring(0, name.length() - 1);
    this.data = Long.valueOf(name, radix);
  }

}
