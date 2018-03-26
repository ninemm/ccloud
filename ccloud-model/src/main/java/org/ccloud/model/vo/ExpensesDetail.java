package org.ccloud.model.vo;

public class ExpensesDetail {
	private String ExpenseDetailID;//费用投入明细ID
	private String ExpenseID;//费用投入主ID
	private String FlowID;//流程ID
	private String FlowTypeID;//流程分类
	private String CostType;//费用类型
	private String CommodityCode;//产品编码
	private String ChannelID;//渠道ID
	private String Quantity;//申请数量
	private String ShowType;//展示形式
	
	public String getCommodityCode() {
		return CommodityCode;
	}
	public void setCommodityCode(String commodityCode) {
		CommodityCode = commodityCode;
	}
	public String getChannelID() {
		return ChannelID;
	}
	public void setChannelID(String channelID) {
		ChannelID = channelID;
	}
	public String getQuantity() {
		return Quantity;
	}
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}
	public String getShowType() {
		return ShowType;
	}
	public void setShowType(String showType) {
		ShowType = showType;
	}
	private String ApplyAmount;//申请总金额
	private String CreateTime;//创建日期
	private String ModifyTime;//最后修改日期
	private String Flag;//数据有效状态(1:有效，0:无效)
	
	public String getExpenseDetailID() {
		return ExpenseDetailID;
	}
	public void setExpenseDetailID(String expenseDetailID) {
		ExpenseDetailID = expenseDetailID;
	}
	public String getExpenseID() {
		return ExpenseID;
	}
	public void setExpenseID(String expenseID) {
		ExpenseID = expenseID;
	}
	public String getFlowID() {
		return FlowID;
	}
	public void setFlowID(String flowID) {
		FlowID = flowID;
	}
	public String getFlowTypeID() {
		return FlowTypeID;
	}
	public void setFlowTypeID(String flowTypeID) {
		FlowTypeID = flowTypeID;
	}
	public String getCostType() {
		return CostType;
	}
	public void setCostType(String costType) {
		CostType = costType;
	}
	public String getApplyAmount() {
		return ApplyAmount;
	}
	public void setApplyAmount(String applyAmount) {
		ApplyAmount = applyAmount;
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
