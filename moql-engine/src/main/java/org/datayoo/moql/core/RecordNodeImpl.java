/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.datayoo.moql.core;

import org.apache.commons.lang3.Validate;
import org.datayoo.moql.RecordNode;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RecordNodeImpl implements RecordNode, Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] columns;
	private RecordNode parent;
	private List<RecordNode> children = new LinkedList<RecordNode>();
	
	public RecordNodeImpl(Object[] columns) {
		setColumns(columns);
	}
	
	public RecordNodeImpl() {}
	
	public void addChild(RecordNodeImpl child) {
		// TODO Auto-generated method stub
		child.setParent(this);
		children.add(child);
	}

	public RecordNode getChild(int index) {
		// TODO Auto-generated method stub
		return children.get(index);
	}

	public List<RecordNode> getChildren() {
		// TODO Auto-generated method stub
		return children;
	}
	
	public Object[] getColumns() {
		return columns;
	}

	public RecordNode getParent() {
		// TODO Auto-generated method stub
		return parent;
	}

	public boolean isLeaf() {
		// TODO Auto-generated method stub
		if (children.size() == 0)
			return true;
		return false;
	}

	public void setChildren(List<RecordNode> children) {
		// TODO Auto-generated method stub
		Validate.notNull(children, "children is null!");
		this.children = children;
	}

	public void setColumns(Object[] columns) {
		Validate.notNull(columns, "columns is null!");
		this.columns = columns;
	}

	public void setParent(RecordNodeImpl parent) {
		// TODO Auto-generated method stub
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj == null)
			return false;
		if (!(obj instanceof RecordNode))
			return false;
		RecordNode rn = (RecordNode)obj;
		return Objects.deepEquals(rn.getColumns(), columns);
	}

	
}
