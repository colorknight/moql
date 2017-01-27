package org.moql.cep.main;

import org.apache.commons.lang.Validate;
import org.moql.cep.Tickable;
import org.moql.cep.Ticker;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by tangtadin on 17/1/27.
 */
public class TickerTrigger implements Tickable {

  protected List<Ticker> tickers = new LinkedList<Ticker>();

  @Override public void addTicker(Ticker ticker) {
    Validate.notNull(ticker, "ticker is null!");
    tickers.add(ticker);
  }

  @Override public boolean removeTicker(Ticker ticker) {
    if (ticker == null)
      return false;
    return tickers.remove(ticker);
  }

}
