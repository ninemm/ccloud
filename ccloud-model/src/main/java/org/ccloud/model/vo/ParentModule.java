package org.ccloud.model.vo;

import java.util.List;

public class ParentModule {

	private String name;
	private List<ModuleInfo> list;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ModuleInfo> getList() {
		return list;
	}
	public void setList(List<ModuleInfo> list) {
		this.list = list;
	}
	
	
}
