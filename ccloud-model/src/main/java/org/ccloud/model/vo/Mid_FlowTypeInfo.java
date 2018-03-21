package org.ccloud.model.vo;

public class Mid_FlowTypeInfo {

	private String FlowTypeID;// 流程类型ID
	private String FlowTypeName;// 流程类型名字
	private String ParentID;// 父ID
	private String Memo;// 描述
	private String CreateTime;// 创建时间
	private String ModifyTime;// 修改时间
	private String Flag;// 有效性
	
	public String getFlowTypeID() {
		return FlowTypeID;
	}
	public void setFlowTypeID(String flowTypeID) {
		FlowTypeID = flowTypeID;
	}
	public String getFlowTypeName() {
		return FlowTypeName;
	}
	public void setFlowTypeName(String flowTypeName) {
		FlowTypeName = flowTypeName;
	}
	public String getParentID() {
		return ParentID;
	}
	public void setParentID(String parentID) {
		ParentID = parentID;
	}
	public String getMemo() {
		return Memo;
	}
	public void setMemo(String memo) {
		Memo = memo;
	}
	public String getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(String createTime) {
		CreateTime = createTime;
	}
	public String getModifyTime() {
		return ModifyTime;
	}
	public void setModifyTime(String modifyTime) {
		ModifyTime = modifyTime;
	}
	public String getFlag() {
		return Flag;
	}
	public void setFlag(String flag) {
		Flag = flag;
	}
	
}
