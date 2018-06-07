package org.moql.querier;

import org.moql.RecordSet;

import java.io.IOException;
import java.util.Properties;

public interface DataQuerier {

  void connect(String[] serverIps, Properties properties) throws IOException;

  void disconnect() throws IOException;

  RecordSet query(String sql) throws IOException;

}
