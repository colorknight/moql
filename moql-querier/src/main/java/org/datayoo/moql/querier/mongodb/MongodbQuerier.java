package org.datayoo.moql.querier.mongodb;

import org.datayoo.moql.RecordSet;
import org.datayoo.moql.querier.DataQuerier;
import org.datayoo.moql.querier.SupplementReader;

import java.io.IOException;
import java.util.Properties;

public class MongodbQuerier implements DataQuerier {


  @Override
  public void connect(String[] serverIps, Properties properties)
      throws IOException {

  }

  @Override
  public void disconnect() throws IOException {

  }

  @Override
  public RecordSet query(String sql) throws IOException {
    return null;
  }

  @Override
  public RecordSet query(String sql, Properties queryProps) throws IOException {
    return null;
  }

  @Override
  public RecordSet query(String sql, SupplementReader supplementReader)
      throws IOException {
    return null;
  }

  @Override
  public RecordSet query(String sql, Properties queryProps,
      SupplementReader supplementReader) throws IOException {
    return null;
  }
}
