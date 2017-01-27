package org.moql.cep.sw;

import org.moql.cep.SlideWindow;
import org.moql.cep.metadata.CeperMetadata;

/**
 * Created by tangtadin on 17/1/26.
 */
public class SlideWindowFactory<T> {

  public SlideWindow<T> createSlideWindow(SlideWindowEnum type,
      String eventStreamName, CeperMetadata metadata) {
    if (type == SlideWindowEnum.SW_BATCH)
      return new BatchWindow<T>(eventStreamName, metadata);
    else if (type == SlideWindowEnum.SW_TIME)
      return new TimeWindow<T>(eventStreamName, metadata);
    else if (type == SlideWindowEnum.SW_BATCH_TIME)
      return new BatchAndTimeWindow<T>(eventStreamName, metadata);
    else if (type == SlideWindowEnum.SW_MATCHER)
      return new MatcherWindow<T>(eventStreamName, metadata);
    else
      throw new IllegalArgumentException("");
  }
}
