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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.mid.MidDataUtil;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.StringUtils;
import org.ccloud.utils.XmlUtils;
import org.ccloud.model.Activity;
import org.ccloud.model.ActivityApply;
import org.ccloud.model.ActivityExecute;
import org.ccloud.model.CustomerVisit;
import org.ccloud.model.Dict;
import org.ccloud.model.ExpenseDetail;
import org.ccloud.model.QyExpense;
import org.ccloud.model.QyExpensedetail;
import org.ccloud.model.User;
import org.ccloud.model.query.ActivityApplyQuery;
import org.ccloud.model.query.ActivityExecuteQuery;
import org.ccloud.model.query.ActivityQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.CustomerVisitQuery;
import org.ccloud.model.query.DictQuery;
import org.ccloud.model.query.ExpenseDetailQuery;
import org.ccloud.model.query.OptionQuery;
import org.ccloud.model.query.QyBasicfeetypeQuery;
import org.ccloud.model.query.QyExpenseQuery;
import org.ccloud.model.query.QyExpensedetailQuery;
import org.ccloud.model.query.SalesOrderQuery;
import org.ccloud.model.vo.ImageJson;
import org.ccloud.model.vo.YX_ActivityDisplayInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/activity", viewPath = "/WEB-INF/admin/activity")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _ActivityController extends JBaseCRUDController<Activity> { 

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
		String sellerId = getSessionAttr("sellerId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<Record> page = ActivityQuery.me().paginate(getPageNumber(), getPageSize(), keyword, startDate, endDate,sellerId);
		for(int i = 0; i <page.getList().size();i++){
			if(page.getList().get(i).getStr("customer_type")!="") {
				page.getList().get(i).set("customer_type", ActivityQuery.me().getCustomerTypes(page.getList().get(i).getStr("customer_type")));
			}
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	public void getOptions() {
		String id = getPara("id");
		List<Dict> dlist = DictQuery.me().findByCode(Consts.DICT_UNIT_CODE);
		List<Map<String, Object>> list = new ArrayList<>();
		for(Dict dict : dlist) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", dict.getId());
			map.put("name",dict.getName());
			if (!StringUtils.isBlank(id)) {
				Activity activity = ActivityQuery.me().findById(id);
						if((dict.getId().toString()).equals(activity.getUnit())){
							map.put("isvalid", 1);
						} else {
							map.put("isvalid", 0);
						}
			}else {
					map.put("isvalid", 0);
			}
			list.add(map);
		}
		renderJson(list);
		
	}
	public void edit() {
		String id = getPara("id");
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<Dict> dicts = DictQuery.me().findDictByType(Consts.INVEST_TYPE);
		List<ExpenseDetail> expenseList = ExpenseDetailQuery.me().findByActivityId(id);
		setAttr("expenseList", expenseList);
		setAttr("dicts",dicts);
		if(!StrKit.isBlank(id)) {
			Activity activity = ActivityQuery.me().findById(id);
			setAttr("activity", activity);
			if(activity.getImageListStore()!=null) {
				String[] imags = activity.getImageListStore().split(",");
				List<String> imageList = new ArrayList<String>();
				for(int i=0;i<imags.length;i++) {
					if(!imags[i].equals("")) {
						imageList.add(imags[i]);
					}
				}
				setAttr("imageList", imageList);
			}
			String[] area = activity.getAreaType().split("-");
			List<String> areaList = new ArrayList<String>();
			for(int i=0;i<area.length;i++) {
				areaList.add(area[i]);
			}
			setAttr("areaList",areaList);
			if (!StrKit.isBlank(activity.getInvestType())) {
				String[] investType = activity.getInvestType().split(",");
				List<String> investTypeList = new ArrayList<String>();
				for(int i=0;i<investType.length;i++) {
					investTypeList.add(investType[i]);
				}
				setAttr("investTypeList",investTypeList);
			}
			setAttr("startDate",  DateFormatUtils.format(activity.getStartTime(), "yyyy-MM-dd"));
			setAttr("endDate", DateFormatUtils.format(activity.getEndTime(), "yyyy-MM-dd"));
		}
		
		List<Record> productlist = SalesOrderQuery.me().findProductListBySeller(sellerId);
		List<Map<String, String>> productOptionList = new ArrayList<Map<String, String>>();
		for (Record record : productlist) {
			Map<String, String> productOptionMap = new HashMap<String, String>();

			String sellProductId = record.getStr("id");
			String customName = record.getStr("custom_name");
			String speName = record.getStr("valueName");

			productOptionMap.put("id", sellProductId);
			productOptionMap.put("text", customName + "/" + speName);

			productOptionList.add(productOptionMap);
		}
		setAttr("productOptionList", JSON.toJSON(productOptionList));
	}
	
	@Before(Tx.class)
	public void save() {
		final Activity activity = getModel(Activity.class);
		List<ExpenseDetail> expenseOldList = ExpenseDetailQuery.me().findByActivityId(activity.getId());
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		String [] imagePath = getParaValues("imageUrl[]");
		String [] item1 = getParaValues("item1[]");
		String [] item2 = getParaValues("item2[]");
		String [] item3 = getParaValues("item3[]");
		String [] item4 = getParaValues("item4[]");
		String [] expenseIds = getParaValues("expenseIds[]");
		String investTypes = getPara("invest_type");
		String customerTypes = getPara("customerType");
		if(customerTypes !=null && customerTypes.length()>180) {
			renderAjaxResultForError("客户类型不能超过5个");
			return;
		}
		//存储路径
		String imagPath = "";
		if (imagePath != null) {
			if(imagePath.length>3) {
				renderAjaxResultForError("图片保存不能超过三张");
				return;
			}
			for (int i = 0;i < imagePath.length;i++) {
				imagPath +=imagePath[i].replace("\\", "/")+",";
			}
			activity.setImageListStore(imagPath.substring(0, (imagPath.length()-1)));
		}else {
			activity.setImageListStore(imagPath);
		}
		String unit = getPara("unit");
		String areaNames = getPara("areaNames").replace("/", "-");
		String startDate = getPara("startDate")+" 00:00:00";
		String endDate = getPara("endDate")+" 23:59:59";
		Date sdate = null; 
		Date edate = null;
	    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	    try {
			sdate=formatter.parse(startDate);
			edate=formatter.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}  
	    if(activity.getCategory().equals(Consts.CATEGORY_NORMAL)){
	    	activity.setVisitNum(0);
	    	activity.setInvestType("");
	    }else{
	    	activity.setInvestType(investTypes);
	    }
	    activity.setUnit(unit);
	    activity.setJoinNum(1);
		activity.setSellerId(sellerId);
		activity.setAreaType(areaNames);
		activity.setStartTime(sdate);
		activity.setEndTime(edate);
		activity.setCustomerType(customerTypes);
		activity.saveOrUpdate();
		int num = Integer.valueOf(getPara("num"));
		List<ActivityExecute> activityExecutes = ActivityExecuteQuery.me().findbyActivityId(activity.getId());
		if(activityExecutes.size() > 0) {
			for(ActivityExecute ae : activityExecutes) {
				ActivityExecuteQuery.me().batchDelete(ae.getId());
			}
		}
		if(num > 0) {
			for(int i = 0; i < num ; i++) {
				if(getPara("orderList" + (i + 1)) == ""){
					continue;
				}
				ActivityExecute activityExecute = new ActivityExecute();
				activityExecute.setId(StrKit.getRandomUUID());
				activityExecute.setActivityId(activity.getId());
				activityExecute.setOrderList(getPara("orderList"+(i+1)));
				activityExecute.setRemark(getPara("remark"+(i+1)));
				activityExecute.save();
			}
		}
		BigDecimal totalMoney = new BigDecimal(0);
		Integer totalNum = 0;
		if (item1 != null) {
			for(int i = 0; i < item1.length; i++) {
				ExpenseDetail detail = new ExpenseDetail();
				detail.setActivityId(activity.getId());
				detail.setFlowNo(activity.getProcCode());
				String typeID = findFlowDictType(activity.getInvestType());
				detail.setFlowDictType(typeID);
				if (typeID.equals("feeType_name_display")) {
					detail.setDisplayDictType(findDisplayType(item1[i]));
				}
				detail.setItem1(item1[i]);
				detail.setItem2(item2[i]);
				if (item3 != null) {
					detail.setItem3(item3[i]);
				}
				if (item4 != null) {
					detail.setItem4(item4[i]);
				}
				BigDecimal[] result = calculationTotalInfo(totalMoney, totalNum, detail);
				totalMoney = result[0];
				totalNum = result[1].intValue();
				detail.setState(true);			
				if (StrKit.notBlank(expenseIds[i])) {
					detail.setId(expenseIds[i]);
					detail.setModifyDate(new Date());
					detail.update();
				} else {
					detail.setId(StrKit.getRandomUUID());
					detail.setCreateDate(new Date());
					detail.save();
				}
			}
			if (activity.getInvestAmount() == null) {
				activity.setInvestAmount(totalMoney);
			}
			if (activity.getInvestNum() == null) {
				activity.setInvestNum(totalNum);
			}
			activity.update();
			List<String> ids = getDiffrent(expenseOldList, expenseIds);
			ExpenseDetailQuery.me().batchDelete(ids);
		}
		renderAjaxResultForSuccess();
	}
	
	private BigDecimal[] calculationTotalInfo(BigDecimal totalMoney, Integer totalNum, ExpenseDetail detail) {
		BigDecimal[] result = new BigDecimal[2];
		if (detail.getFlowDictType().equals(Consts.FLOW_DICT_TYPE_NAME_DISPLAY) 
				|| detail.getFlowDictType().equals(Consts.FLOW_DICT_TYPE_NAME_CHANNEL)) {
			result[0] = totalMoney.add(new BigDecimal(detail.getItem4()));
			result[1] = new BigDecimal(totalNum).add(new BigDecimal(detail.getItem3()));
		} else if(detail.getFlowDictType().equals(Consts.FLOW_DICT_TYPE_NAME_SA)) {
			result[0] = totalMoney.add(new BigDecimal(detail.getItem3()));
			result[1] = new BigDecimal(0);
		} else {
			result[0] = totalMoney.add(new BigDecimal(detail.getItem2()));
			result[1] = new BigDecimal(0);
		}
		return result;
	}

	/** 
	 * 获取两个List的不同元素(耗时最低)
	 * @param list1 
	 * @param list2 
	 * @return 
	 */  
	private static List<String> getDiffrent(List<ExpenseDetail> eList, String[] newIds) {
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = Arrays.asList(newIds);
		for (ExpenseDetail expenseDetail : eList) {
			list1.add(expenseDetail.getId());
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
	    return diff;  
	}
	
	private String findFlowDictType(String code) {
		String type = null;
		if (code.equals("101101")) {
			return "feeType_name_PR";
		} else if (code.equals("101102")) {
			return "feeType_name_raise";
		} else if (code.equals("101103")) {
			return "feeType_name_AD";
		} else if (code.equals("101104")) {
			return "feeType_name_display";
		} else if (code.equals("101105")) {
			return "channel_define";
		} else if (code.equals("101106")) {
			return "feeType_name_gift";
		} else if (code.equals("101107")) {
			return "feeType_name_SA";
		}
		return type;
	}
	
	private String findDisplayType(String code) {
		if (code.equals("102032")) {
			return "display_publish";
		} else if(code.equals("102033")) {
			return "display_shop";
		} else if(code.equals("102034")) {
			return "display_retail";
		} else if(code.equals("102035")) {
			return "display_sell";
		} else if(code.equals("102036")) {
			return "display_catering";
		} else if(code.equals("102037")) {
			return "display_dm";
		} else {
			return "display_CER";
		}
	}
	
	public void getCustomerTypeOptions() {
		String id = getPara("id");
		String DataArea = getSessionAttr(Consts.SESSION_DEALER_DATA_AREA);
		List<Record> listty = new ArrayList<Record>();
		List<Map<String, Object>> list = new ArrayList<>();
		listty = CustomerTypeQuery.me().getCustomerTypes(DataArea);
		for(Record record : listty) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", record.get("id").toString());
			map.put("name",record.get("name").toString());
			if (!StringUtils.isBlank(id)) {
				Activity activity = ActivityQuery.me().findById(id);
				String[] customerTypes = activity.getCustomerType().split(",");
					for(int i = 0;i<customerTypes.length;i++){
						if((record.get("id").toString()).equals(customerTypes[i])){
							map.put("isvalid", 1);
							break;
						} else {
							map.put("isvalid", 0);
						}
					}
			}else {
					map.put("isvalid", 0);
			}
			list.add(map);
		}
		renderJson(list);
	}
	
	public void changeIspublish() {
		String id = getPara("id");
		Activity activity =ActivityQuery.me().findById(id);
		boolean flang = false;
		if(activity.getIsPublish()==1){
			activity.set("is_publish", 0);
		}else{
			activity.set("is_publish", 1);
		}
		activity.set("modify_date", new Date());
		flang=activity.update();
		renderJson(flang);
	}
	
	//中间库同步数据
	@Before(Tx.class)
	public void getMidDataTest() {
		String sellerId = getSessionAttr(Consts.SESSION_SELLER_ID);
		List<QyExpense> expenseList = QyExpenseQuery.me().findTextData();
		List<Activity> acList = new ArrayList<>();
		List<ExpenseDetail> dlist = new ArrayList<>();
		for (QyExpense qyExpense : expenseList) {
			if (!ActivityQuery.me().isExist(qyExpense.getFlowNo())) {
				Activity activity = new Activity();
				activity.setId(StrKit.getRandomUUID());
				activity.setSellerId(sellerId);
				activity.setCode(qyExpense.getActivityNo());
				activity.setTitle(qyExpense.getExpenseName());
				activity.setStartTime(qyExpense.getExpenseBeginDate());
				activity.setEndTime(qyExpense.getExpenseEndDate());
				activity.setCategory(Consts.ACTIVITY_CATEGORY_CODE);
				String[] value = getAreaType(qyExpense.getExpenseName());
				activity.setInvestType(DictQuery.me().findbyName(value[0]).getValue());
				activity.setAreaType(value[1]);
				activity.setInvestAmount(new BigDecimal(qyExpense.getApplyAmount()));
				activity.setProcCode(qyExpense.getFlowNo());
				activity.setPlanCode(qyExpense.getActivityNo());
				activity.setContent(qyExpense.getMemo());
				activity.setTimeInterval(qyExpense.getInputDay().toString());
				activity.setIsPublish(0);
				activity.setCreateDate(new Date());
				acList.add(activity);
				dlist = getExpenseDetailList(qyExpense.getExpenseID(), activity.getId(), activity.getInvestType());
			}
		}
		Db.batchSave(acList, acList.size());
		Db.batchSave(dlist, dlist.size());
		renderAjaxResultForSuccess("同步成功");
	}
	
	public void getMidData() {
		MidDataUtil.getActivityInfo("2018-03-02", "2018-03-12", "1", "10");
	}
	
	private List<ExpenseDetail> getExpenseDetailList(String expenseId, String actId, String typeId) {
		List<ExpenseDetail> expenseDetails = new ArrayList<>();
		List<QyExpensedetail> midDatas = QyExpensedetailQuery.me().findByActivityId(expenseId);
		for (QyExpensedetail qyExpensedetail : midDatas) {
			ExpenseDetail expenseDetail = new ExpenseDetail();
			expenseDetail.setId(StrKit.getRandomUUID());
			expenseDetail.setActivityId(actId);
			expenseDetail.setFlowNo(qyExpensedetail.getFlowNo());
			expenseDetail.setFlowTypeId(qyExpensedetail.getFlowTypeID());
			String dict = findFlowDictType(typeId);
			expenseDetail.setFlowDictType(dict);
			if (dict.equals("feeType_name_display")) {
				String name = QyBasicfeetypeQuery.me().findNameById(qyExpensedetail.getItem1());
				Dict code = DictQuery.me().findbyName(name);
				expenseDetail.setDisplayDictType(findDisplayType(code.getValue()));
			}
			expenseDetail.setItem1(qyExpensedetail.getItem1());
			getItem(expenseDetail, qyExpensedetail, 5);
			expenseDetail.setCreateDate(qyExpensedetail.getCreateTime());
			expenseDetail.setModifyDate(qyExpensedetail.getModifyTime());
			if (qyExpensedetail.getFlag() == 0) {
				expenseDetail.setState(false);
			} else {
				expenseDetail.setState(true);
			}
			expenseDetails.add(expenseDetail);
		}
		return expenseDetails;
	}
	
	private void getItem(ExpenseDetail expenseDetail, QyExpensedetail qyExpensedetail, int num) {
		int j = 2;
		for (int i = 2; i < num; i++) {
			String item = "Item" + String.valueOf(i);
			if (StrKit.notBlank(qyExpensedetail.get(item).toString())) {
				expenseDetail.set("item" + String.valueOf(j), qyExpensedetail.get(item));
				j++;
			}
		}
	}

	private String[] getAreaType(String data) {
		String[] value = new String[2];
		String[] areaFirst = data.split(":");
		String[] flowType = areaFirst[0].split("申请");
		value[0] = flowType[0];
		String[] areaSecond = areaFirst[1].split("_");
		int a = 0;
		for (int i = 0; i < areaSecond.length; i++) {
			if (areaSecond[i].contains("省")) {
				a = i;
				break;
			}
		}
		String areaType = areaSecond[a] + "-" + areaSecond[a+1];
		value[1] = areaType;
		return value;
	}

	public void getActivityExecute() {
		String activityId = getPara("activityId");
		List<ActivityExecute> activityExecuteList = ActivityExecuteQuery.me().findbyActivityId(activityId);
		renderJson(activityExecuteList);
	}
	
	//投入列表
	public void put() {
		List<Dict> invest = DictQuery.me().findDictByType(Consts.INVEST_TYPE);
		setAttr("ilist", invest);
		render("activityPut.html");
	}
	
	public void putList() {
		String keyword = getPara("k");
		String invest_type = getPara("invest_type");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		String sellerId = getSessionAttr("sellerId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		Page<Record> page = ActivityQuery.me().activityPutPaginate(getPageNumber(), getPageSize(), keyword, startDate, endDate,sellerId,invest_type);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}

	//活动投放明细
	public void putDetails() {
		String id = getPara("id");
		setAttr("id", id);
		render("putDetails.html");
	}
	
	//活动投放明细
	public void putDetailsList() {
		String id = getPara("id");
		String status = getPara("status");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		Page<Record> page = ActivityQuery.me().putDetailsPaginate(getPageNumber(), getPageSize(), keyword,startDate, endDate,id,status);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//活动拜访详情
	public void visitDetails() {
		String activityApplyId = getPara("activityApplyId");
		setAttr("activityApplyId", activityApplyId);
		render("visitDetails.html");
	}
	
	//活动拜访详情
	public void visitDetailsList() {
		String activityApplyId = getPara("activityApplyId");
		String startDate = getPara("startDate");
		
		String endDate = getPara("endDate");
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		Page<Record> page = ActivityQuery.me().visitDetailsPaginate(getPageNumber(), getPageSize(), keyword,startDate, endDate,activityApplyId);
		for(int i = 0; i <page.getList().size();i++){
			if(StrKit.notBlank(page.getList().get(i).getStr("photo"))) {
				List<ImageJson> list = Lists.newArrayList();
				JSONArray picList = JSON.parseArray(page.getList().get(i).getStr("photo"));
				for (int  a= 0; a <picList.size(); a++) {
					JSONObject obj = picList.getJSONObject(a);
					String domain = OptionQuery.me().findValue("cdn_domain");
					String savePath = obj.getString("savePath");
					String originalPath = obj.getString("originalPath");
					ImageJson image = new ImageJson();
					image.setOriginalPath(domain + "/" +originalPath);
					image.setSavePath(domain + "/" +savePath);
					list.add(image);
				}
				page.getList().get(i).set("photo", list);
			}
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//活动的所有拜访详情
	public void visitAllDetails() {
		String id = getPara("id");
		setAttr("id", id);
		render("visitAllDetails.html");
	}
	
	//活动的所有拜访详情
	public void visitAllDetailsList() {
		String activityId = getPara("id");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}
		Page<Record> page = ActivityQuery.me().visitAllDetailsPaginate(getPageNumber(), getPageSize(), keyword,startDate, endDate,activityId);
		for(int i = 0; i <page.getList().size();i++){
			if(StrKit.notBlank(page.getList().get(i).getStr("photo"))) {
				List<ImageJson> list = Lists.newArrayList();
				JSONArray picList = JSON.parseArray(page.getList().get(i).getStr("photo"));
				for (int  a= 0; a <picList.size(); a++) {
					JSONObject obj = picList.getJSONObject(a);
					String domain = OptionQuery.me().findValue("cdn_domain");
					String savePath = obj.getString("savePath");
					String originalPath = obj.getString("originalPath");
					ImageJson image = new ImageJson();
					image.setOriginalPath(domain + "/" +originalPath);
					image.setSavePath(domain + "/" +savePath);
					list.add(image);
				}
				page.getList().get(i).set("photo", list);
			}
		}
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	//相册js
	public void img() {
		String customerVisitId = getPara(0);
		CustomerVisit CustomerVisit = CustomerVisitQuery.me().findById(customerVisitId);
		List<ImageJson> list = Lists.newArrayList();
		JSONArray picList = JSON.parseArray(CustomerVisit.getPhoto());
		for (int  a= 0; a <picList.size(); a++) {
			JSONObject obj = picList.getJSONObject(a);
			String domain = OptionQuery.me().findValue("cdn_domain");
			String savePath = obj.getString("savePath");
			String originalPath = obj.getString("originalPath");
			ImageJson image = new ImageJson();
			image.setOriginalPath(domain + "/" +originalPath);
			image.setSavePath(domain + "/" +savePath);
			list.add(image);
		}
		setAttr("list", list);
		render("img.html");
	}
	
	//加入核销
	public void auditReimbursement() throws Exception {
		String ids = getPara("ids");
		String[] activityApplyIds = ids.split(",");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		for (String activityApplyId : activityApplyIds) {
			Record YxActivity = ActivityQuery.me().findYxActivity(activityApplyId);
			Map<String, Object>map=new HashMap<>();
			String[] FlowIDNO = YxActivity.getStr("FlowIDNO").split("\\.");
			map.put("IDNO",199);
			map.put("FlowIDNO",Integer.getInteger(FlowIDNO[FlowIDNO.length-1]) );
//			map.put("ResourceID", "");
			map.put("CostType",YxActivity.getInt("CostType"));
			if (YxActivity.getStr("invest_type").equals("101101")) {
				//公关赞助
				map.put("ActivityTime",YxActivity.getStr("ActivityTime"));
				map.put("CustomerName",YxActivity.getStr("CustomerName"));
				map.put("ActivityAddress",YxActivity.getStr("ActivityAddress"));
				map.put("Telephone",YxActivity.getStr("Telephone"));
				map.put("ScenePhoto","123");
				map.put("ResourceFlag",1);
				map.put("WriteOffAmount",YxActivity.getBigDecimal("WriteOffAmount"));
				map.put("Telephone",YxActivity.getStr("Telephone"));
				map.put("CreateManName",YxActivity.getStr("CreateManName"));
				map.put("CreateTime",YxActivity.getStr("CreateTime"));
				map.put("ModifyManName",YxActivity.getStr("ModifyManName"));
				map.put("ModifyTime",YxActivity.getStr("ModifyTime"));
				map.put("Flag",1);
				map.put("ShopOrderID",19);
				map.put("GiftPhoto","123");
				
			}else if(YxActivity.getStr("invest_type").equals("101102")) {
				//消费培育
			}else if(YxActivity.getStr("invest_type").equals("101103")) {
				//终端广告
			}else if(YxActivity.getStr("invest_type").equals("101104")) {
				//终端陈列 
			}else if(YxActivity.getStr("invest_type").equals("101105")) {
				//终端客情
	
			}else if(YxActivity.getStr("invest_type").equals("101106")) {
				//商超赠品
			}else {
				//进场费
				
			}
		}
		renderAjaxResultForSuccess("加入核销成功");
	}
}
