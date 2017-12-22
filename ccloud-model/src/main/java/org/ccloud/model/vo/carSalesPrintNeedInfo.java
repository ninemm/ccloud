package org.ccloud.model.vo;

import java.math.BigDecimal;

public class carSalesPrintNeedInfo {

	private String productName;//产品名字
	private Integer bigCount;//大单位数量
	private Integer smallCount;//小单位数量
	private BigDecimal saleAmount;//销售合计总金额
	private String barCode;//商品条码
	private String wareHouseName;//仓库名字
	private String productAmout;//分品项合计金额
	private Integer isGift;//是否赠品
	private String wareHousePhone;//仓库电话
	private String bigUnit;//商品大单位
	private String smallUnit;//商品小单位
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Integer getBigCount() {
		return bigCount;
	}
	public void setBigCount(Integer bigCount) {
		this.bigCount = bigCount;
	}
	public Integer getSmallCount() {
		return smallCount;
	}
	public void setSmallCount(Integer smallCount) {
		this.smallCount = smallCount;
	}
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}
	public String getWareHouseName() {
		return wareHouseName;
	}
	public void setWareHouseName(String wareHouseName) {
		this.wareHouseName = wareHouseName;
	}
	public String getProductAmout() {
		return productAmout;
	}
	public void setProductAmout(String productAmout) {
		this.productAmout = productAmout;
	}
	public Integer getIsGift() {
		return isGift;
	}
	public void setIsGift(Integer isGift) {
		this.isGift = isGift;
	}
	public String getWareHousePhone() {
		return wareHousePhone;
	}
	public void setWareHousePhone(String wareHousePhone) {
		this.wareHousePhone = wareHousePhone;
	}
	public String getBigUnit() {
		return bigUnit;
	}
	public void setBigUnit(String bigUnit) {
		this.bigUnit = bigUnit;
	}
	public String getSmallUnit() {
		return smallUnit;
	}
	public void setSmallUnit(String smallUnit) {
		this.smallUnit = smallUnit;
	}
	
}
