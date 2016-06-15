/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moql.sql;

import org.moql.Filter;
import org.moql.Selector;
import org.moql.sql.es.ElasticSearchTranslator;

/**
 * @author Tang Tadin
 */
public abstract class SqlTranslatorHelper {

  public static MoqlTranslator moqlTranslator;

  public static OracleTranslator oracleTranslator;

  public static SQLServerTranslator sqlserverTranslator;

  public static DB2Translator db2Translator;

  public static MySQLTranslator mysqlTranslator;

  public static PostgreSQLTranslator postgresqlTranslator;

  public static ElasticSearchTranslator elasticSearchTranslator;

  public static String translate2Sql(Selector selector,
      SqlDialectType dialectType) {
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
    } else {
      throw new UnsupportedOperationException();
    }
    return sqlTranslator.translate2Sql(selector);
  }

  public static String translate2Condition(Filter filter,
      SqlDialectType dialectType) {
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
    return sqlTranslator.translate2Condition(filter);
  }

  protected static synchronized MoqlTranslator getMoqlTranslator() {
    if (moqlTranslator == null) {
      moqlTranslator = new MoqlTranslator();
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
}
