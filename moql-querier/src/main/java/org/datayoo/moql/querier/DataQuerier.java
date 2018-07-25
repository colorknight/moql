package org.datayoo.moql.querier;

import org.datayoo.moql.RecordSet;

import java.io.IOException;
import java.util.Properties;

public interface DataQuerier {

  void connect(String[] serverIps, Properties properties) throws IOException;

  void disconnect() throws IOException;

  RecordSet query(String sql) throws IOException;

  RecordSet query(String sql, Properties queryProps) throws IOException;

}
