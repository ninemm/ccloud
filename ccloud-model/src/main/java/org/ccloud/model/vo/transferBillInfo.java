package org.ccloud.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class transferBillInfo {

	private String transferBillSn;
    private String fromWarehouseId;
    private String toWarehouseId;
    private String bizUserId;
    private Date bizDate;
    private Integer status;
    private Date createDate;
    private String sellerProductId;
    private BigDecimal productCount;
    private String dataArea;
	private List<ProductInfo> productInfos;
	
	
	
	public String getDataArea() {
		return dataArea;
	}
	public void setDataArea(String dataArea) {
		this.dataArea = dataArea;
	}
	public String getTransferBillSn() {
		return transferBillSn;
	}
	public void setTransferBillSn(String transferBillSn) {
		this.transferBillSn = transferBillSn;
	}
	public String getBizUserId() {
		return bizUserId;
	}
	public void setBizUserId(String bizUserId) {
		this.bizUserId = bizUserId;
	}
	public Date getBizDate() {
		return bizDate;
	}
	public void setBizDate(Date bizDate) {
		this.bizDate = bizDate;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getSellerProductId() {
		return sellerProductId;
	}
	public void setSellerProductId(String sellerProductId) {
		this.sellerProductId = sellerProductId;
	}
	
	public BigDecimal getProductCount() {
		return productCount;
	}
	public void setProductCount(BigDecimal productCount) {
		this.productCount = productCount;
	}
	public List<ProductInfo> getProductInfos() {
		return productInfos;
	}
	public void setProductInfos(List<ProductInfo> productInfos) {
		this.productInfos = productInfos;
	}
	public String getFromWarehouseId() {
		return fromWarehouseId;
	}
	public void setFromWarehouseId(String fromWarehouseId) {
		this.fromWarehouseId = fromWarehouseId;
	}
	public String getToWarehouseId() {
		return toWarehouseId;
	}
	public void setToWarehouseId(String toWarehouseId) {
		this.toWarehouseId = toWarehouseId;
	}
	
	
	
}
