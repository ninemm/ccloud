package model.vo;

public class Mid_ActivityInfo {
	
	private String ActivityID;// 活动ID
	private String ActivityNo;// 活动编号
	private String ActivityName;// 活动名字
	private String CreateTime;// 活动开始时间
	private String ModifyTime;// 活动修改时间
	private String Flag;// 有效性
	
	public String getActivityID() {
		return ActivityID;
	}
	public void setActivityID(String activityID) {
		ActivityID = activityID;
	}
	public String getActivityNo() {
		return ActivityNo;
	}
	public void setActivityNo(String activityNo) {
		ActivityNo = activityNo;
	}
	public String getActivityName() {
		return ActivityName;
	}
	public void setActivityName(String activityName) {
		ActivityName = activityName;
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
