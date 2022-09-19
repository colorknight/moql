package org.datayoo.moql.util;

import java.math.BigDecimal;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2022/8/15 1:51 PM
 */
public abstract class ArithmeticUtils {

  protected static boolean needPreciseCalculation(Number lNumber,
      Number rNumber) {
    if (lNumber instanceof BigDecimal || rNumber instanceof BigDecimal)
      return true;
    return false;
  }

  protected static BigDecimal toBigDecimal(Number number) {
    if (number instanceof BigDecimal)
      return (BigDecimal) number;
    return new BigDecimal(number.toString());
  }

  public static Number add(Number v1, Number v2) {
    if (needPreciseCalculation(v1, v2)) {
      BigDecimal n1 = toBigDecimal(v1);
      BigDecimal n2 = toBigDecimal(v2);
      return n1.add(n2);
    } else {
      return v1.doubleValue() + v2.doubleValue();
    }
  }

  public static Number subtract(Number v1, Number v2) {
    if (needPreciseCalculation(v1, v2)) {
      BigDecimal n1 = toBigDecimal(v1);
      BigDecimal n2 = toBigDecimal(v2);
      return n1.subtract(n2);
    } else {
      return v1.doubleValue() - v2.doubleValue();
    }
  }

  public static Number divide(Number v1, Number v2) {
    if (v2.doubleValue() == 0)
      return Double.NaN;
    if (needPreciseCalculation(v1, v2)) {
      BigDecimal n1 = toBigDecimal(v1);
      BigDecimal n2 = toBigDecimal(v2);
      return n1.divide(n2, 6, BigDecimal.ROUND_HALF_UP);
    } else {
      return v1.doubleValue() / v2.doubleValue();
    }
  }

  public static Number multiply(Number v1, Number v2) {
    if (needPreciseCalculation(v1, v2)) {
      BigDecimal n1 = toBigDecimal(v1);
      BigDecimal n2 = toBigDecimal(v2);
      return n1.multiply(n2);
    } else {
      return v1.doubleValue() * v2.doubleValue();
    }
  }

}
