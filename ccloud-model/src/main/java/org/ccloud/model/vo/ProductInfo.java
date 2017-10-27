package org.ccloud.model.vo;

import java.math.BigDecimal;
import java.util.List;

import org.ccloud.model.GoodsSpecificationValue;

public class ProductInfo {
	private String createDate;
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
	private List<GoodsSpecificationValue> specificationList;

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
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

	public List<GoodsSpecificationValue> getSpecificationList() {
		return specificationList;
	}

	public void setSpecificationList(List<GoodsSpecificationValue> specificationList) {
		this.specificationList = specificationList;
	}

}
