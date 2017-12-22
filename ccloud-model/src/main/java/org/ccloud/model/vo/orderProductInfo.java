package org.ccloud.model.vo;

import java.math.BigDecimal;

public class orderProductInfo {

	private String productName;//商品名称
	private String barCode;//商品条码
	private String bigUnit;//商品大单位
	private String smallUnit;//商品小单位
	private Integer productCount;//下单数量（目前订单详细会把件换算成瓶存）
	private Integer bigCount;//大单位数量
	private Integer smallCount;//小单位数量
	private BigDecimal bigPrice;//大单位价格
	private BigDecimal smallPrice;//小单位价格
	private Integer isgift;//是否赠品
	private Integer convertRelate;//大单位小单位换算关系
	private BigDecimal productAmout;//出库单子表，每个品项的金额合计
	private String sellerProductId;//
	private String outStockId;//出库单主表Id
	private String salesOutDetaliId;//出库单子表Id
	private String wareHouseId;//出库单仓库
	private String productId;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBarCode() {
		return barCode;
	}
	public void setBarCode(String barCode) {
		this.barCode = barCode;
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
	public Integer getProductCount() {
		return productCount;
	}
	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
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
	public BigDecimal getBigPrice() {
		return bigPrice;
	}
	public void setBigPrice(BigDecimal bigPrice) {
		this.bigPrice = bigPrice;
	}
	public BigDecimal getSmallPrice() {
		return smallPrice;
	}
	public void setSmallPrice(BigDecimal smallPrice) {
		this.smallPrice = smallPrice;
	}
	public Integer getIsgift() {
		return isgift;
	}
	public void setIsgift(Integer isgift) {
		this.isgift = isgift;
	}
	public Integer getConvertRelate() {
		return convertRelate;
	}
	public void setConvertRelate(Integer convertRelate) {
		this.convertRelate = convertRelate;
	}
	public BigDecimal getProductAmout() {
		return productAmout;
	}
	public void setProductAmout(BigDecimal productAmout) {
		this.productAmout = productAmout;
	}
	public String getSellerProductId() {
		return sellerProductId;
	}
	public void setSellerProductId(String sellerProductId) {
		this.sellerProductId = sellerProductId;
	}
	public String getOutStockId() {
		return outStockId;
	}
	public void setOutStockId(String outStockId) {
		this.outStockId = outStockId;
	}
	public String getSalesOutDetaliId() {
		return salesOutDetaliId;
	}
	public void setSalesOutDetaliId(String salesOutDetaliId) {
		this.salesOutDetaliId = salesOutDetaliId;
	}
	public String getWareHouseId() {
		return wareHouseId;
	}
	public void setWareHouseId(String wareHouseId) {
		this.wareHouseId = wareHouseId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	
}
