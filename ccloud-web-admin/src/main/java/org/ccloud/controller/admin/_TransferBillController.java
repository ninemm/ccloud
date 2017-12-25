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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ccloud.Consts;
import org.ccloud.core.JBaseCRUDController;
import org.ccloud.core.interceptor.ActionCacheClearInterceptor;
import org.ccloud.model.Inventory;
import org.ccloud.model.InventoryDetail;
import org.ccloud.model.SellerProduct;
import org.ccloud.model.TransferBill;
import org.ccloud.model.TransferBillDetail;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.TransferBillDetailQuery;
import org.ccloud.model.query.TransferBillQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.model.vo.transferBillInfo;
import org.ccloud.route.RouterMapping;
import org.ccloud.route.RouterNotAllowConvert;
import org.ccloud.utils.DateUtils;
import org.ccloud.utils.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/transferBill", viewPath = "/WEB-INF/admin/transfer_bill")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _TransferBillController extends JBaseCRUDController<TransferBill> { 

	public static final String BILLTYPE = "TB";
	public final static String startNo = "000001";
	 //目前系统还没有企业编号，先创建一个100000占位	
	
	public void index() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		//判断当前用户有几个可用仓库
		List<Warehouse> wlist = WarehouseQuery.me().findWarehouseByUserId(user.getId());
		if (wlist.size()<=1) {
			setAttr("wlistSize", 0);
		}else {
			setAttr("wlistSize", 1);
		}
		render("index.html");
	}
	
	public void list() {
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String departmentId = user.getDepartmentId();
		String keyword = getPara("k");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("k", keyword);
		}

		Page<TransferBill> page = TransferBillQuery.me().paginate(getPageNumber(), getPageSize(), keyword, "c.create_date desc",departmentId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}
	
	
	public void edit() {	
		String id = getPara("id");
		final String sellerId = getSessionAttr("sellerId").toString();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		if (id != null) {
			TransferBill transferBill = TransferBillQuery.me().findById(id);
			setAttr("transferBill", transferBill);
		}
		List<Warehouse> wlist = WarehouseQuery.me().findWarehouseByUserId(userId);
		setAttr("wlist", wlist);
		List<User> ulist = UserQuery.me().findUserList(userId);
		setAttr("ulist", ulist);
		List<transferBillInfo> ilist = TransferBillDetailQuery.me().findByTransferBillDetailId(id,sellerId);
		setAttr("ilist", ilist);		
	}
	
	
	@Override
	public void save() {			
	 TransferBill transferBill = getModel(TransferBill.class);
	 if (transferBill.getStatus() == null || transferBill.getStatus() == 0) {
	  User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
	  transferBill.setDeptId(user.getDepartmentId());
	  transferBill.setDataArea(user.getDataArea());
	  transferBill.setInputUserId(user.getId());
	 Map<String, String[]> map = getParaMap();
	 boolean update = false;
	  if (StringUtils.isBlank(transferBill.getId())) {
		  transferBill.setId(StrKit.getRandomUUID());
		  transferBill.setCreateDate(new Date());
		  transferBill.setBizDate(new Date());
		  transferBill.setStatus(0);
		  String billno = this.getBillSn();
		  transferBill.setTransferBillSn(billno);
	    }else {
	    	update = true;
		}
		boolean status = this.saveTransferBillInfo(map, transferBill, update);
		if (status) {
			renderAjaxResultForSuccess("ok");
		}
	 }else {
		  renderAjaxResultForError("保存失败,该单子已调拨完成！");
	 }
	}
	
	public boolean saveTransferBillInfo(final Map<String, String[]>map,final TransferBill transferBill,final boolean update) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (update) {
					transferBill.saveOrUpdate();
					List<TransferBillDetail> iSaveList = new ArrayList<>();
	        		int loopEnd = 1;
	               //先根据主表ID，删除调拨单子表相关记录
	        		List<TransferBillDetail> list = TransferBillDetailQuery.me().deleteByTransferBillId(transferBill.getId());
			        for (TransferBillDetail transferBillDetail : list) {
			        	transferBillDetail.delete();
					}				 
			        //再把修改的内容插入子表
					String[] factIndex = map.get("factIndex");
					for (int i = 1; i < factIndex.length; i++) {
						TransferBillDetail transferBillDetail = getModel(TransferBillDetail.class);
						 String productId = StringUtils.getArrayFirst(map.get("transferBillDetailList[" + factIndex[i] +"].product_id"));
						 String productCount = StringUtils.getArrayFirst(map.get("transferBillDetailList[" + factIndex[i] + "].product_count"));
						 
						 transferBillDetail.setSellerProductId(productId);
						 transferBillDetail.setProductCount(Integer.parseInt(productCount));
						 transferBillDetail.setTransferBillId(transferBill.getId());
						 transferBillDetail.setId(StrKit.getRandomUUID());
						 transferBillDetail.setDeptId(transferBill.getDeptId());
						 transferBillDetail.setDataArea(transferBill.getDataArea());
						 transferBillDetail.setCreateDate(transferBill.getCreateDate());
						 transferBillDetail.setModifyDate(new Date());
					     iSaveList.add(transferBillDetail);
					     loopEnd++;
					     if (loopEnd == factIndex.length) {
		    					break;
		    				}
					}
					 try {
							Db.batchSave(iSaveList, iSaveList.size());
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
				}else {
					transferBill.save();
					//存储调拨单子表信息
					List<TransferBillDetail> iSaveList = new ArrayList<>();
					String[] factIndex = map.get("factIndex");
	        		int loopEnd = 1;
					 for (int i = 0; i<factIndex.length; i++) {
						 TransferBillDetail transferBillDetail = getModel(TransferBillDetail.class);
						 String productId = StringUtils.getArrayFirst(map.get("transferBillDetailList[" + i +"].product_id"));
						 String productCount = StringUtils.getArrayFirst(map.get("transferBillDetailList[" + i + "].product_count"));
						 
						 transferBillDetail.setSellerProductId(productId);
						 transferBillDetail.setProductCount(Integer.parseInt(productCount));
						 transferBillDetail.setTransferBillId(transferBill.getId());
						 transferBillDetail.setId(StrKit.getRandomUUID());
						 transferBillDetail.setDeptId(transferBill.getDeptId());
						 transferBillDetail.setDataArea(transferBill.getDataArea());
						 transferBillDetail.setCreateDate(new Date());
					     iSaveList.add(transferBillDetail);					     
	 					loopEnd++;
	 					if (loopEnd == factIndex.length) {
	    					break;
	    				}
					  }
	                try {
						Db.batchSave(iSaveList, iSaveList.size());
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}				
				return true;
			}
		});				
		return isSave;		
	}
	
	
	public void enable() {
		String id = getPara("id");
		int isEnabled = getParaToInt("isEnabled");
		final String sellerId = getSessionAttr("sellerId").toString();
		//获取调拨单明细
		List<transferBillInfo> transferBillInfos = TransferBillDetailQuery.me().findByTransferBillDetailId(id,sellerId);
		//查找商品的库存数量并和调拨数量做比较
		this.checkStoreCount(transferBillInfos);
	    Boolean status = this.inserIntoInventoryInfo(transferBillInfos);
	    if (status) {
	    	TransferBill transferBill = TransferBillQuery.me().findById(id);
			transferBill.setStatus(isEnabled);
			if (transferBill.saveOrUpdate()) {
				renderAjaxResultForSuccess("更新成功");
			} else {
				renderAjaxResultForError("更新失败");
			}
		}else {
			renderAjaxResultForError("更新失败");
		}
	}
    /**
     * 
    * @Title: 检查待调拨数量和现在库存总账库存数量大小关系 
    * @Description: TODO
    * @param @param transferBillInfos   
    * @return void    
    * @throws
     */
	private void checkStoreCount(List<transferBillInfo> transferBillInfos) {
		for (transferBillInfo transferBillInfo : transferBillInfos) {
			InventoryDetail inventoryDetail = InventoryDetailQuery.me().findByWarehouseIdAndProductId(transferBillInfo.getFromWarehouseId(),transferBillInfo.getSellerProductId());
				if (inventoryDetail.getBalanceCount().subtract(transferBillInfo.getProductCount()).doubleValue() < 0) {
					renderAjaxResultForError("商品库存不足，无法调拨");
			}
		}
	}


	private Boolean inserIntoInventoryInfo(final List<transferBillInfo> transferBill) {
		final String sellerId = getSessionAttr("sellerId").toString();
		  final User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
//				List<Inventory> updateList = new ArrayList<>();
//				List<Inventory> saveList = new ArrayList<>();
                for (transferBillInfo transferBillInfo : transferBill) {
        			SellerProduct sellerProduct = SellerProductQuery.me().findById(transferBillInfo.getSellerProductId());
        			
                    	 //先处理调出仓库的总账
                    	   Inventory outInventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId,sellerProduct.getProductId(),transferBillInfo.getFromWarehouseId());        
                    	   outInventory.setOutCount(outInventory.getOutCount().add(transferBillInfo.getProductCount()));
                    	   outInventory.setOutPrice(outInventory.getInPrice());
                    	   outInventory.setOutAmount(outInventory.getOutCount().multiply(outInventory.getInPrice()));
                    	   outInventory.setBalanceCount(outInventory.getBalanceCount().subtract(transferBillInfo.getProductCount()));
                    	   outInventory.setBalancePrice(outInventory.getInPrice());
                    	   outInventory.setBalanceAmount(outInventory.getBalanceCount().multiply(outInventory.getInPrice()));
                    	   outInventory.setModifyDate(new Date());
                    	  if (!outInventory.saveOrUpdate()) {
							return false;
					     	}
                    	   
                    	 //处理调出仓库的总账子表信息
                    	 //查询目前子表该品项的最新库存
               			  InventoryDetail outDetail = InventoryDetailQuery.me().findByWarehouseIdAndProductId(transferBillInfo.getFromWarehouseId(),transferBillInfo.getSellerProductId());
                          InventoryDetail outInventoryDetail = new InventoryDetail();
                    	  outInventoryDetail.setId(StrKit.getRandomUUID());
                    	  outInventoryDetail.setWarehouseId(transferBillInfo.getFromWarehouseId());
                    	  outInventoryDetail.setSellProductId(transferBillInfo.getSellerProductId());
                    	  outInventoryDetail.setInCount(new BigDecimal(0));
                    	  outInventoryDetail.setInPrice(new BigDecimal(0));
                    	  outInventoryDetail.setInAmount(new BigDecimal(0));
                    	  outInventoryDetail.setOutCount(transferBillInfo.getProductCount());
                    	  outInventoryDetail.setOutPrice(outInventory.getInPrice());
                    	  outInventoryDetail.setOutAmount(outInventoryDetail.getOutCount().multiply(outInventoryDetail.getOutPrice()));
                    	  outInventoryDetail.setBalanceCount(outDetail.getBalanceCount().subtract(transferBillInfo.getProductCount()));
                    	  outInventoryDetail.setBalancePrice(outInventory.getInPrice());
                    	  outInventoryDetail.setBalanceAmount(outInventoryDetail.getBalanceCount().multiply(outInventory.getInPrice()));
                    	  outInventoryDetail.setBizType(Consts.BIZ_TYPE_TRANSFER_OUTSTOCK);
                    	  outInventoryDetail.setBizBillSn(transferBillInfo.getTransferBillSn());
                    	  outInventoryDetail.setBizDate(new Date());
                    	  outInventoryDetail.setBizUserId(transferBillInfo.getBizUserId());
                    	  outInventoryDetail.setDataArea(transferBillInfo.getDataArea());
                    	  outInventoryDetail.setDeptId(user.getDepartmentId());
                    	  outInventoryDetail.setBizUserId(transferBillInfo.getBizUserId());
                    	  outInventoryDetail.setCreateDate(new Date());
                    	  if (!outInventoryDetail.save()) {
							return false;
						}
//                    	 //处理调入仓库的库存总账  
                 	      Inventory inInventory = InventoryQuery.me().findBySellerIdAndProductIdAndWareHouseId(sellerId,sellerProduct.getProductId(),transferBillInfo.getToWarehouseId());
                 	      if (inInventory == null) {
                 	    	 inInventory = new Inventory();
                 	    	 inInventory.setId(StrKit.getRandomUUID());
                 	    	 inInventory.setWarehouseId(transferBillInfo.getToWarehouseId());
                 	    	 inInventory.setProductId(sellerProduct.getProductId());
                 	    	 inInventory.setSellerId(sellerId);
                 	    	 inInventory.setOutCount(new BigDecimal(0));
                 	    	 inInventory.setOutPrice(new BigDecimal(0));
                 	    	 inInventory.setOutAmount(new BigDecimal(0));
                 	    	 inInventory.setInCount(transferBillInfo.getProductCount());
                 	    	 inInventory.setInPrice(outInventory.getInPrice());
                 	    	 inInventory.setInAmount(transferBillInfo.getProductCount().multiply(outInventory.getInPrice()));
                 	    	 inInventory.setBalanceCount(transferBillInfo.getProductCount());
                 	    	 inInventory.setBalancePrice(outInventory.getInPrice());
                 	    	 inInventory.setBalanceAmount(transferBillInfo.getProductCount().multiply(outInventory.getInPrice()));
                 	    	 inInventory.setDataArea(user.getDataArea());
                 	    	 inInventory.setDeptId(user.getDepartmentId());
                 	    	 inInventory.setCreateDate(new Date());
                 	    	 inInventory.setModifyDate(new Date());
                 	    	 if (!inInventory.save()) {
								return false;
							}
						  }else {
	                 	      inInventory.setWarehouseId(transferBillInfo.getToWarehouseId());
	                    	  inInventory.setInCount(inInventory.getInCount().add(transferBillInfo.getProductCount()));
	                    	  inInventory.setInPrice(outInventory.getInPrice());
	                    	  inInventory.setInAmount(inInventory.getInCount().multiply(inInventory.getInPrice()));
	                    	  inInventory.setBalanceCount(inInventory.getBalanceCount().add(transferBillInfo.getProductCount()));
	                    	  inInventory.setBalancePrice(outInventory.getBalancePrice());
	                    	  inInventory.setBalanceAmount(inInventory.getBalanceCount().multiply(outInventory.getInPrice()));
	                    	  inInventory.setModifyDate(new Date());
	                    	  if (!inInventory.saveOrUpdate()) {
								return false;
							}
						  }
	                    	  InventoryDetail inDetail = InventoryDetailQuery.me().findBalanceCountByWarehouseIdId(transferBillInfo.getToWarehouseId(),transferBillInfo.getSellerProductId());
                              if ("null".equals(String.valueOf(inDetail.getBalanceCount()))) {
								inDetail.setBalanceCount(new BigDecimal(0));
								InventoryDetail inInventoryDetail = new InventoryDetail();
		                    	  inInventoryDetail.setId(StrKit.getRandomUUID());
		                    	  inInventoryDetail.setSellProductId(transferBillInfo.getSellerProductId());
		                    	  inInventoryDetail.setWarehouseId(transferBillInfo.getToWarehouseId());
		                    	  inInventoryDetail.setOutCount(new BigDecimal(0));
		                    	  inInventoryDetail.setOutPrice(new BigDecimal(0));
		                    	  inInventoryDetail.setOutAmount(new BigDecimal(0));
		                    	  inInventoryDetail.setInCount(transferBillInfo.getProductCount());
		                    	  inInventoryDetail.setInPrice(outInventory.getInPrice());
		                    	  inInventoryDetail.setInAmount(transferBillInfo.getProductCount().multiply(outInventory.getInPrice()));
		                    	  inInventoryDetail.setBalanceCount(inDetail.getBalanceCount().add(transferBillInfo.getProductCount()));
		                    	  inInventoryDetail.setBalancePrice(outInventory.getInPrice());
		                    	  inInventoryDetail.setBalanceAmount(inInventoryDetail.getBalanceCount().multiply(outInventory.getInPrice()));
		                    	  inInventoryDetail.setBizType(Consts.BIZ_TYPE_TRANSFER_INSTOCK);
		                    	  inInventoryDetail.setBizBillSn(transferBillInfo.getTransferBillSn());
		                    	  inInventoryDetail.setBizDate(new Date());
		                    	  inInventoryDetail.setBizUserId(transferBillInfo.getBizUserId());
		                    	  inInventoryDetail.setDataArea(transferBillInfo.getDataArea());
		                    	  inInventoryDetail.setDeptId(user.getDepartmentId());
		                    	  inInventoryDetail.setCreateDate(new Date());
		                    	  if (!inInventoryDetail.save()) {
									return false;
								}
							  }else {
								  InventoryDetail inInventoryDetail = new InventoryDetail();
		                    	  inInventoryDetail.setId(StrKit.getRandomUUID());
		                    	  inInventoryDetail.setSellProductId(transferBillInfo.getSellerProductId());
		                    	  inInventoryDetail.setWarehouseId(transferBillInfo.getToWarehouseId());
		                    	  inInventoryDetail.setOutCount(new BigDecimal(0));
		                    	  inInventoryDetail.setOutPrice(new BigDecimal(0));
		                    	  inInventoryDetail.setOutAmount(new BigDecimal(0));
		                    	  inInventoryDetail.setInCount(transferBillInfo.getProductCount());
		                    	  inInventoryDetail.setInPrice(outInventory.getInPrice());
		                    	  inInventoryDetail.setInAmount(transferBillInfo.getProductCount().multiply(outInventory.getInPrice()));
		                    	  inInventoryDetail.setBalanceCount(inDetail.getBalanceCount().add(transferBillInfo.getProductCount()));
		                    	  inInventoryDetail.setBalancePrice(outInventory.getInPrice());
		                    	  inInventoryDetail.setBalanceAmount(inInventoryDetail.getBalanceCount().multiply(outInventory.getInPrice()));
		                    	  inInventoryDetail.setBizType(Consts.BIZ_TYPE_TRANSFER_INSTOCK);
		                    	  inInventoryDetail.setBizBillSn(transferBillInfo.getTransferBillSn());
		                    	  inInventoryDetail.setBizDate(new Date());
		                    	  inInventoryDetail.setBizUserId(transferBillInfo.getBizUserId());
		                    	  inInventoryDetail.setDataArea(transferBillInfo.getDataArea());
		                    	  inInventoryDetail.setDeptId(user.getDepartmentId());
		                    	  inInventoryDetail.setCreateDate(new Date());
		                    	  if (!inInventoryDetail.save()) {
									return false;
								}
							}                    	  
	                    	  
						
				}
