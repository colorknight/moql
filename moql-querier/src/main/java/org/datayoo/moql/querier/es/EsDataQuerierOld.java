package org.datayoo.moql.querier.es;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.MoqlException;
import org.datayoo.moql.RecordSet;
import org.datayoo.moql.SelectorDefinition;
import org.datayoo.moql.metadata.QueryableMetadata;
import org.datayoo.moql.metadata.SelectorMetadata;
import org.datayoo.moql.metadata.TableMetadata;
import org.datayoo.moql.metadata.TablesMetadata;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.querier.DataQuerier;
import org.datayoo.moql.sql.SqlDialectType;
import org.datayoo.moql.translator.MoqlTranslator;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EsDataQuerierOld implements DataQuerier {

  public static String CLUSTER_NAME = "cluster.name";

  public static String CLIENT_TRANSPORT_SNIFF = "client.transport.sniff";

  protected TransportClient client;

  @Override
  public synchronized void connect(String[] serverIps, Properties properties)
      throws IOException {
    Validate.notEmpty(serverIps, "serverIps is empty!");
    Validate.notEmpty(properties, "properties is empty!");

    Settings.Builder builder = Settings.builder();
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      String key = (String) entry.getKey();
      if (key.equals(CLIENT_TRANSPORT_SNIFF)) {
        builder.put(key, (Boolean)entry.getValue());
      } else {
        builder.put(key, (String) entry.getValue());
      }
    }
    Settings settings = builder.build();
    client = new PreBuiltTransportClient(settings);
    for (int i = 0; i < serverIps.length; i++) {
      try {
        client.addTransportAddresses(
            new TransportAddress(InetAddress.getByName(serverIps[i]), 9300));
      } catch (UnknownHostException e) {
        throw new IOException(e);
      }
    }
  }

  @Override public synchronized void disconnect() throws IOException {
    if (client != null) {
      client.close();
      client = null;
    }
  }

  @Override public RecordSet query(String sql) throws IOException {
    Validate.notEmpty(sql, "sql is empty!");
    try {
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      List<String> indexAndTables = getIndexAndTables(selectorDefinition);
      String query = MoqlTranslator.translateMetadata2Sql(selectorDefinition,
          SqlDialectType.ELASTICSEARCH);
      SearchResponse searchResponse = query(indexAndTables, query);

    } catch (MoqlException e) {
      throw new IOException("Parse failed!", e);
    }
    return null;
  }

  @Override public RecordSet query(String sql, Properties queryProps)
      throws IOException {
    return null;
  }

  protected List<String> getIndexAndTables(
      SelectorDefinition selectorDefinition) {
    SelectorMetadata metadata = (SelectorMetadata) selectorDefinition;
    TablesMetadata tablesMetadata = metadata.getTables();
    List<String> indexAndTables = new LinkedList<String>();
    String index = null;
    for (QueryableMetadata qm : tablesMetadata.getTables()) {
      if (!(qm instanceof TableMetadata)) {
        throw new UnsupportedOperationException("Unsupported join operator!");
      }
      TableMetadata tableMetadata = (TableMetadata) qm;
      String[] segs = tableMetadata.getValue().split(".");
      if (segs.length == 0) {
        segs = new String[] {tableMetadata.getValue()};
      }
      if (indexAndTables.size() == 0) {
        indexAndTables.add(segs[0]);
        index = segs[0];
      } else {
        if (!index.equals(segs[0])) {
          throw new IllegalArgumentException(
              "Index of doc type are different!");
        }
      }
      if (segs.length != 1) {
        indexAndTables.add(segs[1]);
      }
    }
    return indexAndTables;
  }

  protected SearchResponse query(List<String> indexAndTables, String query) {
    SearchRequestBuilder searchRequestBuilder = client
        .prepareSearch(indexAndTables.get(0));
    if (indexAndTables.size() > 1) {
      searchRequestBuilder.setTypes(assembleTypes(indexAndTables));
    }
    searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
    searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(query));
    return searchRequestBuilder.execute().actionGet();
  }

  protected String assembleTypes(List<String> indexAndTables) {
    StringBuffer sbuf = new StringBuffer();
    for (int i = 1; i < indexAndTables.size(); i++) {
      if (i > 1)
        sbuf.append(",");
      sbuf.append("\"");
      sbuf.append(indexAndTables.get(i));
      sbuf.append("\"");
    }
    return sbuf.toString();
  }

  protected RecordSet toRecordSet(SelectorDefinition selectorDefinition,
      SearchResponse searchResponse) {
    SearchHits hits = searchResponse.getHits();
    for(SearchHit hit : hits) {

      Map<String, Object> map = hit.getSourceAsMap();

    }
    return null;
  }



}
