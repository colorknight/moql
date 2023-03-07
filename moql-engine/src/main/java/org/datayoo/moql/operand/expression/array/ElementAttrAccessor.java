package org.datayoo.moql.operand.expression.array;

import com.google.gson.JsonArray;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.util.StringFormater;
import org.dom4j.Element;

import java.lang.reflect.Array;

/**
 * Created by tangtadin on 21/4/11.
 */
public class ElementAttrAccessor implements ArrayAccessor {

  @Override
  public Object getObject(Object array, Object index) {
    Element element = (Element) array;
    if (index instanceof Number) {
      return element.attribute((((Number) index).intValue())).getValue();
    }
    if (index instanceof String) {
      return element.attribute(index.toString()).getValue();
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      return element.attribute(inx.intValue()).getValue();
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public void setObject(Object array, Object index, Object value) {
    if (value == null)
      value = "";
    Element element = (Element) array;
    if (index instanceof Number) {
      element.attribute((((Number) index).intValue()))
          .setValue(value.toString());
      return;
    }
    if (index instanceof String) {
      element.attribute(index.toString()).setValue(value.toString());
      return;
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      element.attribute(inx.intValue()).setValue(value.toString());
      return;
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public OperandContextList toOperandContextList(Object array) {
    Element element = (Element) array;
    OperandContextList ctxList = new OperandContextArrayList(
        Array.getLength(array));
    for (int i = 0; i < element.attributeCount(); i++) {
      ctxList.add(element.attribute(i).getValue());
    }
    return ctxList;
  }
}
