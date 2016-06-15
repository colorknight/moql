package org.moql.ext.test;

import junit.framework.TestCase;

import org.moql.MoqlException;
import org.moql.Selector;
import org.moql.service.MoqlUtils;
import org.moql.sql.SqlDialectType;

public class ExtendedTest extends TestCase {

	public void testGrammar() {
		String sql = "select l.仿冒网站IP数量,r.仿冒页面数量 from lInput l  full join rInput r";
		try {
			Selector selector = MoqlUtils.createSelector(sql);
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testSelectAsterisk() {
		String sql = "select l.*, r.* from lInput l, rInput r";
		try {
			String xml = MoqlUtils.translateMoql2Xml(sql);
			System.out.println(xml);
			sql = MoqlUtils.translateXml2Sql(xml, SqlDialectType.ORACLE);
			System.out.println(sql);
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
