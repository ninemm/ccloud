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

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.ImageIcon;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jfinal.kit.Kv;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.message.Actions;
import org.ccloud.message.MessageKit;
import org.ccloud.model.*;
import org.ccloud.model.query.*;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import org.ccloud.workflow.service.WorkFlowService;
import org.joda.time.DateTime;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/customerVisit", viewPath = "/WEB-INF/admin/customer_visit")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _CustomerVisitController extends JBaseCRUDController<CustomerVisit> {

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void index() {
		render("customer_visit.html");
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void list() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
        String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }

        String questionType = getPara("questionType");

        if (StrKit.notBlank(questionType)) {
        	questionType = StringUtils.urlDecode(questionType);
            setAttr("questionType", questionType);
        }

        String customerType = getPara("customerType");
        if (StrKit.notBlank(customerType)) {
        	customerType = StringUtils.urlDecode(customerType);
            setAttr("customerType", customerType);
        }

        String status = getPara("status");
        if(StrKit.notBlank(status)) {
        	status = StringUtils.urlDecode(status);
        	setAttr("status", status);
		}
        String bizUser = getPara("bizUser");
        if(StrKit.notBlank(bizUser)) {
        	bizUser = StringUtils.urlDecode(bizUser);
        	setAttr("bizUser", bizUser);
		}
        Page<CustomerVisit> page = CustomerVisitQuery.me().paginate(getPageNumber(), getPageSize(), keyword, selectDataArea, customerType, questionType, "id", "cc_v.create_date desc", status,bizUser);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}

	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void audit() {

		keepPara();

		String id = getPara("id");

		if (StrKit.isBlank(id)) {
			renderError(404);
			return ;
		}

		CustomerVisit customerVisit = CustomerVisitQuery.me().findMoreById(id);
		if (customerVisit == null) {
			renderError(404);
			return ;
		}

		String dataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString() + "%";
		List<String> typeList = CustomerJoinCustomerTypeQuery.me().findCustomerTypeNameListBySellerCustomerId(customerVisit.getSellerCustomerId(), dataArea);
		String imageListStore = customerVisit.getPhoto();
		List<ImageJson> list = JSON.parseArray(imageListStore, ImageJson.class);
		List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findByCustomerVisitId(id);
		ExpenseDetail expenseDetail = new ExpenseDetail();
		if(CustomerVisitQuery.me().findById(id).getActiveApplyId()!="" && StrKit.notBlank(CustomerVisitQuery.me().findById(id).getActiveApplyId())) {
			if(StrKit.notBlank((ActivityApplyQuery.me().findById(CustomerVisitQuery.me().findById(id).getActiveApplyId()).getExpenseDetailId()))){
				expenseDetail = ExpenseDetailQuery.me().findById(ActivityApplyQuery.me().findById(CustomerVisitQuery.me().findById(id).getActiveApplyId()).getExpenseDetailId());
				setAttr("expenseDetail",expenseDetail);
			}
		}
		
		setAttr("customerVisit", customerVisit);
		setAttr("cTypeName", Joiner.on(",").join(typeList.iterator()));
		setAttr("list",list);
		setAttr("activityExecutes",activityExecutes);
		User user1 = getSessionAttr(Consts.SESSION_LOGINED_USER);
		//审核后将message中是否阅读改为是
		Message message=MessageQuery.me().findByObjectIdAndToUserId(id, user1.getId());
		if (null!=message) {
			message.setIsRead(Consts.IS_READ);
			message.update();
		}
		
		render("audit.html");
	}

	public void image() {

		String typeDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString();

		Map<String, Object> all = new HashMap<>();
		all.put("text", "全部");
		all.put("id", "");

		List<Record> typeList = CustomerTypeQuery.me().findCustomerTypeList(typeDataArea);
		List<Map<String, Object>> customerTypeList = new ArrayList<>();
		customerTypeList.add(all);

		for(Record customerType : typeList){
			Map<String, Object> item = new HashMap<>();
			item.put("id", customerType.getStr("id"));
			item.put("text", customerType.getStr("name"));
			customerTypeList.add(item);
		}
		setAttr("customerType", JSON.toJSON(customerTypeList));

		List<Record> nameList = SellerCustomerQuery.me().findName(getSessionAttr(Consts.SESSION_SELECT_DATAAREA).toString(), null);
		List<Map<String, Object>> customerList = new ArrayList<>();
		customerList.add(all);

		for(Record name : nameList) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", name.getStr("id"));
			item.put("text", name.getStr("name"));
			customerList.add(item);
		}

		setAttr("customerName", JSON.toJSON(customerList));

		List<Dict> dictList = DictQuery.me().findDictByType("customer_visit");
		List<Map<String, Object>> questionTypeList = new ArrayList<>();
		questionTypeList.add(all);

		for(Dict questionType : dictList) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", questionType.getValue());
			item.put("text", questionType.getName());
			questionTypeList.add(item);
		}

		setAttr("questionType", JSON.toJSON(questionTypeList));
		render("image.html");
	}

	public void getCustomer(){
		String customerType = getPara("customerType");
		List<Record> nameList = SellerCustomerQuery.me().findName(getSessionAttr(Consts.SESSION_SELECT_DATAAREA).toString(), customerType);

		List<Map<String, Object>> customerList = new ArrayList<>();
		Map<String, Object> all = new HashMap<>();
		all.put("text", "全部");
		all.put("id", "");
		customerList.add(all);

		for(Record name : nameList) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", name.getStr("id"));
			item.put("text", name.getStr("name"));
			customerList.add(item);
		}

		renderJson(JSON.toJSON(customerList));
	}

	@RequiresPermissions(value = { "/admin/customerVisit/audit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void complete() {

		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);

		String id = getPara("id");
		String taskId = getPara("taskId");

		String commentDesc = getPara("comment");

		CustomerVisit customerVisit = CustomerVisitQuery.me().findById(id);
		if (!customerVisit.getStatus().equals(Consts.CUSTOMER_VISIT_STATUS_DEFAULT)) {
			renderAjaxResultForError("拜访已审核");
			return;
		}
		Integer status = getParaToInt("status");
		String comment = (status == 1) ? "批准" : "拒绝";

		if(StrKit.notBlank(commentDesc))
			customerVisit.setComment(commentDesc);

		WorkFlowService workFlowService = new WorkFlowService();
		Map<String,Object> var = new HashMap<>();
		var.put("pass", status);
		String applyUsername = workFlowService.getTaskVariableByTaskId(taskId, Consts.WORKFLOW_APPLY_USERNAME).toString();
		User toUser = UserQuery.me().findUserByUsername(applyUsername);

		if (status == 1) {
			customerVisit.setStatus(Customer.CUSTOMER_NORMAL);
		} else {
			customerVisit.setStatus(Customer.CUSTOMER_REJECT);
			Kv kv = Kv.create();

			WxMessageTemplate messageTemplate = WxMessageTemplateQuery.me().findByCode(Consts.PROC_CUSTOMER_VISIT_REVIEW);

			kv.set("touser", toUser.getWechatOpenId());
			kv.set("templateId", messageTemplate.getTemplateId());
			kv.set("customerName", customerVisit.getSellerCustomer().getCustomer().getCustomerName());
			kv.set("submit", toUser.getRealname());

			kv.set("createTime", DateTime.now().toString("yyyy-MM-dd HH:mm"));
			kv.set("status", comment);
			MessageKit.sendMessage(Actions.NotifyWechatMessage.CUSTOMER_VISIT_AUDIT_MESSAGE, kv);
		}

		workFlowService.completeTask(taskId, comment, var);

		sendMessage(sellerId, comment, user.getId(), toUser.getId(), user.getDepartmentId(), user.getDataArea()
				, Message.CUSTOMER_VISIT_REVIEW_TYPE_CODE, customerVisit.getSellerCustomer().getCustomer().getCustomerName());

		if (customerVisit.saveOrUpdate())
			renderAjaxResultForSuccess("操作成功");
		else
			renderAjaxResultForError("操作失败");
	}

	public void queryCustomerType() {
        String typeDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA).toString();
        List<Record> typeList = CustomerTypeQuery.me().findCustomerTypeList(typeDataArea);
        renderAjaxResultForSuccess("success",JSON.toJSON(typeList));
	}

	public void queryQuestionType() {
		List<Dict> visitDictList = DictQuery.me().findDictByType("customer_visit");
		renderAjaxResultForSuccess("success", JSON.toJSON(visitDictList));
	}

	private void sendMessage(String sellerId, String comment, String fromUserId, String toUserId, String deptId
			, String dataArea, String type, String title) {
		Message message = new Message();
		message.setSellerId(sellerId);
		message.setContent(comment);
		message.setFromUserId(fromUserId);

		message.setToUserId(toUserId);
		message.setDeptId(deptId);
		message.setDataArea(dataArea);
		message.setType(type);

		message.setTitle(title);
		MessageKit.sendMessage(Actions.ProcessMessage.PROCESS_MESSAGE_SAVE, message);
	}
	
	public void exportVisit() throws IOException {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		
        String keyword = getPara("k");
        String questionType = getPara("questionType");
        String customerType = getPara("customerType");
        String status = getPara("status");
        String bizUser = getPara("bizUser");
        String filePath = "";

		if (StrKit.notBlank(customerType)) {
			filePath = filePath + customerType;
			customerType = StringUtils.urlDecode(customerType);
			setAttr("customerType", customerType);
		}

        if (StrKit.notBlank(questionType)) {
			filePath = filePath + DictQuery.me().findName(questionType);
        	questionType = StringUtils.urlDecode(questionType);
            setAttr("questionType", questionType);
        }

        if(StrKit.notBlank(status)) {
			filePath = filePath + DictQuery.me().findName(status);
        	status = StringUtils.urlDecode(status);
        	setAttr("status", status);
		}
        
        if(StrKit.notBlank(bizUser)) {
			filePath = filePath + UserQuery.me().findById(bizUser).getRealname();
			bizUser = StringUtils.urlDecode(bizUser);
        	setAttr("bizUser", bizUser);
		}


		if (StrKit.notBlank(keyword)) {
			filePath = filePath + keyword;
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
        
        List<Record> visitList = CustomerVisitQuery.me().exportVisit(keyword, selectDataArea, customerType, questionType, "id", "cc_v.create_date desc", status,bizUser);
        try {
			exportExcel(visitList, filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	@SuppressWarnings("deprecation")
	public void exportExcel(List<Record> dataList, String filePath) throws IOException {

		filePath = filePath +  "客户拜访记录.xls";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = new HSSFWorkbook();  
		try {
			HSSFSheet sheet = wb.createSheet("拜访记录");  
			HSSFRow row1=sheet.createRow(0);  
			HSSFCell cell=row1.createCell(0); 
			
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			cell.setCellValue("拜访记录导出表");  
			sheet.addMergedRegion(new CellRangeAddress(0,0,0,13));  
			HSSFRow rowTitle=sheet.createRow(1);  
			
			//sheet.setDefaultRowHeight((short) (15.625*20));
			sheet.setDefaultColumnWidth( (short) (20));

			rowTitle.createCell(0).setCellValue("客户名称");
			rowTitle.createCell(1).setCellValue("客户类型");
			rowTitle.createCell(2).setCellValue("客户电话");
			rowTitle.createCell(3).setCellValue("问题类型");  
			
			rowTitle.createCell(4).setCellValue("问题描述");      
			rowTitle.createCell(5).setCellValue("拜访人");
			rowTitle.createCell(6).setCellValue("审核状态");
			rowTitle.createCell(7).setCellValue("创建时间");  
			
			rowTitle.createCell(8).setCellValue("拜访图片1"); 
			rowTitle.createCell(9).setCellValue("拜访图片2");
			rowTitle.createCell(10).setCellValue("拜访图片3");
			
			rowTitle.createCell(11).setCellValue("审核建议");
			rowTitle.createCell(12).setCellValue("审核人");  
			rowTitle.createCell(13).setCellValue("审核时间"); 
			
			int rowNum = 1;
			for (Record record : dataList) {
				rowNum++;
				HSSFRow row=sheet.createRow(rowNum);
				row.setHeight((short) (15.625*50));
				row.createCell(0).setCellValue((String) record.get("customer_name"));
				row.createCell(1).setCellValue((String)record.get("customer_type"));
				row.createCell(2).setCellValue((String)record.get("customerMobile"));
				row.createCell(3).setCellValue((String)record.get("questionName"));
				
				row.createCell(4).setCellValue((String)record.get("question_desc"));
				row.createCell(5).setCellValue((String)record.get("visit_user"));
				row.createCell(6).setCellValue((String)record.get("visitStatus"));
				row.createCell(7).setCellValue(record.get("create_date")!=null?(String)sdf.format(record.get("create_date")):"");
				
				String picsrc = (String)record.get("photo");
				List<ImageJson> list = JSON.parseArray(picsrc, ImageJson.class);
				if(list != null) {
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i) == null) {
							break;
						}
						ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
						String domain = OptionQuery.me().findValue("cdn_domain");

						URL url = new URL(domain + "/" + list.get(i).getSavePath());
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("GET");

						conn.setConnectTimeout(5 * 1000);
						InputStream inStream = conn.getInputStream();
						byte[] data = readInputStream(inStream);
						conn.disconnect();

						HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 250, (short) (8 + i), rowNum, (short) (9 + i), rowNum);
						anchor.setAnchorType(0);
						patriarch.createPicture(anchor, wb.addPicture(data, HSSFWorkbook.PICTURE_TYPE_JPEG));
						byteArrayOut.close();

					}
				}

				row.createCell(11).setCellValue((String)record.get("comment"));
				row.createCell(12).setCellValue((String)record.get("review_user"));
				row.createCell(13).setCellValue(record.get("review_date")!=null?(String)sdf.format(record.get("review_date")):"");
			}
      
		   fileOut = new FileOutputStream(filePath.replace("\\", "/"));
		   wb.write(fileOut);  
		} catch (Exception io) {
			io.printStackTrace();
			System.out.println("io erorr : " + io.getMessage());
		} finally {
			if (fileOut != null) {
				try {
					fileOut.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (wb != null)
				wb.close();
		}
		renderFile(new File(filePath.replace("\\", "/")));

	}
	
	public static BufferedImage toBufferedImage(Image image) { 
		if (image instanceof BufferedImage) { 
			return (BufferedImage) image; 
		} 

		image = new ImageIcon(image).getImage(); 
		BufferedImage bimage = null; 

		int w = image.getWidth(null)== -1?600:image.getWidth(null);
		int h = image.getHeight(null)== -1? 500:image.getHeight(null);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		try { 
			int transparency = Transparency.OPAQUE; 
			GraphicsDevice gs = ge.getDefaultScreenDevice(); 
			GraphicsConfiguration gc = gs.getDefaultConfiguration(); 
			bimage = gc.createCompatibleImage(w, h, transparency);
		} catch (HeadlessException e) { 
			e.getStackTrace();
		} 
		
		if (bimage == null) { 
			int type = BufferedImage.TYPE_INT_RGB; 
			bimage = new BufferedImage(image.getWidth(null), 
			image.getHeight(null), type); 
		} 
		
		Graphics g = bimage.createGraphics(); 
		g.drawImage(image, 0, 0, null); 
		g.dispose(); 
		return bimage; 
	}

	private static byte[] readInputStream(InputStream inStream) throws Exception{
	    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    while( (len=inStream.read(buffer)) != -1 ){
	        outStream.write(buffer, 0, len);
	    }
	    inStream.close();
	    return outStream.toByteArray();
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void count() {
		String customerType = getPara("customer_type");
		String customerName = getPara("customer_name");
		String questionType = getPara("question_type");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";

		List<Record> imageList = CustomerVisitQuery.me()._findPhoto(customerType, customerName, questionType, selectDataArea, dealerDataArea);
		if(imageList.size() == 0) renderAjaxResultForError();
		else renderAjaxResultForSuccess();
	}

	@RequiresPermissions(value = { "/admin/customerVisit", "/admin/dealer/all", "/admin/all" }, logical = Logical.OR)
	public void exportImage() throws Exception {
		String customerType = getPara("customer_type");
		String customerName = getPara("customer_name");
		String questionType = getPara("question_type");
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String dealerDataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA) + "%";

		String domain = OptionQuery.me().findByKey("cdn_domain").getOptionValue();
		List<Record> imageList = CustomerVisitQuery.me()._findPhoto(customerType, customerName, questionType, selectDataArea, dealerDataArea);

		String zipFileName = "拜访图片.zip";

		if(StrKit.notBlank(customerName)) zipFileName = SellerCustomerQuery.me().findById(customerName).getCustomer().getCustomerName() + zipFileName;
		if(StrKit.notBlank(customerType)) zipFileName = customerType + zipFileName;
		zipFileName = URLEncoder.encode(zipFileName, "UTF-8");

		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName));

		List<File> fileList = new ArrayList<>();
		for(Record record : imageList) {

			String photo = record.getStr("photo");
			List<String> photoList = Splitter.on("_")
					.trimResults()
					.omitEmptyStrings()
					.splitToList(photo);

			int k = 1;
			for(String savePath : photoList){

				List<ImageJson> list = JSON.parseArray(savePath, ImageJson.class);

				for (int i = 0; i < list.size(); i++){
					ImageJson image = list.get(i);

					String fileName = image.getSavePath();
					String filePath = DateUtils.dateToStr(record.getDate("create_date"), "yyyy-MM-dd" )
							+ record.getStr("realname") + "拜访" + record.getStr("customer_name")
							+  "图片" + k + ".jpg";

					fileList.add(getImage(domain, fileName, filePath));
					 k++;
				}
			}
		}

		File[] files = fileList.toArray(new File[fileList.size()]);
		zipFile(files, "", zos);

		zos.flush();
		zos.close();

		renderFile(new File(zipFileName));

	}

	private File getImage(String domain, String fileName, String filePath) throws Exception {

		String encodedFileName = URLEncoder.encode(fileName, "utf-8");

		String finalUrl = String.format("%s/%s", domain, encodedFileName);

		URL url = new URL(finalUrl);
		HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
		urlCon.setConnectTimeout(6000);
		urlCon.setReadTimeout(6000);
		int code = urlCon.getResponseCode();

		if (code != HttpURLConnection.HTTP_OK) {
			throw  new Exception("文件读取失败");
		}

		DataInputStream in = new DataInputStream(urlCon.getInputStream());
		DataOutputStream out = new DataOutputStream(new FileOutputStream(filePath));
		byte[] buffer = new byte[2048];
		int count = 0;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		out.close();
		in.close();
		return new File(filePath);
	}

	private void zipFile(File[] subs, String baseName, ZipOutputStream zos) throws  IOException{
		for (int i = 0; i < subs.length; i++) {
			File f = subs[i];
			zos.putNextEntry(new ZipEntry(baseName + f.getName()));
			FileInputStream fis = new FileInputStream(f);
			byte[] buffer = new byte[2048];
			int r = 0;
			while ((r = fis.read(buffer)) != -1) {
				zos.write(buffer, 0, r);
			}
			fis.close();
		}
	}

	public void queryBizUser() {
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<CustomerVisit> customerVisits = CustomerVisitQuery.me().findByDataArea(selectDataArea);
		List<Map<String, Object>> customerVisitList = new ArrayList<>();
		for(CustomerVisit customerVisit:customerVisits) {
			Map<String, Object> item = new HashMap<>();
			item.put("userId", customerVisit.getStr("user_id"));
			item.put("bizUser", customerVisit.getStr("realname"));
			customerVisitList.add(item);
		}
		
		renderJson(customerVisitList);
	}
}
