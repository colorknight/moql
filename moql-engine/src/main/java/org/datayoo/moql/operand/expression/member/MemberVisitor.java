package org.datayoo.moql.operand.expression.member;

import java.io.Serializable;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2022/3/9 9:06 PM
 */
public interface MemberVisitor extends Serializable {

  boolean isVisitable(Object object);

  Object operate(Object object, String varName);
}
