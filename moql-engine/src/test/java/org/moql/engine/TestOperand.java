package org.moql.engine;

import com.google.gson.JsonParser;
import junit.framework.TestCase;
import org.moql.EntityMap;
import org.moql.EntityMapImpl;
import org.moql.MoqlException;
import org.moql.Operand;
import org.moql.simulation.BeanA;
import org.moql.simulation.BeanFactory;

import java.util.List;

public class TestOperand extends TestCase {

  public void testConstant() {
    try {
      Operand constant = MoqlEngine.createOperand("1234");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("192.16");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("'中国''china'");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("0-4");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("null");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("Null");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("-2");
      System.out.println(constant.toString() + " " + constant.getOperandType());
      constant = MoqlEngine.createOperand("-2.3");
      System.out.println(constant.toString() + " " + constant.getOperandType());
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
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
      System.out.println(variable.toString() + " " + variable.getOperandType());
      System.out.println(variable.operate(entityMap));
      variable = MoqlEngine.createOperand("长度");
      System.out.println(variable.toString() + " " + variable.getOperandType());
      System.out.println(variable.operate(entityMap));
      variable = MoqlEngine.createOperand("$a");
      System.out.println(variable.toString() + " " + variable.getOperandType());
      System.out.println(variable.operate(entityMap));
      variable = MoqlEngine.createOperand("_data");
      System.out.println(variable.toString() + " " + variable.getOperandType());
      System.out.println(variable.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testFunction() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 123);
    try {
      Operand function = MoqlEngine.createOperand("max(num)");
      System.out.println(function.toString() + " " + function.getOperandType());
      System.out.println(function.operate(entityMap));
      entityMap.putEntity("num", 345);
      System.out.println(function.operate(entityMap));
      function.clear();
      entityMap.putEntity("num", 12);
      System.out.println(function.operate(entityMap));
      function = MoqlEngine.createOperand("test(1, num, 'a')");
      System.out.println(function.toString() + " " + function.getOperandType());
      function = MoqlEngine.createOperand("cast('1978-04-18' as Timestamp)");
      System.out.println(function.toString() + " " + function.getOperandType());
      function = MoqlEngine.createOperand("percent(13/15,2)");
      System.out.println(function.toString() + " " + function.getOperandType());
      function = MoqlEngine.createOperand("trunc(13/15,2)");
      System.out.println(function.toString() + " " + function.getOperandType());
      System.out.println(function.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testArithmeticExpression() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 12);
    entityMap.putEntity("num1", 3);
    entityMap.putEntity("num2", 4);
    try {
      Operand arithmetic = MoqlEngine
          .createOperand("(num*num1) / num2 * 2.2 + 2 - 1");
      Operand arithmetic2 = MoqlEngine
          .createOperand("(经济|文化) + (十八大|中国) + 美国 - 病闹");
      System.out.println(arithmetic2.toString() + " "
          + arithmetic.getOperandType());
      System.out.println(arithmetic.toString() + " "
          + arithmetic.getOperandType());
      System.out.println(arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("num + 21");
      System.out.println(arithmetic.toString() + " "
          + arithmetic.getOperandType());
      System.out.println(arithmetic.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testMemberExpression() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("bean", new BeanA("bean", 5));
    try {
      Operand member = MoqlEngine.createOperand("bean.name");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean.getNum()");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
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
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean[]");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean[4]['bean'].num");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean[4]['bean'].getArray()[5]");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public void testOperandsExpression() {
    try {
      Operand operand = MoqlEngine.createOperand("(1, 2, 3)");
      System.out.println(operand.toString() + " " + operand.getOperandType());
      List<Operand> operands = (List<Operand>) operand.operate(null);
      System.out.println(operands.size());
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testJsonExpression() {
    EntityMap entityMap = new EntityMapImpl();
    String json = "{\"obj\":{\"name\":\"test\";\"value\":[1,2,3]}}";
    JsonParser parser = new JsonParser();
    entityMap.putEntity("j",parser.parse(json));
    try {
      Operand member = MoqlEngine.createOperand("j.obj.name");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("j.obj.value[2]");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testRangeExpression() {
    try {
      Operand operand = MoqlEngine.createOperand("[1, 2}");
      System.out.println(operand.toString() + " " + operand.getOperandType());
      List<Operand> operands = (List<Operand>) operand.operate(null);
      System.out.println(operands.size());
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
