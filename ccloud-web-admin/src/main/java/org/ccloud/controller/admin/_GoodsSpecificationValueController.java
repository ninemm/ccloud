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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.interceptor.UCodeInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.template.TemplateManager;
import org.ccloud.template.Thumbnail;
import org.ccloud.utils.AttachmentUtils;
import org.ccloud.utils.FileUtils;
import org.ccloud.utils.ImageUtils;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.GoodsSpecification;
import org.ccloud.model.GoodsSpecificationValue;
import org.ccloud.model.ProductGoodsSpecificationValue;
import org.ccloud.model.query.GoodsSpecificationQuery;
import org.ccloud.model.query.GoodsSpecificationValueQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.ProductGoodsSpecificationValueQuery;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/specificationValue", viewPath = "/WEB-INF/admin/specification_value")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
@RequiresPermissions(value={"/admin/specificationValue","/admin/all"},logical=Logical.OR)
public class _GoodsSpecificationValueController extends JBaseCRUDController<GoodsSpecificationValue> {
	private static final Log log = Log.getLog(_GoodsSpecificationValueController.class);
	
	@Override
	public void index() {
		render("index.html");
	}

	public void list() {

		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		Page<GoodsSpecificationValue> page = GoodsSpecificationValueQuery.me().paginate(getPageNumber(), getPageSize(),
				keyword, "order_list");
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);

	}

	@Override
	public void save() {

		UploadFile uploadFile = getFile();
		keepPara();
		final GoodsSpecificationValue ccGoodsSpecificationValue = getModel(GoodsSpecificationValue.class);

		if (uploadFile != null) {
			String newPath = AttachmentUtils.moveFile(uploadFile);
			ccGoodsSpecificationValue.setImagePath(newPath.replace("\\", "/"));

			// processImage(newPath);
		}

		if (ccGoodsSpecificationValue.saveOrUpdate()) {
			renderAjaxResultForSuccess("ok");
		} else {
			renderAjaxResultForError("false");
		}
	}

	@SuppressWarnings("unused")
	private void processImage(String newPath) {
		if (!AttachmentUtils.isImage(newPath))
			return;

		if (".gif".equalsIgnoreCase(FileUtils.getSuffix(newPath))) {
			// 过滤 .gif 图片
			return;
		}

		try {
			// 缩略图
			processThumbnail(newPath);
		} catch (Throwable e) {
			log.error("processThumbnail error", e);
		}
		try {
			// 水印
			processWatermark(newPath);
		} catch (Throwable e) {
			log.error("processWatermark error", e);
		}
	}

	private void processThumbnail(String newPath) {
		List<Thumbnail> tbs = TemplateManager.me().currentTemplate().getThumbnails();
		if (tbs != null && tbs.size() > 0) {
			for (Thumbnail tb : tbs) {
				try {
					String newSrc = ImageUtils.scale(PathKit.getWebRootPath() + newPath, tb.getWidth(), tb.getHeight());
					processWatermark(FileUtils.removeRootPath(newSrc));
				} catch (IOException e) {
					log.error("processWatermark error", e);
				}
			}
		}
	}

	public void processWatermark(String newPath) {
		Boolean watermark_enable = OptionQuery.me().findValueAsBool("watermark_enable");
		if (watermark_enable != null && watermark_enable) {

			int position = OptionQuery.me().findValueAsInteger("watermark_position");
			String watermarkImg = OptionQuery.me().findValue("watermark_image");
			String srcImageFile = newPath;

			Float transparency = OptionQuery.me().findValueAsFloat("watermark_transparency");
			if (transparency == null || transparency < 0 || transparency > 1) {
				transparency = 1f;
			}

			srcImageFile = PathKit.getWebRootPath() + srcImageFile;

			File watermarkFile = new File(PathKit.getWebRootPath(), watermarkImg);
			if (!watermarkFile.exists()) {
				return;
			}

			ImageUtils.pressImage(watermarkFile.getAbsolutePath(), srcImageFile, srcImageFile, position, transparency);
		}
	}
	
	@Override
	@RequiresPermissions(value={"/admin/specificationValue/edit","/admin/all"},logical=Logical.OR)
	public void delete() {
		String id = getPara("id");
		List<ProductGoodsSpecificationValue> list = ProductGoodsSpecificationValueQuery.me().findBySId(id);
		if (list.size() > 0) {
			renderAjaxResultForError("已有货品拥有此规格值");
			return;
		}
		final GoodsSpecificationValue r = GoodsSpecificationValueQuery.me().findById(id);
		if (r != null) {
			if (StringUtils.isNotBlank(r.getImagePath())) {
				File file1 = new File(PathKit.getWebRootPath()+r.getImagePath());
				if (file1.exists() && file1.isFile()) {
					file1.delete();
				}
			}			
			boolean success = r.delete();
            if (success) {
                renderAjaxResultForSuccess("删除成功");
            } else {
                renderAjaxResultForError("删除失败");
            }
		}		
	}

	@Override
	@RequiresPermissions(value={"/admin/specificationValue/edit","/admin/all"},logical=Logical.OR)
	public void edit() {
		String id = getPara("id");
		if (id != null) {
			GoodsSpecificationValue goodsSpecificationValue = GoodsSpecificationValueQuery.me().findById(id);
			setAttr("goodsSpecificationValue", goodsSpecificationValue);
		}
		List<GoodsSpecification> list = GoodsSpecificationQuery.me().findAll();
		setAttr("list", list);
	}

	@Before(UCodeInterceptor.class)
	@RequiresPermissions(value={"/admin/specificationValue/edit","/admin/all"},logical=Logical.OR)
	public void batchDelete() {

		String[] ids = getParaValues("dataItem");
		int count = GoodsSpecificationValueQuery.me().batchDelete(ids);
		if (count > 0) {
			renderAjaxResultForSuccess("删除成功");
		} else {
			renderAjaxResultForError("删除失败!");
		}

	}
}