//                try {
//					Db.batchUpdate(updateList, updateList.size());
//					Db.batchSave(saveList, saveList.size());
//				} catch (Exception e) {
//					e.printStackTrace();
//					return false;
//				}
				return true;
			}
		});
		return isSave;
	}


		//删除调拨单主表及其子表的信息
		@Override
		public void delete() {
			String id = getPara("id");
			final TransferBill transferBill = TransferBillQuery.me().findById(id);
			boolean status = TransferBillQuery.me().deleteAbout(transferBill);
			if (status) {
				renderAjaxResultForSuccess("ok");
			}
		}
		
	
	//通过数据库查最大的单据号加1返回去
	  public String getBillSn() {
	    String sellerCode = getSessionAttr("sellerCode");
		int newNo = 0; 
		List<Integer> list = new ArrayList<>();
		StringBuilder sBuilder = new StringBuilder(BILLTYPE);
		sBuilder.append(sellerCode);
        String Number = DateUtils.dateString();
		//查询数据库当天最大的单据号，并在此基础上加1
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_NORMAL_FORMATTER); 
		String today = null;
		today = sdf.format(new Date());
		sBuilder.append(Number);
		List<TransferBill> transferBills = TransferBillQuery.me().findByBillSn(today);
		//如果为空说明是每天的第一次插入数据，则后三位的话从1开始，即001开始
		if (transferBills.size() == 0) {
			sBuilder.append(startNo);
			return sBuilder.toString();
		}
		//获取当天的所有单据号，并截取后三位来进行比大小，找出最大的那位
		for (TransferBill transferBill : transferBills) {
			transferBill.setTransferBillSn(transferBill.getTransferBillSn().substring(16));
			list.add(Integer.valueOf(transferBill.getTransferBillSn()));
		}	
		Integer arr[]=new Integer[list.size()];
		for(int i=0;i<list.size();i++){
			arr[i]=list.get(i);
		}
		//比较找出单据号最大的那位
		int max=arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i]>max) {
				max = arr[i];
			}
		}
		newNo = max + 1;
		//如果单据号是个位数，前面要补一个0
		if (newNo<=9) {
			sBuilder.append("00000");
			sBuilder.append(String.valueOf(newNo));
		}else if (newNo<=99) {
			sBuilder.append("0000");
			sBuilder.append(String.valueOf(newNo));
		}else {
			sBuilder.append(String.valueOf(newNo));
		}
		
		return sBuilder.toString();
	}
	
	
}
