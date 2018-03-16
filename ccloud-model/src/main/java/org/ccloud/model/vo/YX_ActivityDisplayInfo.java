package org.ccloud.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;


public class YX_ActivityDisplayInfo implements Serializable{
	private BigInteger IDNO;
	private BigInteger FlowIDNO;
	private String CustomerName;
	private String CustomerCode;
	private BigInteger ShopID;
	private Integer ChannelID;
	private Integer InvestState;
	private String CancleReason;
	private Integer ResourceFlag;
	private BigInteger CreateManID;
	private Date CreateTime;
	private BigInteger ShopOrderID;
	private String ActivityPhotos;
	private String SignPhotos;
	private BigInteger ModifyManID;
	private Date ModifyTime;
	private Integer Flag;
	private Integer ChannelTypeID;
	private BigDecimal WriteOffAmount;
	private Date ShopCreateTime;
	private String ShopLinkMan;
	private String ShopPhone;
	private Integer ShopVisitCount;
	private Integer ShopXCJHCount;
	private String OrderMan;
	private String ResourceID;
	private String ProvinceName;
	private String CityName;
	private String CountyName;
	private Date SysCreateTime;
	private Date SysModifyTime;
	
	public BigInteger getIDNO() {
		return IDNO;
	}
	public void setIDNO(BigInteger iDNO) {
		IDNO = iDNO;
	}
	public BigInteger getFlowIDNO() {
		return FlowIDNO;
	}
	public void setFlowIDNO(BigInteger flowIDNO) {
		FlowIDNO = flowIDNO;
	}
	public String getCustomerName() {
		return CustomerName;
	}
	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}
	public String getCustomerCode() {
		return CustomerCode;
	}
	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}
	public BigInteger getShopID() {
		return ShopID;
	}
	public void setShopID(BigInteger shopID) {
		ShopID = shopID;
	}
	public Integer getChannelID() {
		return ChannelID;
	}
	public void setChannelID(Integer channelID) {
		ChannelID = channelID;
	}
	public Integer getInvestState() {
		return InvestState;
	}
	public void setInvestState(Integer investState) {
		InvestState = investState;
	}
	public String getCancleReason() {
		return CancleReason;
	}
	public void setCancleReason(String cancleReason) {
		CancleReason = cancleReason;
	}
	public Integer getResourceFlag() {
		return ResourceFlag;
	}
	public void setResourceFlag(Integer resourceFlag) {
		ResourceFlag = resourceFlag;
	}
	public BigInteger getCreateManID() {
		return CreateManID;
	}
	public void setCreateManID(BigInteger createManID) {
		CreateManID = createManID;
	}
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
	public BigInteger getShopOrderID() {
		return ShopOrderID;
	}
	public void setShopOrderID(BigInteger shopOrderID) {
		ShopOrderID = shopOrderID;
	}
	public String getActivityPhotos() {
		return ActivityPhotos;
	}
	public void setActivityPhotos(String activityPhotos) {
		ActivityPhotos = activityPhotos;
	}
	public String getSignPhotos() {
		return SignPhotos;
	}
	public void setSignPhotos(String signPhotos) {
		SignPhotos = signPhotos;
	}
	public BigInteger getModifyManID() {
		return ModifyManID;
	}
	public void setModifyManID(BigInteger modifyManID) {
		ModifyManID = modifyManID;
	}
	public Date getModifyTime() {
		return ModifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		ModifyTime = modifyTime;
	}
	public Integer getFlag() {
		return Flag;
	}
	public void setFlag(Integer flag) {
		Flag = flag;
	}
	public Integer getChannelTypeID() {
		return ChannelTypeID;
	}
	public void setChannelTypeID(Integer channelTypeID) {
		ChannelTypeID = channelTypeID;
	}
	public BigDecimal getWriteOffAmount() {
		return WriteOffAmount;
	}
	public void setWriteOffAmount(BigDecimal writeOffAmount) {
		WriteOffAmount = writeOffAmount;
	}
	public Date getShopCreateTime() {
		return ShopCreateTime;
	}
	public void setShopCreateTime(Date shopCreateTime) {
		ShopCreateTime = shopCreateTime;
	}
	public String getShopLinkMan() {
		return ShopLinkMan;
	}
	public void setShopLinkMan(String shopLinkMan) {
		ShopLinkMan = shopLinkMan;
	}
	public String getShopPhone() {
		return ShopPhone;
	}
	public void setShopPhone(String shopPhone) {
		ShopPhone = shopPhone;
	}
	public Integer getShopVisitCount() {
		return ShopVisitCount;
	}
	public void setShopVisitCount(Integer shopVisitCount) {
		ShopVisitCount = shopVisitCount;
	}
	public Integer getShopXCJHCount() {
		return ShopXCJHCount;
	}
	public void setShopXCJHCount(Integer shopXCJHCount) {
		ShopXCJHCount = shopXCJHCount;
	}
	public String getOrderMan() {
		return OrderMan;
	}
	public void setOrderMan(String orderMan) {
		OrderMan = orderMan;
	}
	public String getResourceID() {
		return ResourceID;
	}
	public void setResourceID(String resourceID) {
		ResourceID = resourceID;
	}
	public String getProvinceName() {
		return ProvinceName;
	}
	public void setProvinceName(String provinceName) {
		ProvinceName = provinceName;
	}
	public String getCityName() {
		return CityName;
	}
	public void setCityName(String cityName) {
		CityName = cityName;
	}
	public String getCountyName() {
		return CountyName;
	}
	public void setCountyName(String countyName) {
		CountyName = countyName;
	}
	public Date getSysCreateTime() {
		return SysCreateTime;
	}
	public void setSysCreateTime(Date sysCreateTime) {
		SysCreateTime = sysCreateTime;
	}
	public Date getSysModifyTime() {
		return SysModifyTime;
	}
	public void setSysModifyTime(Date sysModifyTime) {
		SysModifyTime = sysModifyTime;
	}
}
