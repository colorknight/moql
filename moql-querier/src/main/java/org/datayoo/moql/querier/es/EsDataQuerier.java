package org.datayoo.moql.querier.es;

import com.google.gson.*;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.datayoo.moql.*;
import org.datayoo.moql.core.RecordSetImpl;
import org.datayoo.moql.core.RecordSetMetadata;
import org.datayoo.moql.metadata.*;
import org.datayoo.moql.operand.OperandFactory;
import org.datayoo.moql.operand.factory.OperandFactoryImpl;
import org.datayoo.moql.parser.MoqlParser;
import org.datayoo.moql.querier.DataQuerier;
import org.datayoo.moql.sql.SqlDialectType;
import org.datayoo.moql.translator.MoqlTranslator;
import org.datayoo.moql.util.StringFormater;

import java.io.IOException;
import java.util.*;

public class EsDataQuerier implements DataQuerier {

  public static String HTTP_PORT = "http.port";

  public static String DOC_COUNT = "doc_count";

  protected String esServiceUrl;

  protected CloseableHttpClient httpClient;

  protected OperandFactory operandFactory = OperandFactoryImpl
      .createOperandFactory();

  @Override
  public synchronized void connect(String[] serverIps, Properties properties)
      throws IOException {
    Validate.notEmpty(serverIps, "serverIps is empty!");
    int port = 9200;
    if (properties != null) {
      Object obj = properties.get(HTTP_PORT);
      if (obj != null)
        port = Integer.valueOf(obj.toString());
    }

    esServiceUrl = StringFormater.format("http://{}:{}", serverIps[0], port);

    httpClient = HttpClients.createDefault();
  }

  @Override public synchronized void disconnect() throws IOException {
    if (httpClient != null) {
      httpClient.close();
      httpClient = null;
    }
  }

  @Override public RecordSet query(String sql) throws IOException {
    Validate.notEmpty(sql, "sql is empty!");
    return query(sql, (Properties) null);
  }

  @Override public RecordSet query(String sql, Properties queryProps)
      throws IOException {
    Validate.notEmpty(sql, "sql is empty!");
    if (queryProps == null) {
      queryProps = new Properties();
    }
    try {
      SelectorDefinition selectorDefinition = MoqlParser.parseMoql(sql);
      List<String> indexAndTables = getIndexAndTables(selectorDefinition);
      String query = MoqlTranslator.translateMetadata2Sql(selectorDefinition,
          SqlDialectType.ELASTICSEARCH);
      String queryUrl = makeQueryUrl(indexAndTables, queryProps);
      HttpResponse response = query(queryUrl, query);
      String data = EntityUtils.toString(response.getEntity());
      return toRecordSet(data, selectorDefinition);
    } catch (MoqlException e) {
      throw new IOException("Parse failed!", e);
    }
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
        segs = new String[] { tableMetadata.getValue() };
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

  protected String makeQueryUrl(List<String> indexAndTables,
      Properties queryProps) {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append(esServiceUrl);
    sbuf.append("/");
    sbuf.append(indexAndTables.get(0));
    sbuf.append("/");
    if (indexAndTables.size() > 1) {
      for (int i = 1; i < indexAndTables.size(); i++) {
        if (i != 1) {
          sbuf.append(",");
        }
        sbuf.append(indexAndTables.get(i));
      }
      sbuf.append("/");
    }
    sbuf.append("_search?pretty");
    assembleUrlProperties(sbuf, queryProps);
    return sbuf.toString();
  }

  protected void assembleUrlProperties(StringBuffer sbuf,
      Properties queryProps) {
    for(Map.Entry<Object, Object> entry : queryProps.entrySet()) {
      sbuf.append("&");
      sbuf.append(entry.getKey());
      sbuf.append("=");
      sbuf.append(entry.getValue());
    }
  }

  protected HttpResponse query(String queryUrl, String query)
      throws IOException {
    HttpPost httpPost = new HttpPost(queryUrl);
    StringEntity entity = new StringEntity(query, "utf-8");//解决中文乱码问题
    entity.setContentEncoding("UTF-8");
    entity.setContentType("application/json");
    httpPost.setEntity(entity);
    HttpResponse response = httpClient.execute(httpPost);
    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 && status < 300) {
      return response;
    } else {
      throw new ClientProtocolException(
          "Unexpected response status: " + status);
    }
  }

