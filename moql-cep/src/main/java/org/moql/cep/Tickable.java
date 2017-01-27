package org.moql.cep;

/**
 * Created by tangtadin on 17/1/27.
 */
public interface Tickable {

  void addTicker(Ticker ticker);

  boolean removeTicker(Ticker ticker);
}
