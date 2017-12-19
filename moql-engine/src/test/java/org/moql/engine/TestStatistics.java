package org.moql.engine;

import junit.framework.TestCase;
import org.moql.ColumnDefinition;
import org.moql.RecordSet;
import org.moql.RecordSetDefinition;

import java.lang.reflect.Array;

public class TestStatistics extends TestCase {

	public void testAgeGroup() {/*
		List<Map<String, Object>> records = AudltDataReader.readAudltData("./example-data/audlt.data.txt", -1);
		DataSetMap dataSetMap = new DataSetMapImpl();
		dataSetMap.putDataSet("ds", records);
		String sql = "select ds.age/10*10 ageGrp, count(ds.age) cnt from ds ds group by ageGrp order by ageGrp";
		try {
			Selector selector = MoqlUtils.createSelector(sql);
			selector.select(dataSetMap);
			RecordSet recordSet = selector.getRecordSet();
			outputRecordSet(recordSet);
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void testAgeRange() {/*
		List<Map<String, Object>> records = AudltDataReader.readAudltData("./example-data/audlt.data.txt", -1);
		DataSetMap dataSetMap = new DataSetMapImpl();
		dataSetMap.putDataSet("ds", records);
		String sql = "select 1 cnst, max(ds.age) max, min(ds.age) min, median(ds.age) mid, range(ds.age) range, percentile(ds.age, 90) p90, mode(ds.age) mod from ds ds group by cnst";
		try {
			Selector selector = MoqlUtils.createSelector(sql);
			selector.select(dataSetMap);
			RecordSet recordSet = selector.getRecordSet();
			outputRecordSet(recordSet);
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public void testAgeDistribute() {/*
		List<Map<String, Object>> records = AudltDataReader.readAudltData("./example-data/audlt.data.txt", -1);
		DataSetMap dataSetMap = new DataSetMapImpl();
		dataSetMap.putDataSet("ds", records);
		String sql = "select 1 cnst, avg(ds.age) avg, variance(ds.age) var, standardDeviation(ds.age) sd, semiVariance(ds.age) svar, kurtosis(ds.age) k, skewness(ds.age) s from ds ds group by cnst";
		try {
			Selector selector = MoqlUtils.createSelector(sql);
			selector.select(dataSetMap);
			RecordSet recordSet = selector.getRecordSet();
			outputRecordSet(recordSet);
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	protected void outputRecordSet(RecordSet recordSet) {
		RecordSetDefinition recordSetDefinition = recordSet.getRecordSetDefinition();
		StringBuffer sbuf = new StringBuffer();
		for(ColumnDefinition column : recordSetDefinition.getColumns()) {
			sbuf.append(column.getName());
			sbuf.append("    ");
		}
		System.out.println(sbuf.toString());
		for(Object[] record : recordSet.getRecords()) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < record.length; i++) {
				if (record[i] != null) {
					if (record[i].getClass().isArray()) {
						sb.append(outputArray(record[i]));
					} else {
						sb.append(record[i].toString());
					}
				} else {
					sb.append("NULL");
				}
				sb.append(" ");
			}
			System.out.println(sb.toString());
		}
	}
	
	protected String outputArray(Object obj) {
		StringBuffer sbuf = new StringBuffer();
		for(int i = 0; i < Array.getLength(obj); i++) {
			if (i != 0)
				sbuf.append(",");
			sbuf.append(Array.get(obj, i).toString());
		}
		return sbuf.toString();
	}
}
