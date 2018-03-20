package org.ccloud.model.vo;

public class Mid_FlowTypeInfo {

	private String flowTypeID;// 流程类型ID
	private String flowTypeName;// 流程类型名字
	private String parentID;// 父ID
	private String memo;// 描述
	private String createTime;// 创建时间
	private String modifyTime;// 修改时间
	private String flag;// 有效性
	private String YX_FlowTypeID;
	private String YX_ParentID;
	
	public String getFlowTypeID() {
		return flowTypeID;
	}
	public void setFlowTypeID(String flowTypeID) {
		this.flowTypeID = flowTypeID;
	}
	public String getFlowTypeName() {
		return flowTypeName;
	}
	public void setFlowTypeName(String flowTypeName) {
		this.flowTypeName = flowTypeName;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getYX_FlowTypeID() {
		return YX_FlowTypeID;
	}
	public void setYX_FlowTypeID(String yX_FlowTypeID) {
		YX_FlowTypeID = yX_FlowTypeID;
	}
	public String getYX_ParentID() {
		return YX_ParentID;
	}
	public void setYX_ParentID(String yX_ParentID) {
		YX_ParentID = yX_ParentID;
	}
	
}
