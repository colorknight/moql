package org.moql.cep.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by tangtadin on 17/1/27.
 */
public abstract class DataSimulator {

  public static List<Object[]> createDataList(int bucketCount, int bucketSize) {
    List<Object[]> dataList = new LinkedList<Object[]>();
    Random random = new Random();
    for(int i = 0; i < bucketCount; i++) {
      char c = (char)('A'+ i % 'A');
      for (int j = 0; j < bucketSize; j++) {
        Object[] data = new Object[] {c, j, random.nextInt(j+bucketCount)};
        dataList.add(data);
      }
    }
    return dataList;
  }
}
