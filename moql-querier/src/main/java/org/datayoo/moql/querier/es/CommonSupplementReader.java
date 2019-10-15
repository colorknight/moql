package org.datayoo.moql.querier.es;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.datayoo.moql.querier.SupplementReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommonSupplementReader implements SupplementReader {

  protected int totalHits;

  protected double maxScore;

  protected List<Map<String, Object>> hitSupplements = new LinkedList();

  @Override
  public void read(JsonObject root) {
    JsonObject aggHits = (JsonObject) root.get("aggregations");
    if (aggHits != null) {
      readAggregations(aggHits);
    } else {
      JsonObject hits = (JsonObject) root.get("hits");
      readHits(hits);
    }
  }

  protected void readAggregations(JsonObject aggs) {

  }

  protected void readHits(JsonObject hits) {
    JsonElement jValue = hits.getAsJsonPrimitive("total");
    totalHits = jValue.getAsInt();
    jValue = hits.get("max_score");
    if (!jValue.isJsonNull())
      maxScore = jValue.getAsDouble();
    JsonArray hitArray = hits.getAsJsonArray("hits");
    for (int i = 0; i < hitArray.size(); i++) {
      JsonObject jo = (JsonObject) hitArray.get(i);
      this.hitSupplements.add(readHit(jo));
    }
  }

  protected Map<String, Object> readHit(JsonObject jo) {
    Map<String, Object> hitMap = new HashMap<String, Object>();
    for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {
      if (entry.getValue() instanceof JsonPrimitive) {
        Object value = getValue((JsonPrimitive) entry.getValue());
        hitMap.put(entry.getKey(), value);
        continue;
      }
      if (entry.getKey().equals("sort")) {
        hitMap.put("sort", readSortArray((JsonArray) entry.getValue()));
      }
    }
    return hitMap;
  }

  protected Object[] readSortArray(JsonArray sortArray) {
    Object[] data = new Object[sortArray.size()];
    for (int i = 0; i < sortArray.size(); i++) {
      data[i] = getValue((JsonPrimitive) sortArray.get(i));
    }
    return data;
  }

  protected Object getValue(JsonPrimitive value) {
    if (value.isNumber()) {
      Number number = value.getAsNumber();
      double d = number.doubleValue();
      long l = number.longValue();
      if (d - l > 0) {
        return d;
      } else {
        return l;
      }
    } else if (value.isBoolean())
      return value.getAsBoolean();
    else
      return value.getAsString();
  }

  public int getTotalHits() {
    return totalHits;
  }

  public double getMaxScore() {
    return maxScore;
  }

  public List<Map<String, Object>> getHitSupplements() {
    return hitSupplements;
  }
}
