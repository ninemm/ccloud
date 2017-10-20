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
package org.ccloud.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.AttachmentUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.GoodsSpecification;
import org.ccloud.model.GoodsSpecificationValue;
import org.ccloud.model.query.GoodsSpecificationQuery;
import org.ccloud.model.query.GoodsSpecificationValueQuery;
import org.ccloud.model.vo.GoodsSpecificationInfo;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/specification", viewPath = "/WEB-INF/admin/specification")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _GoodsSpecificationController extends JBaseCRUDController<GoodsSpecification> { 

	public void list() {
		
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        Page<GoodsSpecification> page = GoodsSpecificationQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "create_date");
        List<GoodsSpecification> ccsList = page.getList();
        List<GoodsSpecificationInfo> specificationInfoList = new ArrayList<>();
        for (GoodsSpecification ccGoodsSpecification : ccsList) {
        	List<GoodsSpecificationValue> childList = GoodsSpecificationValueQuery.me().findByParentId(ccGoodsSpecification.getId());
        	GoodsSpecificationInfo info = new GoodsSpecificationInfo();
        	info.setId(ccGoodsSpecification.getId());
        	info.setName(ccGoodsSpecification.getName());
        	info.setShowType(ccGoodsSpecification.getShowType());
        	info.setType(ccGoodsSpecification.getType());
        	info.setChildList(childList);
        	specificationInfoList.add(info);
		}
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", specificationInfoList);
        renderJson(map);
		
	}
	
	@Override
	public void save() {
		
		List<UploadFile> uploadFiles = getFiles();
		keepPara();
		final GoodsSpecification ccGoodsSpecification = getModel(GoodsSpecification.class);
		String [] valueName = getParaValues("mytext[]");
		String [] orderList = getParaValues("order[]");
		String [] childIdList = getParaValues("childId[]");
		if (ccGoodsSpecification.getId() == null) {
			ccGoodsSpecification.saveOrUpdate();
		} else {
			List<GoodsSpecificationValue> oldIdList = GoodsSpecificationValueQuery.me().findByParentId(ccGoodsSpecification.getId());
			if (childIdList == null) {
				childIdList = new String[] {}; 
			}
			List<GoodsSpecificationValue> deleteIds = getDiffrent(oldIdList, childIdList);
 			GoodsSpecificationValueQuery.me().batchDeleteAndFile(deleteIds);
			ccGoodsSpecification.saveOrUpdate();
		}
		if (valueName != null) {
			for (int i = 0; i < valueName.length; i++) {
				if (StringUtils.isBlank(valueName[i])) {
					continue;
				}
				GoodsSpecificationValue value = new GoodsSpecificationValue();
				value.setName(valueName[i]);
				if (StringUtils.isNotBlank(childIdList[i])) {
					value.setId(childIdList[i]);
				}
				if (uploadFiles.size() > 0) {
					value.setImagePath(AttachmentUtils.moveFile(uploadFiles.get(i)).replace("\\", "/"));
				}
				if (StringUtils.isNotBlank(orderList[i])) {
					if (StringUtils.isNumeric(orderList[i])) {
						value.setOrderList(Integer.parseInt(orderList[i]));
					} else {
						value.setOrderList(0);
					}
				}
				value.setGoodsSpecificationId(ccGoodsSpecification.getId());
				value.saveOrUpdate();
			}
		}
				
		renderAjaxResultForSuccess("ok");
	}
	
	/** 
	 * 获取两个List的不同元素(耗时最低)
	 * @param list1 
	 * @param list2 
	 * @return 
	 */  
	private static List<GoodsSpecificationValue> getDiffrent(List<GoodsSpecificationValue> csvList, String [] childIdList) {
		List<GoodsSpecificationValue> diffValueList = new ArrayList<>();
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = Arrays.asList(childIdList);
		for (GoodsSpecificationValue csv : csvList) {
			list1.add(csv.getId());
		}
		List<String> diff = new ArrayList<String>();  
	    List<String> maxList = list1;  
	    List<String> minList = list2;  
	    if(list2.size()>list1.size()) {  
	         maxList = list2;  
	         minList = list1;  
	    }  
	    Map<String,Integer> map = new HashMap<String,Integer>(maxList.size());  
	    for (String string : maxList) {  
	        map.put(string, 1);  
	    }  
	    for (String string : minList) {  
	        if(map.get(string)!=null) {  
	            map.put(string, 2);  
	            continue;  
	        }  
	        diff.add(string);  
	    }  
	    for(Map.Entry<String, Integer> entry:map.entrySet()) {  
	        if(entry.getValue()==1) {  
	            diff.add(entry.getKey());  
	        }  
	    }
	    
	    for (String string : diff) {
			for (GoodsSpecificationValue bean : csvList) {
				if (string == bean.getId()) {
					diffValueList.add(bean);
				}
			}
		}
	    return diffValueList;  
	}  	
	
	@Override
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			GoodsSpecification goodsSpecification = GoodsSpecificationQuery.me().findById(id);
			setAttr("goodsSpecification", goodsSpecification);
			
			List<GoodsSpecificationValue> childList = GoodsSpecificationValueQuery.me().findByParentId(goodsSpecification.getId());
			setAttr("childList", childList);
		}
	}
	
	@Override
	public void delete() {
		String id = getPara("id");
		final GoodsSpecification ccGoodsSpecification = GoodsSpecificationQuery.me().findById(id);
		ccGoodsSpecification.delete();
		renderAjaxResultForSuccess("删除成功");
		if (ccGoodsSpecification != null) {
            List<String> ids = new ArrayList<>();
            List<GoodsSpecificationValue> cgsList = GoodsSpecificationValueQuery.me().findByParentId(id);
            for (GoodsSpecificationValue spv : cgsList) {
				ids.add(spv.getId());
			}
            if (ids.size() > 0) {
                int count = GoodsSpecificationValueQuery.me().batchDelete(ids);
                if (count > 0) {
                    renderAjaxResultForSuccess("删除成功");
                } else {
                    renderAjaxResultForError("规格值删除失败");
                }
            }
		}
	}	
	
	@Before(UCodeInterceptor.class)
	public void batchDelete() {
		
		String[] ids = getParaValues("dataItem");
		int count = GoodsSpecificationQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}
		
	}
}
