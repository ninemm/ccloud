/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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
package ccloud.controller.admin;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.model.ActivityExecuteTemplate;

import com.jfinal.aop.Before;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/cc_activity_execute_template", viewPath = "/WEB-INF/admin/cc_activity_execute_template")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ActivityExecuteTemplateController extends JBaseCRUDController<ActivityExecuteTemplate> { 

	
}
