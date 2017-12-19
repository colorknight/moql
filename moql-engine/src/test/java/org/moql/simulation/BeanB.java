package org.moql.simulation;

import org.apache.commons.lang.Validate;

public class BeanB implements Bean {
	
	private String id;
	
	private String name;
	
	private int num = 0;
	
	private String[] array = null;
	
	public BeanB(String id, int num) {
		Validate.notEmpty(id, "id is empty!");
		Validate.isTrue(num > -1, "num is negative!");
		
		this.id = id;
		this.name = 'B'+id;
		this.num = num;
		array = new String[num];
		for(int i = 0; i < num; i++) {
			array[i] = String.valueOf(i);
		}
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	@Override
	public String[] getArray() {
		// TODO Auto-generated method stub
		return array;
	}
	
}
