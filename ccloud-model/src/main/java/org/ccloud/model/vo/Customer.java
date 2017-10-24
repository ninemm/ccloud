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

import java.util.ArrayList;
import java.util.List;

import org.ccloud.model.CcCustomerType;

/**
 * 存档、归档。 和数据库无关的实体类。
 * 
 * @author Chen.XueBing
 */
public class Customer {

	private String customerName; // 客户名称
	private String contact; // 联系人
	private String mobile; // 手机号
	private String customerTypeName; // 客户类型
	private String provName; // 省(名称)
	private String cityName; // 市(名称)
	private String countryName; // 区(名称)
	private String address; // 详细地址
	private List<CcCustomerType> customerTypeList; // 客户类型
	//private List<user> userList; // 客户类型
	
	private String isEnabled; // 是否启用
	private String isArchive; // 是否归档

	public Customer() {
	}

}
