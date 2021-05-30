/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.translator;

import org.datayoo.moql.Filter;
import org.datayoo.moql.Selector;
import org.datayoo.moql.sql.*;
import org.datayoo.moql.sql.es.ElasticSearchTranslator;
import org.datayoo.moql.sql.mongodb.MongoDBTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tang Tadin
 */
public abstract class SqlTranslatorHelper {

  public static MoqlGrammarTranslator moqlTranslator;

  public static OracleTranslator oracleTranslator;

  public static SQLServerTranslator sqlserverTranslator;

  public static DB2Translator db2Translator;

  public static MySQLTranslator mysqlTranslator;

  public static PostgreSQLTranslator postgresqlTranslator;

  public static ElasticSearchTranslator elasticSearchTranslator;

  public static MongoDBTranslator mongoDBTranslator;

  public static String translate2Sql(Selector selector,
      SqlDialectType dialectType) {
    return translate2Sql(selector, dialectType, new HashMap<String, Object>());
  }

  public static String translate2Sql(Selector selector,
      SqlDialectType dialectType, Map<String, Object> translationContext) {
    SqlTranslator sqlTranslator;
    if (dialectType.equals(SqlDialectType.MOQL)) {
      sqlTranslator = getMoqlTranslator();
    } else if (dialectType.equals(SqlDialectType.ORACLE)) {
      sqlTranslator = getOracleTranslator();
    } else if (dialectType.equals(SqlDialectType.SQLSERVER)) {
      sqlTranslator = getSQLServerTranslator();
    } else if (dialectType.equals(SqlDialectType.DB2)) {
      sqlTranslator = getDB2Translator();
    } else if (dialectType.equals(SqlDialectType.MYSQL)) {
      sqlTranslator = getMySQLTranslator();
    } else if (dialectType.equals(SqlDialectType.POSTGRESQL)) {
      sqlTranslator = getPostgreSQLTranslator();
    } else if (dialectType.equals(SqlDialectType.ELASTICSEARCH)) {
      sqlTranslator = getElasticSearchTranslator();
    } else if (dialectType.equals(SqlDialectType.MONGODB)) {
      sqlTranslator = getMongoDBTranslator();
    } else {
      throw new UnsupportedOperationException();
    }
    return sqlTranslator.translate2Sql(selector, translationContext);
  }

  public static String translate2Condition(Filter filter,
      SqlDialectType dialectType) {
    return translate2Condition(filter, dialectType,
        new HashMap<String, Object>());
  }

  public static String translate2Condition(Filter filter,
      SqlDialectType dialectType, Map<String, Object> translationContext) {
    SqlTranslator sqlTranslator;
    if (dialectType.equals(SqlDialectType.MOQL)) {
      sqlTranslator = getMoqlTranslator();
    } else if (dialectType.equals(SqlDialectType.ORACLE)) {
      sqlTranslator = getOracleTranslator();
    } else if (dialectType.equals(SqlDialectType.SQLSERVER)) {
      sqlTranslator = getSQLServerTranslator();
    } else if (dialectType.equals(SqlDialectType.DB2)) {
      sqlTranslator = getDB2Translator();
    } else if (dialectType.equals(SqlDialectType.MYSQL)) {
      sqlTranslator = getMySQLTranslator();
    } else if (dialectType.equals(SqlDialectType.POSTGRESQL)) {
      sqlTranslator = getPostgreSQLTranslator();
    } else {
      throw new UnsupportedOperationException();
    }
    return sqlTranslator.translate2Condition(filter, translationContext);
  }

  protected static synchronized MoqlGrammarTranslator getMoqlTranslator() {
    if (moqlTranslator == null) {
      moqlTranslator = new MoqlGrammarTranslator();
    }
    return moqlTranslator;
  }

  protected static synchronized OracleTranslator getOracleTranslator() {
    if (oracleTranslator == null) {
      oracleTranslator = new OracleTranslator();
    }
    return oracleTranslator;
  }

  protected static synchronized SQLServerTranslator getSQLServerTranslator() {
    if (sqlserverTranslator == null) {
      sqlserverTranslator = new SQLServerTranslator();
    }
    return sqlserverTranslator;
  }

  protected static synchronized DB2Translator getDB2Translator() {
    if (db2Translator == null) {
      db2Translator = new DB2Translator();
    }
    return db2Translator;
  }

  protected static synchronized MySQLTranslator getMySQLTranslator() {
    if (mysqlTranslator == null) {
      mysqlTranslator = new MySQLTranslator();
    }
    return mysqlTranslator;
  }

  protected static synchronized PostgreSQLTranslator getPostgreSQLTranslator() {
    if (postgresqlTranslator == null) {
      postgresqlTranslator = new PostgreSQLTranslator();
    }
    return postgresqlTranslator;
  }

  protected static synchronized ElasticSearchTranslator getElasticSearchTranslator() {
    if (elasticSearchTranslator == null) {
      elasticSearchTranslator = new ElasticSearchTranslator();
    }
    return elasticSearchTranslator;
  }

  protected static synchronized MongoDBTranslator getMongoDBTranslator() {
    if (mongoDBTranslator == null) {
      mongoDBTranslator = new MongoDBTranslator();
    }
    return mongoDBTranslator;
  }
}
