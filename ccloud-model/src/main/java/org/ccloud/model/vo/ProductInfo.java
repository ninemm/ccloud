package org.ccloud.model.vo;

import java.math.BigDecimal;
import java.util.Date;

public class ProductInfo {
	private Date createDate;
	private BigDecimal cost;
	private boolean isMarketable;
	private BigDecimal marketPrice;
	private String name;
	private BigDecimal price;
	private String productSn;
	private String store;
	private String storePlace;
	private String weight;
	private String weightUnit;
	private String code;
	private String brandName;
	private String categoryName;
	private String specificationValue;
	private String productId;
	private String bigUnit;
	private String customName;
	private String sellerProductId;
	private BigDecimal storeCount;
	private BigDecimal balanceCount;//库存总账中明细的库存


	public BigDecimal getBalanceCount() {
		return balanceCount;
	}

	public void setBalanceCount(BigDecimal balanceCount) {
		this.balanceCount = balanceCount;
	}

	public BigDecimal getStoreCount() {
		return storeCount;
	}

	public void setStoreCount(BigDecimal storeCount) {
		this.storeCount = storeCount;
	}

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

	public String getBigUnit() {
		return bigUnit;
	}

	public void setBigUnit(String bigUnit) {
		this.bigUnit = bigUnit;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public boolean getIsMarketable() {
		return isMarketable;
	}

	public void setIsMarketable(boolean isMarketable) {
		this.isMarketable = isMarketable;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getProductSn() {
		return productSn;
	}

	public void setProductSn(String productSn) {
		this.productSn = productSn;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getStorePlace() {
		return storePlace;
	}

	public void setStorePlace(String storePlace) {
		this.storePlace = storePlace;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}


	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSpecificationValue() {
		return specificationValue;
	}

	public void setSpecificationValue(String specificationValue) {
		this.specificationValue = specificationValue;
	}

	public void setMarketable(boolean isMarketable) {
		this.isMarketable = isMarketable;
	}

	
	
}
