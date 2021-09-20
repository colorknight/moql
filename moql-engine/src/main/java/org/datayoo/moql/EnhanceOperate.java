package org.datayoo.moql;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2021/9/20 12:58 PM
 */
public interface EnhanceOperate {
  /**
   * bind the index of entity
   */
  void bind(String[] entityNames);

  /**
   * whether the operand binded the index
   */
  boolean isBinded();

  /**
   * get or caculate the value from the given entity array. The entity array
   * is an array of entities. The order of entity is samed as entityNames inputed
   * in function bind.
   */
  Object operate(Object[] entityArray);

}
