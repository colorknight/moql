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
package org.moql.function.factory;

import org.apache.commons.lang.Validate;
import org.moql.MoqlRuntimeException;
import org.moql.Operand;
import org.moql.operand.function.*;
import org.moql.operand.function.decorator.GroupOrdinal;
import org.moql.operand.function.decorator.OtherCaculation;
import org.moql.operand.function.decorator.RowTransposition;
import org.moql.operand.function.decorator.TotalCaculation;
import org.moql.operand.function.factory.FunctionFactory;
import org.moql.util.StringFormater;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public class FunctionFactoryImpl
    implements org.moql.operand.function.factory.FunctionFactory {

  protected Map<String, FunctionBean> functionMap = new HashMap<String, FunctionBean>();

  protected static org.moql.operand.function.factory.FunctionFactory functionFactory;

  {
    functionMap.put(Count.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Count.FUNCTION_NAME, Count.class.getName(), true));
    functionMap.put(Avg.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Avg.FUNCTION_NAME, Avg.class.getName(), true));
    functionMap.put(Min.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Min.FUNCTION_NAME, Min.class.getName(), true));
    functionMap.put(Max.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Max.FUNCTION_NAME, Max.class.getName(), true));
    functionMap.put(Sum.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Sum.FUNCTION_NAME, Sum.class.getName(), true));
    functionMap.put(NotNull.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(NotNull.FUNCTION_NAME, NotNull.class.getName(), true));
    functionMap.put(Percentile.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Percentile.FUNCTION_NAME, Percentile.class.getName(),
            true));
    functionMap.put(Median.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Median.FUNCTION_NAME, Median.class.getName(), true));
    functionMap.put(Mode.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Mode.FUNCTION_NAME, Mode.class.getName(), true));
    functionMap.put(Range.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Range.FUNCTION_NAME, Range.class.getName(), true));
    functionMap.put(Kurtosis.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Kurtosis.FUNCTION_NAME, Kurtosis.class.getName(),
            true));
    functionMap.put(Skewness.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Skewness.FUNCTION_NAME, Skewness.class.getName(),
            true));
    functionMap.put(Variance.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Variance.FUNCTION_NAME, Variance.class.getName(),
            true));
    functionMap.put(StandardDeviation.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(StandardDeviation.FUNCTION_NAME,
            StandardDeviation.class.getName(), true));
    functionMap.put(SemiVariance.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(SemiVariance.FUNCTION_NAME,
            SemiVariance.class.getName(), true));
    functionMap.put(Regex.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Regex.FUNCTION_NAME, Regex.class.getName(), true));
    functionMap.put(Percent.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Percent.FUNCTION_NAME, Percent.class.getName(), true));
    functionMap.put(Ceil.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Ceil.FUNCTION_NAME, Ceil.class.getName(), true));
    functionMap.put(Floor.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Floor.FUNCTION_NAME, Floor.class.getName(), true));
    functionMap.put(Trunc.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(Trunc.FUNCTION_NAME, Trunc.class.getName(), true));
    functionMap.put(GroupOrdinal.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(GroupOrdinal.FUNCTION_NAME,
            GroupOrdinal.class.getName(), true));
    functionMap.put(RowTransposition.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(RowTransposition.FUNCTION_NAME,
            RowTransposition.class.getName(), true));
    functionMap.put(TotalCaculation.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(TotalCaculation.FUNCTION_NAME,
            TotalCaculation.class.getName(), true));
    functionMap.put(OtherCaculation.FUNCTION_NAME.toLowerCase(),
        new FunctionBean(OtherCaculation.FUNCTION_NAME,
            OtherCaculation.class.getName(), true));

  }

  protected FunctionFactoryImpl() {
  }

  public static synchronized FunctionFactory createFunctionFactory() {
    if (functionFactory == null) {
      functionFactory = org.moql.operand.function.factory.FunctionFactoryImpl
          .createFunctionFactory();
    }
    return functionFactory;
  }

  @Override
  public Function createFunction(String name, List<Operand> parameters) {
    // TODO Auto-generated method stub
    Validate.notEmpty(name, "Parameter name is empty!");
    FunctionBean bean = functionMap.get(name.toLowerCase());
    if (bean == null) {
      //
      return new MemberFunction(name, parameters);
    }
    Function func;
    try {
      func = (Function) bean.getCstr().newInstance(new Object[] { parameters
      });
    } catch (Exception e) {
      // TODO Auto-generated catch block
      throw new MoqlRuntimeException(
          StringFormater.format("Create function '{}' failed!", name), e);
    }
    return func;
  }

  @Override public void importFunction(InputStream is) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("");
  }

  @Override public String registFunction(String name, String className) {
    // TODO Auto-generated method stub
    Validate.notEmpty(name, "Parameter name is empty!");
    Validate.notEmpty(name, "Parameter className is empty!");
    name = name.toLowerCase();
    FunctionBean bean = functionMap.get(name);
    if (bean != null) {
      if (bean.isReadonly())
        return null;
      functionMap.put(name, new FunctionBean(name, className, false));
      return bean.getClassName();
    } else {
      functionMap.put(name, new FunctionBean(name, className, false));
    }
    return null;
  }

  @Override public String unregistFunction(String name) {
    // TODO Auto-generated method stub
    Validate.notEmpty(name, "Parameter name is empty!");
    FunctionBean bean = functionMap.get(name);
    if (bean != null) {
      if (bean.isReadonly())
        return null;
      return bean.getClassName();
    }
    return null;
  }

  static class FunctionBean {
    private String name;
    private String className;
    private boolean readonly = false;
    private Constructor<?> cstr;

    public FunctionBean(String name, String className, boolean readonly) {
      this.name = name;
      this.className = className;
      this.readonly = readonly;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /**
     * @return the className
     */
    public String getClassName() {
      return className;
    }

    /**
     * @return the readonly
     */
    public boolean isReadonly() {
      return readonly;
    }

    /**
     * @return the cstr
     */
    public synchronized Constructor<?> getCstr() {
      if (cstr == null) {
        try {
          Class<?> clazz = (Class<?>) this.getClass().getClassLoader()
              .loadClass(className);
          cstr = clazz.getConstructor(new Class<?>[] { List.class
          });
        } catch (Exception e) {
          // TODO Auto-generated catch block
          throw new MoqlRuntimeException(StringFormater
              .format("Load function '{}' class = '{}' failed!", name,
                  className), e);
        }
      }
      return cstr;
    }

  }
}
