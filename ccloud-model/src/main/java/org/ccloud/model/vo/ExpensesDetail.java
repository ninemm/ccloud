package org.ccloud.model.vo;

import com.jfinal.kit.StrKit;

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
	private String ApplyAmount;//申请总金额
	private String CreateTime;//创建日期
	private String ModifyTime;//最后修改日期
	private String Flag;//数据有效状态(1:有效，0:无效)
	
	private String item1;
	private String item2;
	private String item3;
	private String item4;
	
	public void setItemInfo() {
		if (StrKit.notBlank(this.CommodityCode)) {
			setItem2(this.CommodityCode);
		} else {
			setItem2(this.ShowType);
		}
		setItem3(this.Quantity);
		setItem4(this.ApplyAmount);
	}
	
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public String getItem2() {
		return item2;
	}
	public void setItem2(String item2) {
		this.item2 = item2;
	}
	public String getItem3() {
		return item3;
	}
	public void setItem3(String item3) {
		this.item3 = item3;
	}
	public String getItem4() {
		return item4;
	}
	public void setItem4(String item4) {
		this.item4 = item4;
	}
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
