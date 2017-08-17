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

import java.util.List;

public class TplTaxonomyType {

	public static final String TYPE_INPUT = "input";
	public static final String TYPE_SELECT = "select";

	private String title;
	private String name;
	private String formType = TYPE_SELECT;
	private TplModule module;

	private List<TplMetadata> metadatas;

	public List<TplMetadata> getMetadatas() {
		return metadatas;
	}

	public void setMetadatas(List<TplMetadata> metadatas) {
		this.metadatas = metadatas;
	}

	public TplTaxonomyType(TplModule module) {
		this.module = module;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TplModule getModule() {
		return module;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public boolean isInputType() {
		return TplTaxonomyType.TYPE_INPUT.equals(getFormType());
	}

	public boolean isSelectType() {
		return TplTaxonomyType.TYPE_SELECT.equals(getFormType());
	}

	@Override
	public String toString() {
		return "TplTaxonomyType [title=" + title + ", name=" + name + ", formType=" + formType + ", metadatas="
				+ metadatas + "]";
	}

}
