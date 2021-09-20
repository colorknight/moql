package org.datayoo.moql.engine;

import org.datayoo.moql.EntityMap;
import org.datayoo.moql.EntityMapImpl;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.Operand;
import org.datayoo.moql.operand.function.AbstractFunction;
import org.datayoo.moql.operand.function.FunctionType;

import java.util.List;

public class TestRegistFunction {

  public static class Test extends AbstractFunction {
    public Test(List<Operand> parameters) {
      /* test is the function's name, 3 is the parameters' count.
       * if the parameters' count not equal the parameters' size
       * it will throw the IllegalArgumentException
       * */
      super("test", 3, parameters);
      functionType = FunctionType.COMMON;
    }

    @Override
    protected Object innerOperate(EntityMap entityMap) {
      // TODO Auto-generated method stub
      Object obj1 = parameters.get(0).operate(entityMap);
      Object obj2 = parameters.get(1).operate(entityMap);
      Object obj3 = parameters.get(2).operate(entityMap);
      StringBuffer sbuf = new StringBuffer();
      sbuf.append(obj1.toString());
      sbuf.append("||");
      sbuf.append(obj2.toString());
      sbuf.append("||");
      sbuf.append(obj3.toString());
      return sbuf.toString();
    }

    @Override
    protected Object innerOperate(Object[] entityArray) {
      Object obj1 = parameters.get(0).operate(entityArray);
      Object obj2 = parameters.get(1).operate(entityArray);
      Object obj3 = parameters.get(2).operate(entityArray);
      StringBuffer sbuf = new StringBuffer();
      sbuf.append(obj1.toString());
      sbuf.append("||");
      sbuf.append(obj2.toString());
      sbuf.append("||");
      sbuf.append(obj3.toString());
      return sbuf.toString();
    }

  }

  public static void main(String[] args) {
    EntityMap entityMap = new EntityMapImpl();
    entityMap.putEntity("num", 123);
    try {
      Operand function = MoqlEngine.createOperand("test(1, num, 'a')");
      System.out.println(function.toString() + " " + function.getOperandType());
      System.out.println(function.operate(entityMap));
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      MoqlEngine.registFunction("test", Test.class.getName());
      Operand function = MoqlEngine.createOperand("test(1, num, 'a')");
      System.out.println(function.toString() + " " + function.getOperandType());
      System.out.println(function.operate(entityMap));
    } catch (MoqlException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
