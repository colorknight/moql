package org.moql.core.test;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.moql.core.simulation.AudltDataReader;
import org.moql.data.ColumnFeature;
import org.moql.data.Counter;
import org.moql.data.DataPlanningUtils;

import junit.framework.TestCase;

public class TestDataPlanning extends TestCase {

  public void testGetColumnFeatures() {
//    List<Map<String, Object>> recordSet = AudltDataReader.readAudltData(
//        "./example-data/audlt.data.txt", 10000);
//    List<ColumnFeature> columnFeatures = DataPlanningUtils.analyzeColumns(
//        recordSet, null);
//    outputColumnFeatures(columnFeatures);
  }

  protected void outputColumnFeatures(List<ColumnFeature> columnFeatures) {
    DecimalFormat df = new DecimalFormat("0.0000");
    for (ColumnFeature columnFeature : columnFeatures) {
      System.out.println("-----------------------------------");
      System.out.println("ColumnName :" + columnFeature.getName()
          + "; Ratio : " + df.format(columnFeature.getRatioOfVR()));
      if (!columnFeature.isDimension())
        continue;
      System.out.println("\r\nValues:\r\n");
      for(Map.Entry<Object, Counter> entry : columnFeature.getValueCounters().entrySet()) {
        System.out.println(entry.getKey().toString()+" : "+entry.getValue().getCount());
      }
    }
  }
}
