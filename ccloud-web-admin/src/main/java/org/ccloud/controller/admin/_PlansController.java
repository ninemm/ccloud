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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DataAreaUtil;
import org.ccloud.utils.StringUtils;
import org.ccloud.model.Dict;
import org.ccloud.model.Plans;
import org.ccloud.model.PlansDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.User;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.PlansDetailQuery;
import org.ccloud.model.query.PlansQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.UserQuery;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/plans", viewPath = "/WEB-INF/admin/plans")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _PlansController extends JBaseCRUDController<Plans> { 
	
	
	@Override
	public void index() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		List<Plans> plans = PlansQuery.me().findbyDateArea(dataArea);
		List<String> months = new ArrayList<>();
		List<String> years = new ArrayList<>();
		for(int i = 0 ; i < plans.size() ; i++) {
			if(plans.get(i).getType().equals(Consts.MONTH_PLAN)) {
				months.add(plans.get(i).getStartDate().toString().substring(0, plans.get(i).getStartDate().toString().lastIndexOf('-')));
			}else if(plans.get(i).getType().equals(Consts.YEAR_PLAN)) {
				years.add(plans.get(i).getStartDate().toString().substring(0, plans.get(i).getStartDate().toString().indexOf('-')));
			}
		}
		setAttr("years",years);
		setAttr("months",months);
		List<Dict> dicts = DictQuery.me().findDictByType(Consts.PLAN);
		setAttr("dicts",dicts);
		render("index.html");
	}
	
	public void list() {
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String keyword = getPara("k");
        if (StrKit.notBlank(keyword)) {
            keyword = StringUtils.urlDecode(keyword);
            setAttr("k", keyword);
        }
        String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
        String type = getPara("type");
        String dateType = getPara("dateType");
        String startDate = "";
        String endDate = "";
        if(StrKit.notBlank(dateType)) {
        	int index = dateType.indexOf("-");
        	startDate = dateType + "-01";
        	Calendar cal = Calendar.getInstance();  
        	//设置年份  
        	cal.set(Calendar.YEAR,Integer.parseInt(dateType.substring(0,index)));  
        	//设置月份  
        	cal.set(Calendar.MONTH, Integer.parseInt(dateType.substring(index+1,dateType.length()))-1); 
        	//获取某月最大天数  
        	int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        	endDate = dateType + "-"+(lastDay);
        }
		/*if(StrKit.notBlank(dateType)) {
			if(type.equals(Consts.MONTH_PLAN)) {
				int index = dateType.indexOf("-");
				startDate = dateType + "-01";
				Calendar cal = Calendar.getInstance();  
				//设置年份  
				cal.set(Calendar.YEAR,Integer.parseInt(dateType.substring(0,index)));  
				//设置月份  
				cal.set(Calendar.MONTH, Integer.parseInt(dateType.substring(index+1,dateType.length()))-1); 
				//获取某月最大天数  
				int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				endDate = dateType + "-"+(lastDay);
			}else if(type.equals(Consts.YEAR_PLAN)) {
				startDate = dateType + "-01-01";
				endDate = dateType + "-12-31";
			}
		}*/
        Page<Plans> page = PlansQuery.me().paginate(getPageNumber(), getPageSize(),keyword, "cp.create_date", dataArea,type,startDate,endDate,dateType,sellerId);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
	
	/*@RequiresPermissions(value = { "/admin/plans/uploading", "/admin/dealer/all",
			"/admin/all" }, logical = Logical.OR)*/
	public void upload() {
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		String ti = OptionQuery.me().findValue(Consts.OPTION_WEB_PROC_PLANS_LIMIT + sellerCode);
		if(StrKit.isBlank(ti)) {
			ti = "01";
		}
		setAttr("ti",ti);
		render("upload.html");
	}
	
	@SuppressWarnings("resource")
	/*@RequiresPermissions(value = { "/admin/plans/uploading", "/admin/dealer/all",
	"/admin/all" }, logical = Logical.OR)*/
	public void plansTemplate() {
		String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\plans\\"
				+ "plansTemplate.xls";
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", ""); 
		List<User> users = UserQuery.me().findByDataAndDeptId(dataArea,user.getDepartmentId());
	    // 声明一个工作薄
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("table");
		sheet.setColumnWidth(0, 5000); 
//	    XSSFWorkbook workBook = new XSSFWorkbook();
//	    XSSFSheet sheet = workBook.createSheet();
	   //导出excel样式1
	    HSSFCellStyle ztStyle = (HSSFCellStyle) wb.createCellStyle();   
	    Font ztFont = wb.createFont();  
	    ztFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	    ztFont.setFontHeightInPoints((short) 12);
        ztStyle.setFont(ztFont);
        //水平居中
	    ztStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); 
	    //上下居中
	    ztStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
	    //自动换行
	    ztStyle.setWrapText(true);
	    
	    //导出excel样式2
	    HSSFCellStyle ztStyle2 = (HSSFCellStyle) wb.createCellStyle();   
	    Font ztFont2 = wb.createFont();  
	    ztFont2.setFontHeightInPoints((short) 10);
        ztStyle2.setFont(ztFont2);
	    //上下居中
	    ztStyle2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);;
	    
	    //导出excel样式3
	    HSSFCellStyle ztStyle3 = (HSSFCellStyle) wb.createCellStyle(); 
	    HSSFDataFormat format= wb.createDataFormat();
	    ztStyle3.setDataFormat(format.getFormat("yyyy/m/d"));
	    
	    // 生成一个表格
	    wb.setSheetName(0,"销售计划");
	    //模板例子
	   
	    HSSFRow row_0 = sheet.createRow(0);
	    for(int i = 0 ; i<users.size();i++) {
	    	row_0.createCell(i+2).setCellValue(users.get(i).getId());
	    	row_0.setZeroHeight(true);
	    }
	    HSSFRow row = sheet.createRow(1);
	    Cell ce = row.createCell(1);
	    ce.setCellValue("产品");
	    ce.setCellStyle(ztStyle);
	    for(int i = 0 ; i<users.size();i++) {
	    	Cell cell = row.createCell(i+2);
	    	cell.setCellValue(users.get(i).getRealname());
	    	cell.setCellStyle(ztStyle);
	    }
	   sheet.setColumnHidden((short)0,true);
	    //插入需导出的数据
	    for(int i=0;i<productRecords.size();i++){
	    	HSSFRow rowP = sheet.createRow(i+2);
	        rowP.createCell(0).setCellValue(productRecords.get(i).getStr("sell_product_id"));
	        Cell cell = rowP.createCell(1);
	        cell.setCellValue(productRecords.get(i).getStr("custom_name")+" "+productRecords.get(i).getStr("valueName"));
	        cell.setCellStyle(ztStyle);
	        for(int j = 0;j<users.size() ; j++) {
	        	Cell cellP = rowP.createCell(j+2);
	        	cellP.setCellValue(0);
	        	cellP.setCellStyle(ztStyle2);
	        }
	    }
	    File  file = new File(filePath);
	    //文件输出流
	    try {
			FileOutputStream outStream = new FileOutputStream(file);
			wb.write(outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderFile(new File(filePath));
	
	}
	@Before(Tx.class)
//	@RequiresPermissions(value = { "/admin/plans/uploading", "/admin/dealer/all",
//			"/admin/all" }, logical = Logical.OR)
	
	public void uploading() {
		int inCnt = 0;
		int inNum = 0;
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", ""); 
		List<User> users = UserQuery.me().findByDataAndDeptId(dataArea,user.getDepartmentId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
//		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
//		String ti = OptionQuery.me().findValue(Consts.OPTION_WEB_PROC_PLANS_LIMIT + sellerCode);
		//开始时间
		File file = getFile().getFile();
		String month = getPara("start");
		String startDate  = getPara("startDate");
		String endDate  = getPara("endDate");
		Plans plans = PlansQuery.me().findSellerIdAndDataAreaAndMonth(sellerId,deptDataArea,month);
		String plansId = "";
		if(plans == null) {
			plans = new Plans();
			plansId = StrKit.getRandomUUID();
			plans.setId(plansId);
			plans.setSellerId(sellerId);
			plans.setType(Consts.MONTH_PLAN);
			/*int index = month.indexOf("-");
			String day = month +"-"+ti;
			Calendar cal = Calendar.getInstance();  
			//设置年份  
			try {
				if(StrKit.notBlank(ti)) {
					cal.setTime(sdf.parse(day));
					cal.set(Calendar.YEAR,Integer.parseInt(month.substring(0,index)));  
					plans.setStartDate(sdf.parse(day));
					//设置月份  
					cal.set(Calendar.MONTH, Integer.parseInt(month.substring(index+1,month.length()))); 
					cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
					endDate= sdf.format(cal.getTime());//获得前一天
					startDate =  month+"-"+ti;
					plans.setEndDate(sdf.parse(endDate));
					plans.setPlansMonth(sd.parse(month));
				}else {
					startDate = month + "-01";
					cal.setTime(sdf.parse(month + "-01"));
					cal.set(Calendar.YEAR,Integer.parseInt(month.substring(0,index)));  
					plans.setStartDate(sdf.parse(startDate));
					//设置月份  
					cal.set(Calendar.MONTH, Integer.parseInt(month.substring(index+1,month.length()))); 
					cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
					endDate= sdf.format(cal.getTime());//获得前一天
					plans.setEndDate(sdf.parse(endDate));
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}*/
			try {
				plans.setStartDate(sdf.parse(startDate));
				plans.setEndDate(sdf.parse(endDate));
				plans.setPlansMonth(sd.parse(month));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plans.setDeptId(user.getDepartmentId());
			plans.setDataArea(deptDataArea);
			plans.setCreateDate(new Date());
			plans.setUserId(user.getId());
			plans.save();
		}else {
			plansId = plans.getId();
		}
		//结束时间
		try {
			FileInputStream fis = new FileInputStream(file);  
			
			POIFSFileSystem fs = new POIFSFileSystem(fis); 
			Workbook workbook = WorkbookFactory.create(fs);
			 Sheet sheet = workbook.getSheetAt(0);
			//设置单元格类型
			 for(int i = 0;i<users.size() ; i++) {
				 Cell cell = sheet.getRow(0).getCell(i+2);
				 if(cell==null) {
					 continue;
				 }
				 User us = UserQuery.me().findById(cell.getStringCellValue());
				 for(int j = 0;j<productRecords.size();j++) {
					PlansDetail detail = PlansDetailQuery.me().findbySSEU(getPara("sellerProduct"+i),startDate,endDate,us.getId());
					String sellerProductId = sheet.getRow(j+2).getCell(0).getStringCellValue();
					if(detail!=null) {
						inNum++;
						continue;
					}
					Cell cl = sheet.getRow(j+2).getCell(i+2);
					if(cl==null) {
						continue;
					}
					if(cl.getNumericCellValue()==0) {
						continue;
					}
					cl.setCellType(CellType.STRING);
					PlansDetail plansDetail = new PlansDetail();
					plansDetail.setId(StrKit.getRandomUUID());
					plansDetail.setPlansId(plansId);
					plansDetail.setSellerProductId(sellerProductId);
					plansDetail.setPlanNum(new BigDecimal(cl.getStringCellValue()));
					plansDetail.setCompleteNum(new BigDecimal(0));
					plansDetail.setCompleteRatio(new BigDecimal(0));
					plansDetail.setUserId(us.getId());
					plansDetail.setDataArea(us.getDataArea());
					plansDetail.save();
					inCnt++;
				 }
			 }
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderAjaxResultForSuccess("成功导入计划" + inCnt + "条数据,重复"+inNum+"条数据");
	}
	
//	@RequiresPermissions(value = { "/admin/plans/downloading", "/admin/dealer/all",
//	"/admin/all" }, logical = Logical.OR)
public void downloading() throws UnsupportedEncodingException{
	//计算行数
	int num = 0;
	//合并行的结束位置
	int end = 1;
//	int amountPlans = 0;
//	int amountComplete = 0;
	String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
	String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
//	String type = getPara("type");
	String dateType = getPara("dateType");
	//统计销售计划中需要导出的计划范围的所有的产品
	List<Record> details = PlansDetailQuery.me().findBySellerId(sellerId,dateType,dataArea);
	//查询出所有计划中的产品、业务员明细
	List<Record> sellerProductDetails = PlansDetailQuery.me().findAllBySellerId(sellerId,dateType,dataArea);
	List<Record> _sellerProductDetails = PlansDetailQuery.me()._findAllBySellerId(sellerId,dateType,dataArea);
	// 声明一个工作薄
	HSSFWorkbook wb = new HSSFWorkbook();
	HSSFSheet sheet = wb.createSheet("table");
    String filePath = getSession().getServletContext().getRealPath("\\") + "\\WEB-INF\\admin\\plans\\"
			+ "plansInfo.xls";
    //设置表格样式1
    HSSFCellStyle setBorder = wb.createCellStyle();
    setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
    setBorder.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中
    setBorder.setWrapText(true);//设置自动换行
    //设置表格样式2
    HSSFCellStyle setBorder2 = wb.createCellStyle();
    setBorder2.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
    setBorder2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中
    setBorder2.setWrapText(true);//设置自动换行
    //设置表格样式3
    HSSFCellStyle setBorder3 = wb.createCellStyle();
    setBorder3.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
    setBorder3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中
    setBorder3.setWrapText(true);//设置自动换行
    setBorder3.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
    setBorder3.setFillPattern(CellStyle.SOLID_FOREGROUND);
  //设置表格样式4
    HSSFCellStyle setBorder4 = wb.createCellStyle();
    setBorder4.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 水平居中
    setBorder4.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 上下居中
    setBorder4.setWrapText(true);//设置自动换行
    setBorder4.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
    setBorder4.setFillPattern(CellStyle.SOLID_FOREGROUND);
    //设置字体
    HSSFFont font = wb.createFont();
    font.setFontName("黑体");
    font.setFontHeightInPoints((short) 10);//设置字体大小
    setBorder2.setFont(font);
    HSSFFont font2 = wb.createFont();
    font2.setFontName("仿宋_GB2312");
    font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
    font2.setFontHeightInPoints((short) 12);
    setBorder.setFont(font2);
    setBorder3.setFont(font2);
    setBorder4.setFont(font2);
    HSSFRow  newRow = sheet.createRow(0);
    HSSFRow  nRow = sheet.createRow(1);
    //合并单元格
    Cell sCell = newRow.createCell(1);
    sCell.setCellValue("开始时间");
    sCell.setCellStyle(setBorder);
    Cell eCell = newRow.createCell(2);
    eCell.setCellValue("结束时间");
    eCell.setCellStyle(setBorder);
    Cell yCell = newRow.createCell(3);
    yCell.setCellValue("业务员");
    yCell.setCellStyle(setBorder);
    CellRangeAddress regions = new CellRangeAddress(0, 1, 1,1 );
    CellRangeAddress regione = new CellRangeAddress(0,  1, 2, 2);
    CellRangeAddress regiony = new CellRangeAddress(0,  1, 3, 3);
    sheet.addMergedRegion(regions);
    sheet.addMergedRegion(regione);
    sheet.addMergedRegion(regiony);
    for(int i = 0;i < details.size()+1; i++) {
    	if(i == (details.size())) {
    		Cell zCell = nRow.createCell(5*details.size()+4);
    		zCell.setCellValue("合计计划金额");
    		Cell cCell = nRow.createCell(5*details.size()+5);
    		cCell.setCellValue("合计完成金额");
    		Cell rCell = nRow.createCell(5*details.size()+6);
    		rCell.setCellValue("完成率");
    		zCell.setCellStyle(setBorder4);
    		cCell.setCellStyle(setBorder4);
    		rCell.setCellStyle(setBorder4);
    	}else {
    		
    		CellRangeAddress region = new CellRangeAddress(0,  0, (5*i+4), (5*i+8));
    		Cell pCell = newRow.createCell(5*i+4);
    		Cell cell01 = nRow.createCell(5*i+4);
    		Cell cell02 = nRow.createCell(5*i+4+1);
    		Cell cell03 = nRow.createCell(5*i+4+2);
    		Cell cell04 = nRow.createCell(5*i+4+3);
    		Cell cell05 = nRow.createCell(5*i+4+4);
    		pCell.setCellValue(details.get(i).getStr("custom_name"));
    		cell01.setCellValue("计划");
    		cell02.setCellValue("完成");
    		cell03.setCellValue("计划金额");
    		cell04.setCellValue("完成金额");
    		cell05.setCellValue("完成情况");
    		sheet.addMergedRegion(region);
    		pCell.setCellStyle(setBorder);
    		cell01.setCellStyle(setBorder);
    		cell02.setCellStyle(setBorder);
    		cell03.setCellStyle(setBorder3);
    		cell04.setCellStyle(setBorder3);
    		cell05.setCellStyle(setBorder);
    	}
    }
    for(int i = 0;i < _sellerProductDetails.size(); i++) {
    	HSSFRow  row = sheet.createRow(i+2);
        	if( i>0 && _sellerProductDetails.get(i).get("type") .equals(_sellerProductDetails.get(i-1).get("type")) ) {
        		end = i+1;
    	    	if(!_sellerProductDetails.get(i).get("plansMonth").equals(_sellerProductDetails.get(i-1).get("plansMonth"))  
    	    			) {
    	    		Cell typeCell = row.createCell(0);
    	    		Cell startCell = row.createCell(1);
    	    		Cell endCell = row.createCell(2);
    	    		if(_sellerProductDetails.get(i).get("type").equals(Consts.WEEK_PLAN)) {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.WEEK_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}else if(sellerProductDetails.get(i).get("type").equals(Consts.MONTH_PLAN)) {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.MONTH_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}else {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.YEAR_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}
    	    		if(end>(end-num)) {
        				CellRangeAddress regionT = new CellRangeAddress((end-num),  end, 0, 0);
        				CellRangeAddress regionS = new CellRangeAddress((end-num),  end, 1, 1);
        				CellRangeAddress regionE = new CellRangeAddress((end-num),  end, 2, 2);
        				sheet.addMergedRegion(regionT);
        				sheet.addMergedRegion(regionS);
        				sheet.addMergedRegion(regionE);
    	    		}
    	    		
    	    		if(i+1 == _sellerProductDetails.size() && num > 1) {
        				
        			}
    	    		typeCell.setCellStyle(setBorder);
    	    		startCell.setCellStyle(setBorder);
    	    		endCell.setCellStyle(setBorder);
    	    		num=0;
    	    	}else {
    	    		Cell typeCell = row.createCell(0);
    	    		Cell startCell = row.createCell(1);
    	    		Cell endCell = row.createCell(2);
    	    		if(sellerProductDetails.get(i).get("type").equals(Consts.WEEK_PLAN)) {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.WEEK_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}else if(sellerProductDetails.get(i).get("type").equals(Consts.MONTH_PLAN)) {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.MONTH_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}else {
    	    			typeCell.setCellValue(DictQuery.me().findByValue(Consts.YEAR_PLAN).getName());
    	    			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
    	    			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
    	    		}
    	    		typeCell.setCellStyle(setBorder);
    	    		startCell.setCellStyle(setBorder);
    	    		endCell.setCellStyle(setBorder);
    	    		num++;
    	    		if(i+1 == _sellerProductDetails.size()) {
    	    			CellRangeAddress regionT = new CellRangeAddress((end-num+1),  end+1, 0, 0);
    	    			CellRangeAddress regionS = new CellRangeAddress((end-num+1),  end+1, 1, 1);
    	    			CellRangeAddress regionE = new CellRangeAddress((end-num+1),  end+1, 2, 2);
    	    			sheet.addMergedRegion(regionT);
    	    			sheet.addMergedRegion(regionS);
    	    			sheet.addMergedRegion(regionE);
    	    		}
    	    		
    	    	}
        	}else {
        		Cell typeCell = row.createCell(0);
        		Cell startCell = row.createCell(1);
        		Cell endCell = row.createCell(2);
        		if(sellerProductDetails.get(i).get("type").equals(Consts.WEEK_PLAN)) {
        			typeCell.setCellValue(DictQuery.me().findByValue(Consts.WEEK_PLAN).getName());
        			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
        			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
        		}else if(sellerProductDetails.get(i).get("type").equals(Consts.MONTH_PLAN)) {
        			typeCell.setCellValue(DictQuery.me().findByValue(Consts.MONTH_PLAN).getName());
        			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
        			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
        		}else {
        			typeCell.setCellValue(DictQuery.me().findByValue(Consts.YEAR_PLAN).getName());
        			startCell.setCellValue(_sellerProductDetails.get(i).get("startDate").toString());
        			endCell.setCellValue(_sellerProductDetails.get(i).get("endDate").toString());
        		}
        		typeCell.setCellStyle(setBorder);
        		startCell.setCellStyle(setBorder);
        		endCell.setCellStyle(setBorder);
        		
        	}
    		Cell uCell = row.createCell(3);
    		uCell.setCellValue(UserQuery.me().findById(_sellerProductDetails.get(i).get("user_id").toString()).getRealname());
    		uCell.setCellStyle(setBorder);
    		for(int  j= 0;j < details.size();j++) {
        		Cell p0Cell = row.createCell(5*j+4);
        		Cell p1Cell = row.createCell(5*j+4+1);
        		Cell p2Cell = row.createCell(5*j+4+2);
        		Cell p3Cell = row.createCell(5*j+4+3);
        		Cell p4Cell = row.createCell(5*j+4+4);
    	    	 for(int k = 0; k < sellerProductDetails.size();k++) {
        			 if(sellerProductDetails.get(k).getStr("seller_product_id").equals(details.get(j).getStr("seller_product_id")) 
        					 && sellerProductDetails.get(k).getStr("user_id").equals(_sellerProductDetails.get(i).get("user_id"))
        					 && sellerProductDetails.get(k).get("plans_id").equals(_sellerProductDetails.get(i).get("plans_id"))
        					 && sellerProductDetails.get(k).get("plansMonth").equals(_sellerProductDetails.get(i).get("plansMonth"))) {
        				 p0Cell.setCellValue(sellerProductDetails.get(k).getStr("plan_num"));
        				 p1Cell.setCellValue(sellerProductDetails.get(k).getStr("complete_num"));
        				 p2Cell.setCellValue(sellerProductDetails.get(k).getStr("plansAmount"));
        				 p3Cell.setCellValue(sellerProductDetails.get(k).getStr("completeAmount"));
        				 p4Cell.setCellValue(sellerProductDetails.get(k).getStr("complete_ratio")+"%");
        				 p0Cell.setCellStyle(setBorder2);
        				 p1Cell.setCellStyle(setBorder2);
        				 p2Cell.setCellStyle(setBorder3);
        				 p3Cell.setCellStyle(setBorder3);
        				 p4Cell.setCellStyle(setBorder2);
        				 break;
        			 }else {
        				 p0Cell.setCellValue("0");
        				 p1Cell.setCellValue("0");
        				 p2Cell.setCellValue("0.00");
        				 p3Cell.setCellValue("0.00");
        				 p4Cell.setCellValue("0.00%");
        				 p0Cell.setCellStyle(setBorder2);
        				 p1Cell.setCellStyle(setBorder2);
        				 p2Cell.setCellStyle(setBorder3);
        				 p3Cell.setCellStyle(setBorder3);
        				 p4Cell.setCellStyle(setBorder2);
        			 }
    	    	 }
        	 }
    		 Cell zACell = row.createCell(5*details.size()+4);
     		Cell cACell = row.createCell(5*details.size()+5);
     		Cell rACell = row.createCell(5*details.size()+6);
     		zACell.setCellValue(_sellerProductDetails.get(i).get("plansAmount").toString());
     		cACell.setCellValue(_sellerProductDetails.get(i).get("completeAmount").toString());
     		rACell.setCellValue(_sellerProductDetails.get(i).get("completeRetio").toString()+"%");
     		rACell.setCellStyle(setBorder4);
     		zACell.setCellStyle(setBorder4);
     		cACell.setCellStyle(setBorder4);
		 //合并行
		 
    }
    
    HSSFRow  rowT = sheet.createRow(_sellerProductDetails.size()+2);
    Cell zTCell = rowT.createCell(0);
    zTCell.setCellValue("合计");
    zTCell.setCellStyle(setBorder4);
    CellRangeAddress regionT = new CellRangeAddress(_sellerProductDetails.size()+2, _sellerProductDetails.size()+2, 0, 3);
    sheet.addMergedRegion(regionT);
    for(int i = 0 ; i < details.size()+1 ; i++) {
    	if(i == details.size()) {
    		Cell pTZCell = rowT.createCell(5*i+4);
    		Cell pTCCell = rowT.createCell(5*i+4+1);
    		Cell pTRCell = rowT.createCell(5*i+4+2);
    		/*pTZCell.setCellValue(amountPlans);
    		pTCCell.setCellValue(amountComplete);*/
    		pTZCell.setCellStyle(setBorder4);
    		pTCCell.setCellStyle(setBorder4);
    		pTRCell.setCellStyle(setBorder4);
    	}else {
    		Cell p0Cell = rowT.createCell(5*i+4);
    		Cell p1Cell = rowT.createCell(5*i+4+1);
    		Cell p2Cell = rowT.createCell(5*i+4+2);
    		Cell p3Cell = rowT.createCell(5*i+4+3);
    		Cell p4Cell = rowT.createCell(5*i+4+4);
    		p0Cell.setCellValue(details.get(i).get("pNum").toString());
    		p1Cell.setCellValue(details.get(i).get("cNum").toString());
    		p2Cell.setCellValue(details.get(i).get("totalPlansAmount").toString());
    		p3Cell.setCellValue(details.get(i).get("totalCompleteAmount").toString());
    		p4Cell.setCellValue(details.get(i).get("completeRetio").toString()+"%");
    		p0Cell.setCellStyle(setBorder4);
    		p1Cell.setCellStyle(setBorder4);
    		p2Cell.setCellStyle(setBorder4);
    		p3Cell.setCellStyle(setBorder4);
    		p4Cell.setCellStyle(setBorder4);
    	}
    }
    File  file = new File(filePath);
    //文件输出流
    try {
		FileOutputStream outStream = new FileOutputStream(file);
		wb.write(outStream);
		outStream.flush();
		outStream.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	renderFile(new File(filePath));
}
	
	@RequiresPermissions(value = { "/admin/plans/edit"}, logical = Logical.OR)
	public void edit() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", ""); 
		List<User> users = UserQuery.me().findByDataAndDeptId(dataArea,user.getDepartmentId());
		List<Dict> dicts = DictQuery.me().findDictByType(Consts.PLAN);
		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
		String ti = OptionQuery.me().findValue(Consts.OPTION_WEB_PROC_PLANS_LIMIT + sellerCode);
		if(StrKit.isBlank(ti)) {
			ti = "01";
		}
		//获取当前月份，在后面再加10个月
		Calendar calendar=Calendar.getInstance();
		//获得当前时间的月份，月份从0开始所以结果要加1
		int month=calendar.get(Calendar.MONTH)+1;
		//获取当前年份
		int year = calendar.get(Calendar.YEAR); 
		List<String> months = new ArrayList<>();
		List<String> years = new ArrayList<>();
		for(int i = 0 ; i < 11 ; i++) {
			if((month+i)>12) {
				months.add((year+1)+"-"+(month+i-12));
			}else {
				months.add(year+"-"+(month+i));
			}
			years.add(year+i+"");
		}
		setAttr("ti",ti);
		setAttr("dicts",dicts);
		setAttr("sellerProducts",productRecords);
		setAttr("users",users);
		setAttr("months",months);
		setAttr("years",years);
		render("edit.html");
	}
	
	@Before(Tx.class)
	public void save() {
		String[] userIds = getParaValues("userId");
		String planType = getPara("planType");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String deptDataArea = DataAreaUtil.getDeptDataAreaByCurUserDataArea(user.getDataArea());
		String month = getPara("month");
//		String sellerCode = getSessionAttr(Consts.SESSION_SELLER_CODE);
//		String ti = OptionQuery.me().findValue(Consts.OPTION_WEB_PROC_PLANS_LIMIT + sellerCode);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
		Plans plans = PlansQuery.me().findSellerIdAndDataAreaAndMonth(sellerId,deptDataArea,month);
		String plansId = "";
		if(plans == null) {
			plans = new Plans();
			plansId = StrKit.getRandomUUID();
			plans.setId(plansId);
			plans.setSellerId(sellerId);
			plans.setType(planType);
			/*int index = month.indexOf("-");
			String day = month +"-"+ti;
			Calendar cal = Calendar.getInstance();  
			//设置年份  
			try {
				if(StrKit.notBlank(ti)) {
					cal.setTime(sdf.parse(day));
					cal.set(Calendar.YEAR,Integer.parseInt(month.substring(0,index)));  
					plans.setStartDate(sdf.parse(day));
					//设置月份  
					cal.set(Calendar.MONTH, Integer.parseInt(month.substring(index+1,month.length()))); 
					cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
					endDate= sdf.format(cal.getTime());//获得前一天
					startDate =  month+"-"+ti;
					plans.setEndDate(sdf.parse(endDate));
					plans.setPlansMonth(sd.parse(month));
				}else {
					startDate = month + "-01";
					cal.setTime(sdf.parse(month + "-01"));
					cal.set(Calendar.YEAR,Integer.parseInt(month.substring(0,index)));  
					plans.setStartDate(sdf.parse(startDate));
					//设置月份  
					cal.set(Calendar.MONTH, Integer.parseInt(month.substring(index+1,month.length()))); 
					cal.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
					endDate= sdf.format(cal.getTime());//获得前一天
					plans.setEndDate(sdf.parse(endDate));
				}
			} catch (ParseException e1) {
				e1.printStackTrace();
			}*/
			try {
				plans.setStartDate(sdf.parse(startDate));
				plans.setEndDate(sdf.parse(endDate));
				plans.setPlansMonth(sd.parse(month));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			plans.setDeptId(user.getDepartmentId());
			plans.setDataArea(deptDataArea);
			plans.setCreateDate(new Date());
			plans.setUserId(user.getId());
			plans.save();
		}else {
			plansId = plans.getId();
		}
		int num = Integer.parseInt(getPara("productNum"));
		int inCnt = 0;
		for(String  userId: userIds) {
			for(int i = 1 ; i <= num ; i++) {
				if(StrKit.isBlank(getPara("sellerProduct" + i))){
					continue;
				}
				PlansDetail detail = PlansDetailQuery.me().findbySSEU(getPara("sellerProduct"+i),startDate,endDate,userId);
				SellerProduct sellerProduct = SellerProductQuery.me().findById(getPara("sellerProduct"+i));
				if(detail!=null) {
					renderAjaxResultForError("产品："+sellerProduct.getCustomName()+" 已经存在该月计划");
					return;
				}
				if(getPara("planNum"+i).equals("0")) {
					renderAjaxResultForError("产品："+sellerProduct.getCustomName()+" 数量不能为0");
					return;
				}
				User us = UserQuery.me().findById(userId);
				PlansDetail plansDetail = new PlansDetail();
				plansDetail.setId(StrKit.getRandomUUID());
				plansDetail.setPlansId(plansId);
				plansDetail.setSellerProductId(getPara("sellerProduct"+i));
				plansDetail.setPlanNum(new BigDecimal(getPara("planNum"+i)));
				plansDetail.setCompleteNum(new BigDecimal(0));
				plansDetail.setCompleteRatio(new BigDecimal(0));
				plansDetail.setUserId(userId);
				plansDetail.setCreateDate(new Date());
				plansDetail.setDataArea(us.getDataArea());
				plansDetail.save();
				inCnt++;
			}
		}
		
		renderAjaxResultForSuccess("成功导入计划" + inCnt + "条数据");
	}
	
	public void detailPlans() {
		String plansId = getPara("plansId");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String selectDataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		Map<String, Object> all = new HashMap<>();
		all.put("title", "全部");
		all.put("value", "");
//		List<Record> productRecords = SellerProductQuery.me().findProductListForApp(sellerId, "", "");
		List<PlansDetail> plansDetails = PlansDetailQuery.me().findbyDateAreaAndPlansId(selectDataArea,plansId);
		List<Map<String, Object>> productItems = new ArrayList<>();
		productItems.add(all);
		for(PlansDetail plansDetail : plansDetails) {
			Map<String, Object> item = new HashMap<>();
			item.put("title", plansDetail.get("custom_name"));
			item.put("value", plansDetail.getSellerProductId());
			productItems.add(item);
		}
		
		List<Record> users = PlansDetailQuery.me().findUserBySellerId(sellerId, "");
		List<Map<String, Object>> userItems = new ArrayList<>();
		userItems.add(all);
		for(Record record : users) {
			
			Map<String, Object> item = new HashMap<>();
			
			String userId = record.get("user_id");
			item.put("title", record.getStr("realname"));
			item.put("value", userId);

			userItems.add(item);
		}
		setAttr("productItems",JSON.toJSON(productItems));
		setAttr("userItems",JSON.toJSON(userItems));
		setAttr("plansId",plansId);
		 render("detail.html");
	}
	public void detail() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String dataArea = getSessionAttr(Consts.SESSION_SELECT_DATAAREA);
		String plansId = getPara("plansId");
		String userId = getPara("userId");
		String sellerProductId = getPara("sellerProductId");
		String keyword = getPara("keyword");
		Page<Record> page = PlansDetailQuery.me().paginate(getPageNumber(), getPageSize(),keyword,sellerId, dataArea,userId,sellerProductId,plansId);
        Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
        renderJson(map);
	}
}
