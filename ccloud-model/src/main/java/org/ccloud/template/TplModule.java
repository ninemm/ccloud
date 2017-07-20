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
package org.ccloud.template;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.utils.StringUtils;

import com.jfinal.kit.PathKit;

public class TplModule {
	
	private String title;
	private String name;
	private String listTitle;
	private String addTitle;
	private String commentTitle;
	private String iconClass;
	private String orders;
	private List<TplTaxonomyType> taxonomyTypes;
	private List<TplMetadata> metadatas;
	
	private String moduleName;

	public List<TplTaxonomyType> getTaxonomyTypes() {
		return taxonomyTypes;
	}

	public void setTaxonomyTypes(List<TplTaxonomyType> taxonomys) {
		this.taxonomyTypes = taxonomys;
	}

	public List<TplMetadata> getMetadatas() {
		return metadatas;
	}

	public void setMetadatas(List<TplMetadata> metadatas) {
		this.metadatas = metadatas;
	}

	public List<String> getStyles() {
		List<String> moduleStyles = null;
		File f = new File(PathKit.getWebRootPath(), TemplateManager.me().currentTemplatePath());
		String[] fileNames = f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.startsWith("content_" + name + "_")
						&& !fileName.contains(Consts.TAXONOMY_TEMPLATE_PREFIX);
			}
		});
		if (fileNames != null && fileNames.length > 0) {
			moduleStyles = new ArrayList<String>();
			int start = ("content_" + name + "_").length();
			for (String fileName : fileNames) {
				moduleStyles.add(fileName.substring(start, fileName.lastIndexOf(".")));
			}
		}
		return moduleStyles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getListTitle() {
		return listTitle;
	}

	public void setListTitle(String listTitle) {
		this.listTitle = listTitle;
	}

	public String getAddTitle() {
		return addTitle;
	}

	public void setAddTitle(String addTitle) {
		this.addTitle = addTitle;
	}

	public String getCommentTitle() {
		return commentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		this.commentTitle = commentTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	public String getOrders() {
		return orders;
	}

	public void setOrders(String orders) {
		this.orders = orders;
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public TplTaxonomyType getTaxonomyTypeByType(String name) {
		List<TplTaxonomyType> tts = taxonomyTypes;
		if (null != tts && tts.size() > 0) {
			for (TplTaxonomyType type : tts) {
				if (type.getName().equals(name))
					return type;
			}
		}
		return null;
	}

	public long findContentCount(String status) {
		//return ContentQuery.me().findCountByModuleAndStatus(getName(), status);
		return 0l;
	}
	
	public long findTripRouteCount(String status) {
		//return TripRouteQuery.me().findCountByStatus(status);
		return 0l;
	}

	public long findNormalContentCount() {
//		if (StringUtils.areNotBlank(moduleName)) {
//			return findTripRouteCount(Content.STATUS_NORMAL);
//		}
//		return findContentCount(Content.STATUS_NORMAL);
		return 0l;
	}

	public long findDraftContentCount() {
//		if (StringUtils.areNotBlank(moduleName)) {
//			return findTripRouteCount(Content.STATUS_DRAFT);
//		}
//		return findContentCount(Content.STATUS_DRAFT);
		return 0l;
	}

	public long findDeleteContentCount() {
//		if (StringUtils.areNotBlank(moduleName)) {
//			return findTripRouteCount(Content.STATUS_DELETE);
//		}
//		return findContentCount(Content.STATUS_DELETE);
		return 0l;
	}

	public long findNotDeleteContentCount() {
//		if (StringUtils.areNotBlank(moduleName)) {
//			return TripRouteQuery.me().findCountInNormal();
//		}
//		return ContentQuery.me().findCountInNormalByModule(getName());
		return 0l;
	}

	public boolean isSupportOrder(String order) {

		if (StringUtils.isBlank(orders)) {
			return false;
		}

		if (StringUtils.isBlank(order)) {
			return false;
		}

		String[] orderX = orders.split(",");
		for (int i = 0; i < orderX.length; i++) {
			if (order.equals(orderX[i])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "TplModule [title=" + title + ", name=" + name + ", moduleName=" + moduleName + ", listTitle=" + listTitle + ", addTitle=" + addTitle
				+ ", commentTitle=" + commentTitle + ", orders=" + orders + ", taxonomyTypes=" + taxonomyTypes
				+ ", metadatas=" + metadatas + "]";
	}

}
