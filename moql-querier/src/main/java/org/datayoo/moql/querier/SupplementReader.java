package org.datayoo.moql.querier;

import com.google.gson.JsonObject;

public interface SupplementReader {

  void read(JsonObject root);
}
