package org.ccloud.model.vo;

import java.util.List;

public class ModuleInfo {

	private String moduleId;
	private String moduleName;
	private List<OperationInfo> list;
	
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public List<OperationInfo> getList() {
		return list;
	}
	public void setList(List<OperationInfo> list) {
		this.list = list;
	}
	
	
}
