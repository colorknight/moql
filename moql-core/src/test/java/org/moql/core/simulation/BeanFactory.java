package org.moql.core.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class BeanFactory {

  public static List<BeanA> createBeanAList(int startNumber, int size) {
    List<BeanA> beanList = new LinkedList<BeanA>();
    size = startNumber + size;
    for (int i = startNumber; i < size; i++) {
      BeanA beanA = new BeanA(String.valueOf(i), i * 5);
      beanList.add(beanA);
    }
    return beanList;
  }

  public static List<BeanB> createBeanBList(int startNumber, int size) {
    List<BeanB> beanList = new LinkedList<BeanB>();
    size = startNumber + size;
    for (int i = startNumber; i < size; i++) {
      BeanB beanB = new BeanB(String.valueOf(i), i * 10);
      beanList.add(beanB);
    }
    return beanList;
  }

  public static List<BeanC> createBeanCList(int startNumber, int size) {
    List<BeanC> beanList = new LinkedList<BeanC>();
    size = startNumber + size;
    for (int i = startNumber; i < size; i++) {
      BeanC beanC = new BeanC(String.valueOf(i), i * 10);
      beanList.add(beanC);
    }
    return beanList;
  }

  public static List<Object[]> createObjectArrayList(int startNumber,
      int size) {
    List<Object[]> arrayList = new LinkedList<Object[]>();
    size = startNumber + size;
    for (int i = startNumber; i < size; i++) {
      Object[] objAry = null;
      if (startNumber % 2 == 0)
        objAry = new Object[] { startNumber, "ary" + size, "group1" };
      else
        objAry = new Object[] { startNumber, "ary" + size, null };
      arrayList.add(objAry);
    }
    return arrayList;
  }

  public static List<LittileBean> createLittleBeanList(int startNumber,
      int size) {
    List<LittileBean> beanList = new LinkedList<LittileBean>();
    size = startNumber + size;
    for (int i = startNumber; i < size; i++) {
      LittileBean littileBean = new LittileBean(String.valueOf(i));
      beanList.add(littileBean);
    }
    return beanList;
  }

  public static List<Map<String, Object>> toMap(List<? extends Bean> beanList) {
    List<Map<String, Object>> mapList = new LinkedList<Map<String, Object>>();
    for (Bean bean : beanList) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("id", bean.getId());
      map.put("name", bean.getName());
      map.put("num", bean.getNum());
    }
    return mapList;
  }

  public static List<Map<String, Object>> createMapList(int size) {
    if (size < 200) {
      size = 200;
    }
    int randNameCount = size / 10;
    int randValueCount = size / 200;
    List<Map<String, Object>> mapList = new LinkedList<Map<String, Object>>();

    for (int i = 0; i < size; i++) {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("name", String.valueOf(i % randNameCount));
      map.put("value", String.valueOf(i % randValueCount));
      map.put("bean", new BeanA("bean" + i, 10));
      mapList.add(map);
    }
    return mapList;
  }

  public static List<ScoreItem> createScoreItems() {
    String[] students = new String[] { "Alex", "John", "Jenny", "Tom", "Smith"
    };
    String[] subjects = new String[] { "Language", "Logic", "Scientist",
        "Nature"
    };
    int[] scores = new int[] { 83, 79, 98, 85, 85, 82, 74, 91, 93, 95, 88, 94,
        69, 87, 89, 97, 93, 62, 73, 88
    };
    List<ScoreItem> scoreItems = new LinkedList<ScoreItem>();
    int id = 0;
    for (int i = 0; i < students.length; i++) {
      for (int j = 0; j < subjects.length; j++) {
        ScoreItem si = new ScoreItem(++id);
        si.setStudent(students[i]);
        si.setSubject(subjects[j]);
        si.setTerm1(scores[id - 1]);
        si.setTerm2(scores[id - 1] + 2);
        si.setTerm3(scores[id - 1] - 2);
        si.setTerm4(scores[id - 1] + 1);
        scoreItems.add(si);
      }
    }
    return scoreItems;
  }

  public static List<Object[]> createArrayList(int size) {
    List<Object[]> result = new LinkedList<Object[]>();
    for (int i = 0; i < size; i++) {
      Object[] ary = new Object[] { i, "name", "data" };
      result.add(ary);
    }
    return result;
  }

  public static List<BeanE> createBeanEList() {
    List<BeanE> beanList = new LinkedList<BeanE>();
    for (int i = 1; i <= 10; i++) {
      String src = "src" + i % 3;
      String dst = "dst" + i % 3;
      BeanE beanE = new BeanE(src, dst);
      beanList.add(beanE);
    }
    return beanList;
  }
}
