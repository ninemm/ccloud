package org.ccloud.model.vo;

import java.util.List;

import org.ccloud.model.GoodsSpecificationValue;

public class GoodsSpecificationInfo {
	
	private String id;
	private String name;
	private String showType;
	private String type;
	private List<GoodsSpecificationValue> childList;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<GoodsSpecificationValue> getChildList() {
		return childList;
	}
	public void setChildList(List<GoodsSpecificationValue> childList) {
		this.childList = childList;
	}
	
}
