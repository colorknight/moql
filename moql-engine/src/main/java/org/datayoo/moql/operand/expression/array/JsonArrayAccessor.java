package org.datayoo.moql.operand.expression.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import org.datayoo.moql.NumberConvertable;
import org.datayoo.moql.operand.OperandContextArrayList;
import org.datayoo.moql.operand.OperandContextList;
import org.datayoo.moql.util.StringFormater;

import java.lang.reflect.Array;

/**
 * Created by tangtadin on 17/2/5.
 */
public class JsonArrayAccessor implements ArrayAccessor {

  @Override
  public Object getObject(Object array, Object index) {
    JsonArray jsonArray = (JsonArray) array;
    if (index instanceof Number) {
      return jsonArray.get(((Number) index).intValue());
    }
    //    if (index.getClass().equals(String.class)) {
    //      return jsonArray.get(Integer.valueOf((String)index));
    //    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      return jsonArray.get(inx.intValue());
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  @Override
  public void setObject(Object array, Object index, Object value) {
    JsonArray jsonArray = (JsonArray) array;
    if (index instanceof Number) {
      jsonArray.set(((Number) index).intValue(), toJsonElement(value));
      return;
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable) index).toNumber();
      jsonArray.set(inx.intValue(), toJsonElement(value));
      return;
    }
    throw new IllegalArgumentException(
        StringFormater.format("Unsupport 'index' of class '{}'!",
            index.getClass().getName()));
  }

  protected JsonElement toJsonElement(Object value) {
    if (value == null)
      return JsonNull.INSTANCE;
    if (value instanceof Number)
      return new JsonPrimitive((Number) value);
    if (value instanceof Boolean)
      return new JsonPrimitive((Boolean) value);
    return new JsonPrimitive(value.toString());
  }

  @Override
  public OperandContextList toOperandContextList(Object array) {
    JsonArray jsonArray = (JsonArray) array;
    OperandContextList ctxList = new OperandContextArrayList(
        Array.getLength(array));
    for (int i = 0; i < jsonArray.size(); i++) {
      ctxList.add(jsonArray.get(i));
    }
    return ctxList;
  }
}
