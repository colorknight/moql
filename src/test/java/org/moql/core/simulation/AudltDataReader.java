package org.moql.core.simulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AudltDataReader {
	
	public static String[] names = new String[] {
		"age",
		"workclass",
		"fnlwgt",		//	final weight
		"education",
		"education-num",
		"marital-status",
		"occupation",
		"relationship",
		"race",			//	种族
		"sex",
		"capital-gain",	//	资本利得
		"capital-loss",	//	价值损失
		"hours-per-week",
		"native-country",
		"label"
	};

	public static List<Map<String, Object>> readAudltData(String filePath, int lineNums) {
		List<Map<String, Object>> dataList = new LinkedList<Map<String, Object>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			while(true) {
				String line = br.readLine();
				if (line == null)
					break;
				Map<String, Object> record = parseLine(line);
				if (record == null)
					continue;
				dataList.add(record);
				if (lineNums > 0) {
					lineNums--;
					if (lineNums == 0)
						break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}
	
	protected static Map<String, Object> parseLine(String line) {
		Map<String, Object> record = new HashMap<String, Object>();
		String[] fields = line.split(",");
		if(fields.length != names.length)
			return null;
		for(int i = 0; i < fields.length; i++) {
			if (i == 0 || i == 2 || i==4 || i == 10 || i == 11 || i == 12)
				record.put(names[i], Long.valueOf(fields[i].trim()));
			else
				record.put(names[i], fields[i].trim());
		}
		return record;
	}
}
