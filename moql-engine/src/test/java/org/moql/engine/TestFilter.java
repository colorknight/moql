package org.moql.engine;

import junit.framework.TestCase;
import org.moql.EntityMap;
import org.moql.EntityMapImpl;
import org.moql.Filter;
import org.moql.MoqlException;
import org.moql.simulation.BeanA;

public class TestFilter extends TestCase {
	
	protected EntityMap entityMap;

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		entityMap = new EntityMapImpl();
		entityMap.putEntity("num", 123);
		entityMap.putEntity("bean", new BeanA("bean", 100));
	}

	public void testCompareFilter() {
		
		try {
			Filter filter1 = MoqlEngine.createFilter("num = 123");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num < 160");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.num > 0 ");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num <> 123");
			assertFalse(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num >= 123");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num <= 100");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testBetweenFilter() {
		
		try {
			Filter filter1 = MoqlEngine.createFilter("bean.num Between 0 and 200");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.num between 0 and 100");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testLikeFilter() {
		
		try {
			Filter filter1 = MoqlEngine.createFilter("bean.name like '%ean'");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.name like '%ea.'");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.name like '%e%'");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.name Like '%c%'");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testInFilter() {
		
		try {
			Filter filter1 = MoqlEngine.createFilter("bean.name in ('Abean', 'Bbean')");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.name IN ('Cbean', 'Bbean')");
			assertFalse(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("50 in (bean.getArray())");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("150 in (bean.getArray())");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testIsFilter() {
		
		try {
			Filter filter1 = MoqlEngine.createFilter("bean.name is null");
			assertFalse(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.name Is not null");
			assertTrue(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testAndFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("num > 100 and bean.num < 200 and bean.name like'%ean'");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.num > 0 and bean.num < 100");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testOrFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("num > 150 or bean.num = 100");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num > 150 or bean.num <> 100");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testNotFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("not bean.num <> 100");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("not num > 150 or bean.num = 100");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("not (num > 150 Or bean.num = 100)");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testParenFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("bean.num < 100 and num > 100 or bean.num = 100");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("bean.num < 100 AND (num > 100 or bean.num = 100)");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testRegexFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("num > 100 and regex(bean.name,'.b\\w+')");
			assertTrue(filter1.isMatch(entityMap));
			filter1 = MoqlEngine.createFilter("num > 100 and regex(bean.name,'.c\\w+')");
			assertFalse(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testTypeConvertFilter() {
		try {
			Filter filter1 = MoqlEngine.createFilter("1 = '1'");
			assertTrue(filter1.isMatch(entityMap));
		} catch (MoqlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
