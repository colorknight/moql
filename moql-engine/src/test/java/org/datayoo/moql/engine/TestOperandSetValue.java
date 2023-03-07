package org.datayoo.moql.engine;

import junit.framework.TestCase;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.simulation.BeanA;
import org.datayoo.moql.simulation.BeanFactory;

public class TestOperandSetValue extends TestCase {

  public void testConstant() {
    try {
      Operand constant = MoqlEngine.createOperand("1234");
      constant.setValue((EntityMap) null, "a");
      System.out.println(constant.toString());
    } catch (UnsupportedOperationException e) {
      System.out.println("That's ok!");
    } catch (MoqlException e) {
      e.printStackTrace();
    }
  }

  public void testVariable() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 123);
    entityMap.putEntity("长度", 184);
    entityMap.putEntity("$a", 38);
    entityMap.putEntity("_data", 32);
    try {
      Operand variable = MoqlEngine.createOperand("num");
      variable.setValue(entityMap, 456);
      System.out.println(variable.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testEnhanceVariable() {
    String[] names = new String[] { "num", "长度", "$a", "_data" };
    Object[] data = new Object[] { 123, 184, 38, 32 };
    try {
      Operand variable = MoqlEngine.createOperand("num");
      variable.bind(names);
      variable.setValue(data, 456);
      System.out.println(variable.operate(data));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testMemberExpression() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("bean", new BeanA("bean", 5));
    entityMap.putEntity("ary", new String[] { "1", "2" });
    try {
      Operand member = MoqlEngine.createOperand("bean.name");
      System.out.println(member.operate(entityMap));
      member.setValue(entityMap, "bean2");
      System.out.print("Changed :");
      System.out.println(member.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testEhanceMemberExpression() {
    String[] names = new String[] { "bean", "ary" };
    Object[] data = new Object[] { new BeanA("bean", 5),
        new String[] { "1", "2" }
    };
    try {
      Operand member = MoqlEngine.createOperand("bean.name");
      member.bind(names);
      System.out.println(member.operate(data));
      member.setValue(data, "bean2");
      System.out.print("Changed :");
      System.out.println(member.operate(data));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testArrayExpression() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("bean", BeanFactory.createMapList(5));
    try {
      Operand member = MoqlEngine.createOperand("bean[2]['value']");
      System.out.println(member.operate(entityMap));
      member.setValue(entityMap, 2);
      System.out.print("Changed :");
      System.out.println(member.operate(entityMap));
      System.out.println("--------------------");
      member = MoqlEngine.createOperand("bean[4]['bean'].num");
      System.out.println(member.operate(entityMap));
      member.setValue(entityMap, 20);
      System.out.print("Changed :");
      System.out.println(member.operate(entityMap));
      System.out.println("--------------------");
      member = MoqlEngine.createOperand("bean[4]['bean'].getArray()[5]");
      System.out.println(member.operate(entityMap));
      member.setValue(entityMap, "10");
      System.out.print("Changed :");
      System.out.println(member.operate(entityMap));
      System.out.println("--------------------");
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testEhanceArrayExpression() {
    String[] names = new String[] { "bean" };
    Object[] data = new Object[] { BeanFactory.createMapList(5)
    };
    try {
      Operand member = MoqlEngine.createOperand("bean[2]['value']");
      member.bind(names);
      System.out.println(member.operate(data));
      member.setValue(data, 2);
      System.out.print("Changed :");
      System.out.println(member.operate(data));
      System.out.println("--------------------");
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
