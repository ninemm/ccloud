package org.ccloud.model.vo;

public class PurchaseSeller{
	private String sellerProductId;
	private String customName;
	private int privateCount;
	public String getSellerProductId() {
		return sellerProductId;
	}
	public void setSellerProductId(String sellerProductId) {
		this.sellerProductId = sellerProductId;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public int getPrivateCount() {
		return privateCount;
	}
	public void setPrivateCount(int privateCount) {
		this.privateCount = privateCount;
	}
	
}