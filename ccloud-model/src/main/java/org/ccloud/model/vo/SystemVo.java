package org.ccloud.model.vo;

import java.util.List;

public class SystemVo {
	
	private String name;
	private List<ParentModule> list;
	private int operationCount;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ParentModule> getList() {
		return list;
	}
	public void setList(List<ParentModule> list) {
		this.list = list;
	}
	public int getOperationCount() {
		return operationCount;
	}
	public void setOperationCount(int operationCount) {
		this.operationCount = operationCount;
	}
	
}
