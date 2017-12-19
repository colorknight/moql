package org.moql.operand.expression.array;

import com.google.gson.JsonArray;
import org.moql.NumberConvertable;
import org.moql.operand.OperandContextArrayList;
import org.moql.operand.OperandContextList;
import org.moql.util.StringFormater;

import java.lang.reflect.Array;

/**
 * Created by tangtadin on 17/2/5.
 */
public class JsonArrayAccessor implements ArrayAccessor {

  @Override public Object getObject(Object array, Object index) {
    JsonArray jsonArray = (JsonArray)array;
    if (index instanceof Number) {
      return jsonArray.get(((Number)index).intValue());
    }
    if (index.getClass().equals(String.class)) {
      return jsonArray.get(Integer.valueOf((String)index));
    }
    if (index instanceof NumberConvertable) {
      Number inx = ((NumberConvertable)index).toNumber();
      return jsonArray.get(inx.intValue());
    }
    throw new IllegalArgumentException(StringFormater
        .format("Unsupport 'index' of class '{}'!", index.getClass().getName()));
  }

  @Override public OperandContextList toOperandContextList(Object array) {
    JsonArray jsonArray = (JsonArray)array;
    OperandContextList ctxList = new OperandContextArrayList(Array.getLength(array));
    for(int i = 0; i < Array.getLength(array); i++) {
      ctxList.add(jsonArray.get(i));
    }
    return ctxList;
  }
}
