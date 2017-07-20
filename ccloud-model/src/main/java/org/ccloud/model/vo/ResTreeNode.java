/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.model.vo;

import java.util.List;

public class ResTreeNode {
	
	/**资源名称*/
	private String text;
	
	/** 节点上的图标 */
	private String icon;
	
	/** 节点被选择后显示的图标 */
	private String selectedIcon;
	private String href;
	
	/** 树节点的前景色 */
	private String color;
	
	/** 树节点的背景色 */
	private String backColor;
	
	/** 在树节点右边添加额外信息 */
	private String tags;
	
	/** 树节点是否可选择 */
	private Boolean selectable;
	
	private ResTreeNodeState state;
	
	private List<ResTreeNode> nodes;
	
	public ResTreeNode() {
		super();
	}
	
	public ResTreeNode(String text, String href, String color, String backColor, 
			String tags, Boolean selectable, ResTreeNodeState state) {
		super();
		this.text = text;
		this.href = href;
		this.color = color;
		this.backColor = backColor;
		this.tags = tags;
		this.selectable = selectable;
		this.state = state;
	}

	public ResTreeNode(String text, String icon, String selectedIcon, String href, 
			String color, String backColor,	String tags, Boolean selectable, ResTreeNodeState state) {
		super();
		this.text = text;
		this.icon = icon;
		this.selectedIcon = selectedIcon;
		this.href = href;
		this.color = color;
		this.backColor = backColor;
		this.tags = tags;
		this.selectable = selectable;
		this.state = state;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSelectedIcon() {
		return selectedIcon;
	}
	public void setSelectedIcon(String selectedIcon) {
		this.selectedIcon = selectedIcon;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getBackColor() {
		return backColor;
	}
	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Boolean getSelectable() {
		return selectable;
	}
	public void setSelectable(Boolean selectable) {
		this.selectable = selectable;
	}

	public ResTreeNodeState getState() {
		return state;
	}

	public void setState(ResTreeNodeState state) {
		this.state = state;
	}

	public List<ResTreeNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ResTreeNode> nodes) {
		this.nodes = nodes;
	}

	

	
}