  protected RecordSet toRecordSet(String data,
      SelectorDefinition selectorDefinition) {
    JsonParser jsonParser = new JsonParser();
    JsonObject root = (JsonObject) jsonParser.parse(data);
    JsonObject aggHits = (JsonObject) root.get("aggregations");
    if (aggHits != null) {
      return toAggregationRecordSet(aggHits, selectorDefinition);
    } else {
      JsonObject hits = (JsonObject) root.get("hits");
      return toQueryRecordSet(hits, selectorDefinition);
    }
  }

  protected RecordSet toQueryRecordSet(JsonObject jsonObject,
      SelectorDefinition selectorDefinition) {
    RecordSetImpl recordSet = createRecordSet(selectorDefinition);
    Operand[] operands = buildColumnOperands(selectorDefinition);
    JsonArray hitArray = jsonObject.getAsJsonArray("hits");
    List<Object[]> records = recordSet.getRecords();
    for (int i = 0; i < hitArray.size(); i++) {
      JsonObject jo = (JsonObject) hitArray.get(i);
      EntityMap entityMap = toQueryEntityMap(jo);
      Object[] record = toRecord(operands, entityMap);
      records.add(record);
    }
    return recordSet;
  }

  protected RecordSet toAggregationRecordSet(JsonObject jsonObject,
      SelectorDefinition selectorDefinition) {
    RecordSetImpl recordSet = createRecordSet(selectorDefinition);
    Operand[] operands = buildColumnOperands(selectorDefinition);

    List<Object[]> records = recordSet.getRecords();
    List<EntityMap> entityMaps = toAggregationEntityMaps(jsonObject,
        recordSet.getRecordSetDefinition().getGroupColumns());
    for (EntityMap entityMap : entityMaps) {
      Object[] record = toRecord(operands, entityMap);
      records.add(record);
    }
    return recordSet;
  }

  protected RecordSetImpl createRecordSet(
      SelectorDefinition selectorDefinition) {
    SelectorMetadata selectorMetadata = (SelectorMetadata) selectorDefinition;
    List<ColumnDefinition> columns = new LinkedList<ColumnDefinition>();
    for (ColumnMetadata columnMetadata : selectorMetadata.getColumns()
        .getColumns()) {
      columns.add(columnMetadata);
    }
    List<ColumnDefinition> groupColumns = new LinkedList<ColumnDefinition>();
    if (selectorMetadata.getGroupBy() != null) {
      for (GroupMetadata groupMetadata : selectorMetadata.getGroupBy()) {
        ColumnDefinition columnDefinition = getColumnDefinition(
            groupMetadata.getColumn(), columns);
        groupColumns.add(columnDefinition);
      }
    }
    RecordSetMetadata recordSetMetadata = new RecordSetMetadata(columns,
        groupColumns);
    return new RecordSetImpl(recordSetMetadata, new Date(), new Date(),
        new LinkedList<Object[]>());
  }

  protected ColumnDefinition getColumnDefinition(String name,
      List<ColumnDefinition> columns) {
    for (ColumnDefinition columnDefinition : columns) {
      if (name.equals(columnDefinition.getName()))
        return columnDefinition;
    }
    throw new IllegalArgumentException(
        StringFormater.format("Invalid group column name '{}'!", name));
  }

  protected Operand[] buildColumnOperands(
      SelectorDefinition selectorDefinition) {
    SelectorMetadata selectorMetadata = (SelectorMetadata) selectorDefinition;
    Operand[] operands = new Operand[selectorMetadata.getColumns().getColumns()
        .size()];
    int i = 0;
    for (ColumnMetadata columnMetadata : selectorMetadata.getColumns()
        .getColumns()) {
      String value = columnMetadata.getValue();
      int index = value.indexOf('(');
      if (index == -1) {
        index = value.indexOf('.');
        value = value.substring(index + 1);
      } else {
        value = getAggregationFunctionExpression(columnMetadata);
      }
      try {
        operands[i++] = operandFactory.createOperand(value);
      } catch (MoqlException e) {
        throw new IllegalArgumentException(StringFormater
            .format("Invalid column value '{}'!", columnMetadata.getValue()));
      }
    }
    return operands;
  }

  protected String getAggregationFunctionExpression(
      ColumnMetadata columnMetadata) {
    int index = columnMetadata.getValue().indexOf("count(");
    if (index != -1) {
      index = columnMetadata.getValue().indexOf("true");
      if (index == -1)
        return DOC_COUNT;
    }
    return columnMetadata.getName();
  }

