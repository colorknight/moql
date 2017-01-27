package org.moql.cep.sw;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.moql.EntityMap;
import org.moql.EntityMapImpl;
import org.moql.MoqlException;
import org.moql.Operand;
import org.moql.cep.metadata.CeperMetadata;
import org.moql.core.MoqlFactory;
import org.moql.service.MoqlUtils;

import java.util.List;

/**
 * Created by tangtadin on 17/1/25.
 */
public class MatcherWindow<T> extends AbstractWindow<T> {

  public static final String PARAM_MATCHER_EXPRESSION = "win.matcher.expression";

  protected Operand operand;

  protected Object curValue;

  protected EntityMap entityMap = new EntityMapImpl();

  public MatcherWindow(String eventStreamName, CeperMetadata metadata) {
    super(eventStreamName, metadata);
    String expression = metadata.getParameters().get(PARAM_MATCHER_EXPRESSION);
    Validate.notEmpty(expression, "matcher expression is empty!");
    try {
      operand = MoqlUtils.createOperand(expression);
    } catch (MoqlException e) {
      throw new IllegalArgumentException("matcher expression is invalid!");
    }
  }

  @Override public synchronized void push(List<T> dataSet) {
    for(T entity : dataSet) {
      push(entity);
    }
  }

  @Override public synchronized void push(T entity) {
    entityMap.putEntity(eventStreamName, entity);
    Object value = operand.operate(entityMap);
    if (!ObjectUtils.equals(curValue, value)) {
      if (curBucket.size() != 0) {
        operate();
        updateBuckets();
      }
      curValue = value;
    }
    curBucket.add(entity);
  }

  @Override public void onTick(int tick) {

  }
}
