package org.moql.cep;

import org.moql.RecordSet;

/**
 * Created by tangtadin on 17/1/18.
 */
public interface CepListener {

  void onTrigger(RecordSet recordSet);
}