  protected EntityMap toQueryEntityMap(JsonObject jsonObject) {
    Map<String, Object> record = new HashMap<String, Object>();
    toMap(jsonObject, record);
    return new EntityMapImpl(record);
  }

  protected void toMap(JsonObject jsonObject, Map<String, Object> record) {
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      if (entry.getKey().equals("_source")) {
        toMap((JsonObject) entry.getValue(), record);
        continue;
      }
      Object value = entry.getValue();
      if (entry.getValue() instanceof JsonPrimitive) {
        value = getValue((JsonPrimitive) entry.getValue());
      }
      record.put(entry.getKey(), value);
    }
  }

  protected List<EntityMap> toAggregationEntityMaps(JsonObject jsonObject,
      List<ColumnDefinition> groupColumns) {
    List<EntityMap> entityMaps = new LinkedList<EntityMap>();
    Map<String, Object> head = new HashMap<String, Object>();
    String[] groupNodeNames = extractGroupNodeNames(groupColumns);
    toAggregationEntityMaps(jsonObject, entityMaps, head, groupNodeNames, 0);
    return entityMaps;
  }

  protected String[] extractGroupNodeNames(
      List<ColumnDefinition> groupColumns) {
    String[] groupNodeNames = new String[groupColumns.size()];
    int i = 0;
    for (ColumnDefinition columnDefinition : groupColumns) {
      int index = columnDefinition.getValue().indexOf('.');
      groupNodeNames[i++] = columnDefinition.getValue().substring(index + 1);
    }
    return groupNodeNames;
  }

  protected void toAggregationEntityMaps(JsonObject jsonObject,
      List<EntityMap> entityMaps, Map<String, Object> head,
      String[] groupNodeNames, int offset) {
    if (groupNodeNames.length - 1 == offset) {
      toAggretaionEntityMaps(jsonObject, entityMaps, head,
          groupNodeNames[offset]);
    } else {
      JsonObject groupNode = (JsonObject) jsonObject
          .get(groupNodeNames[offset]);
      JsonArray jsonArray = (JsonArray) groupNode.get("buckets");
      for (int i = 0; i < jsonArray.size(); i++) {
        JsonObject jo = (JsonObject) jsonArray.get(i);
        head.put(groupNodeNames[offset], jo.get("key").getAsString());
        toAggregationEntityMaps(jo, entityMaps, head, groupNodeNames,
            offset + 1);
      }
    }
  }

  protected void toAggretaionEntityMaps(JsonObject jsonObject,
      List<EntityMap> entityMaps, Map<String, Object> head, String key) {
    JsonObject groupNode = (JsonObject) jsonObject.get(key);
    JsonArray jsonArray = (JsonArray) groupNode.get("buckets");
    for (int i = 0; i < jsonArray.size(); i++) {
      JsonObject jo = (JsonObject) jsonArray.get(i);
      Map<String, Object> map = toMap(jo, head, key);
      EntityMap entityMap = new EntityMapImpl(map);
      entityMaps.add(entityMap);
    }
  }

  protected Map<String, Object> toMap(JsonObject jsonObject,
      Map<String, Object> head, String key) {
    Map<String, Object> map = new HashMap<String, Object>(head);
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      if (entry.getKey().equals("key")) {
        map.put(key, entry.getValue().getAsString());
        continue;
      }
      if (entry.getKey().equals(DOC_COUNT)) {
        map.put(DOC_COUNT, entry.getValue().getAsInt());
        continue;
      }
      JsonPrimitive value = ((JsonObject) entry.getValue())
          .getAsJsonPrimitive("value");
      map.put(entry.getKey(), getValue(value));
    }
    return map;
  }

  protected Object getValue(JsonPrimitive value) {
    if (value.isNumber()) {
      Number number = value.getAsNumber();
      double d = number.doubleValue();
      long l = number.longValue();
      if (d - l > 0) {
        return d;
      } else {
        return l;
      }
    } else if (value.isBoolean())
      return value.getAsBoolean();
    else
      return value.getAsString();
  }

  protected Object[] toRecord(Operand[] operands, EntityMap entityMap) {
    Object[] record = new Object[operands.length];
    for (int i = 0; i < operands.length; i++) {
      record[i] = operands[i].operate(entityMap);
    }
    return record;
  }
}
