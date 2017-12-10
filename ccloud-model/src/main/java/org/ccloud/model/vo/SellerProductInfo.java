package org.ccloud.model.vo;

import java.util.List;

import org.ccloud.model.SellerProduct;
//保存采购入库订单显示页面
public class SellerProductInfo {
	
	private String purchaseInstockId;
	private String purchaseInstockDetailId;
	private String productCount;
	private String sellerProductId;
	private String productName;
	private String purchaseOrderDetailId;
	private String warehouseId;
	private String customName;
	private String storeCount;
	private String bigUnit;
	private String smallUnit;
	private String convertRelate;
	private String cpsName;
	private List<SellerProduct> list;
	public String getPurchaseInstockId() {
		return purchaseInstockId;
	}
	public void setPurchaseInstockId(String purchaseInstockId) {
		this.purchaseInstockId = purchaseInstockId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<SellerProduct> getList() {
		return list;
	}
	public void setList(List<SellerProduct> list) {
		this.list = list;
	}
	public String getPurchaseInstockDetailId() {
		return purchaseInstockDetailId;
	}
	public void setPurchaseInstockDetailId(String purchaseInstockDetailId) {
		this.purchaseInstockDetailId = purchaseInstockDetailId;
	}
	public String getSellerProductId() {
		return sellerProductId;
	}
	public void setSellerProductId(String sellerProductId) {
		this.sellerProductId = sellerProductId;
	}
	public String getPurchaseOrderDetailId() {
		return purchaseOrderDetailId;
	}
	public void setPurchaseOrderDetailId(String purchaseOrderDetailId) {
		this.purchaseOrderDetailId = purchaseOrderDetailId;
	}
	public String getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getStoreCount() {
		return storeCount;
	}
	public void setStoreCount(String storeCount) {
		this.storeCount = storeCount;
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
	public String getConvertRelate() {
		return convertRelate;
	}
	public void setConvertRelate(String convertRelate) {
		this.convertRelate = convertRelate;
	}
	public String getCpsName() {
		return cpsName;
	}
	public void setCpsName(String cpsName) {
		this.cpsName = cpsName;
	}
	public String getProductCount() {
		return productCount;
	}
	public void setProductCount(String productCount) {
		this.productCount = productCount;
	}

}
