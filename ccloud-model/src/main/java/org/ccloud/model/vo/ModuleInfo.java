package org.ccloud.model.vo;

import java.util.List;

public class ModuleInfo {

	private String moduleId;
	private String moduleName;
	private List<OperationInfo> list;
	private String systemName;
	private int systemRowSpan;
	private String parentModuleName;
	private int parentRowSpan;
	
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public int getSystemRowSpan() {
		return systemRowSpan;
	}
	public void setSystemRowSpan(int systemRowSpan) {
		this.systemRowSpan = systemRowSpan;
	}
	public String getParentModuleName() {
		return parentModuleName;
	}
	public void setParentModuleName(String parentModuleName) {
		this.parentModuleName = parentModuleName;
	}
	public int getParentRowSpan() {
		return parentRowSpan;
	}
	public void setParentRowSpan(int parentRowSpan) {
		this.parentRowSpan = parentRowSpan;
	}
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
