package org.datayoo.moql.engine;

import com.google.gson.JsonParser;
import junit.framework.TestCase;
import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.simulation.BeanA;
import org.datayoo.moql.simulation.BeanFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
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
      constant = MoqlEngine.createOperand("'中国\t''ch\r\n'");
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
      variable = MoqlEngine.createOperand("_data + num");
      System.out.println(variable.toString() + " " + variable.getOperandType());
      System.out.println(variable.operate(entityMap));
      variable = MoqlEngine.createOperand("_data / num");
      System.out.println(variable.toString() + " " + variable.getOperandType());
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
      System.out.println(variable.operate(data));
      variable = MoqlEngine.createOperand("长度");
      variable.bind(names);
      System.out.println(variable.operate(data));
      variable = MoqlEngine.createOperand("$a");
      variable.bind(names);
      System.out.println(variable.operate(data));
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

  public void testEnhanceFunction() {
    String[] names = new String[] { "num" };
    Object[] data = new Object[] { 123 };
    try {
      Operand function = MoqlEngine.createOperand("max(num)");
      function.bind(names);
      System.out.println(function.operate(data));
      data = new Object[] { 345 };
      System.out.println(function.operate(data));
      function.clear();
      data = new Object[] { 12 };
      System.out.println(function.operate(data));
      function = MoqlEngine.createOperand("trunc(13/15,2)");
      System.out.println(function.operate(data));
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
      Operand arithmetic3 = MoqlEngine.createOperand("6+ 5*2");
      System.out.println(
          arithmetic3.toString() + " " + arithmetic3.operate(entityMap));
      System.out
          .println(arithmetic2.toString() + " " + arithmetic2.getOperandType());
      System.out
          .println(arithmetic.toString() + " " + arithmetic.getOperandType());
      System.out.println(arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("num + 21");
      System.out
          .println(arithmetic.toString() + " " + arithmetic.getOperandType());
      System.out.println(arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("num + num1 + num2");
      System.out
          .println(arithmetic.toString() + " " + arithmetic.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testEnhanceArithmeticExpression() {
    String[] names = new String[] { "num", "num1", "num2" };
    Object[] data = new Object[] { 12, 3, 4 };
    try {
      Operand arithmetic = MoqlEngine
          .createOperand("(num*num1) / num2 * 2.2 + 2 - 1");
      arithmetic.bind(names);
      System.out.println(arithmetic.operate(data));

      Operand arithmetic3 = MoqlEngine.createOperand("6+ 5*2");
      System.out
          .println(arithmetic3.toString() + " " + arithmetic3.operate(data));
      arithmetic = MoqlEngine.createOperand("num + 21");
      arithmetic.bind(names);
      System.out.println(arithmetic.operate(data));
      arithmetic = MoqlEngine.createOperand("num + num1 + num2");
      arithmetic.bind(names);
      System.out
          .println(arithmetic.toString() + " " + arithmetic.operate(data));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testBitwiseExpression() {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 2);
    entityMap.putEntity("num1", 3);
    entityMap.putEntity("num2", 4);
    try {
      Operand arithmetic = MoqlEngine.createOperand(" num << num1 + 1");
      System.out.println(
          arithmetic.toString() + " = " + arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("num2 | num1 & num");
      System.out.println(
          arithmetic.toString() + " = " + arithmetic.operate(entityMap));
      arithmetic = MoqlEngine.createOperand("~num2 ^ num2");
      System.out.println(
          arithmetic.toString() + " = " + arithmetic.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testEhanceBitwiseExpression() {
    String[] names = new String[] { "num", "num1", "num2" };
    Object[] data = new Object[] { 2, 3, 4 };
    try {
      Operand arithmetic = MoqlEngine.createOperand(" num << num1 + 1");
      arithmetic.bind(names);
      System.out
          .println(arithmetic.toString() + " = " + arithmetic.operate(data));
      arithmetic = MoqlEngine.createOperand("num2 | num1 & num");
      arithmetic.bind(names);
      System.out
          .println(arithmetic.toString() + " = " + arithmetic.operate(data));
      arithmetic = MoqlEngine.createOperand("~num2 ^ num2");
      arithmetic.bind(names);
      System.out
          .println(arithmetic.toString() + " = " + arithmetic.operate(data));
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
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean.getNum()");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("bean.getNum().getX()");
      System.out.println(member.toString() + " " + member.getOperandType());
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
      member = MoqlEngine.createOperand("bean.getNum()");
      member.bind(names);
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

  public void testEhanceArrayExpression() {
    String[] names = new String[] { "bean" };
    Object[] data = new Object[] { BeanFactory.createMapList(5)
    };
    try {
      Operand member = MoqlEngine.createOperand("bean[2]['value']");
      member.bind(names);
      System.out.println(member.operate(data));
      member = MoqlEngine.createOperand("bean[]");
      member.bind(names);
      System.out.println(member.operate(data));
      member = MoqlEngine.createOperand("bean[4]['bean'].num");
      member.bind(names);
      System.out.println(member.operate(data));
      member = MoqlEngine.createOperand("bean[4]['bean'].getArray()[5]");
      member.bind(names);
      System.out.println(member.operate(data));
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
      List<Operand> operands = (List<Operand>) operand
          .operate((EntityMap) null);
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
    entityMap.putEntity("j", parser.parse(json));
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
      List<Operand> operands = (List<Operand>) operand
          .operate((EntityMap) null);
      System.out.println(operands.size());
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void testElementExpression() {
    EntityMap entityMap = new EntityMapImpl();
    String xml = "<e1><e10 a1=\"t1\" a2=\"t2\">test</e10><e11>ddd</e11><e11 a3=\"t3\">eee</e11><e11>fff</e11></e1>";
    ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
    SAXReader reader = new SAXReader();
    try {
      Document document = reader.read(bais);
      entityMap.putEntity("e", document.getRootElement());
      Operand member = MoqlEngine.createOperand("e.e10.getText()");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("e.e10[1]");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("e.e11[1].getText()");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
      member = MoqlEngine.createOperand("e.e11[1]['a3']");
      System.out.println(member.toString() + " " + member.getOperandType());
      System.out.println(member.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (DocumentException e) {
      e.printStackTrace();
    }
  }
}
