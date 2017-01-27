package org.moql.cep.main;

import org.moql.ColumnDefinition;
import org.moql.RecordSet;
import org.moql.RecordSetDefinition;
import org.moql.cep.CepListener;

/**
 * Created by tangtadin on 17/1/26.
 */
public class CepPrintListener implements CepListener {

  @Override public void onTrigger(RecordSet recordSet) {
    RecordSetDefinition recordSetDefinition = recordSet
        .getRecordSetDefinition();
    StringBuffer sbuf = new StringBuffer();
    for (ColumnDefinition column : recordSetDefinition.getColumns()) {
      sbuf.append(column.getName());
      sbuf.append("    ");
    }
    System.out.println(sbuf.toString());
    for (Object[] record : recordSet.getRecords()) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < record.length; i++) {
        if (record[i] != null) {
          sb.append(record[i].toString());
        } else {
          sb.append("NULL");
        }
        sb.append(" ");
      }
      System.out.println(sb.toString());
    }
    System.out.println("------------------------------------------------");
  }
}
