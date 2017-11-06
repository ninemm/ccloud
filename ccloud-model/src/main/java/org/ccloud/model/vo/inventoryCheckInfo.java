package org.ccloud.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class inventoryCheckInfo {

	private String billSn;
    private String warehouseId;
    private String bizUserId;
    private Date bizDate;
    private Integer status;
    private Date createDate;
    private String productId;
    private BigDecimal productAmount;
    private Integer productCount;
    private String remark;
	private List<ProductInfo> productInfos;
	public String getBillSn() {
		return billSn;
	}
	public void setBillSn(String billSn) {
		this.billSn = billSn;
	}
	public String getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
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
	public List<ProductInfo> getProductInfos() {
		return productInfos;
	}
	public void setProductInfos(List<ProductInfo> productInfos) {
		this.productInfos = productInfos;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public BigDecimal getProductAmount() {
		return productAmount;
	}
	public void setProductAmount(BigDecimal productAmount) {
		this.productAmount = productAmount;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getProductCount() {
		return productCount;
	}
	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
	}

	
	
}
