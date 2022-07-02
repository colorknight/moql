package org.datayoo.moql.env;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tangtadin
 * @version 1.0
 * @description: TODO
 * @date 2022/7/2 2:44 PM
 */
public abstract class MoqlEnv {
  //	env properties
  public static final String ENV_OPERAND_FACTORY = "org.moql.operandfactory";

  protected static Map<String, Object> envMap = new HashMap<>();

  public static Object getEnvProp(String name) {
    return envMap.get(name);
  }

  public static void putEnvProp(String name, Object value) {
    envMap.put(name, value);
  }

  public static Object removeEnvProp(String name) {
    return envMap.remove(name);
  }
}
