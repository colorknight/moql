package org.datayoo.moql.simulation;

import org.apache.commons.lang3.Validate;

public class LittileBean implements Bean {
	
	private String id;
	
	public LittileBean(String id) {
		Validate.notEmpty(id, "id is empty!");
		this.id = id;
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
		return id;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return 0;
	}

  @Override
  public String[] getArray() {
    // TODO Auto-generated method stub
    return null;
  }
	
}
