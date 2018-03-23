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
import org.ccloud.model.StockTaking;
import org.ccloud.model.StockTakingDetail;
import org.ccloud.model.User;
import org.ccloud.model.Warehouse;
import org.ccloud.model.query.InventoryDetailQuery;
import org.ccloud.model.query.InventoryQuery;
import org.ccloud.model.query.SellerProductQuery;
import org.ccloud.model.query.StockTakingDetailQuery;
import org.ccloud.model.query.StockTakingQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.query.WarehouseQuery;
import org.ccloud.model.vo.StockTakingInfo;
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
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@RouterMapping(url = "/admin/stockTaking", viewPath = "/WEB-INF/admin/stock_taking")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _StockTakingController extends JBaseCRUDController<StockTaking> {

	public static final String BILLTYPE = "ST";
	// 目前系统还没有企业编号，先创建一个100000占位
	public static final String COMPANYCODE = "100000";

	public void list() {
		String keyword = getPara("keyword");
		if (StrKit.notBlank(keyword)) {
			keyword = StringUtils.urlDecode(keyword);
			setAttr("keyword", keyword);
		}
		String seller_id=getSessionAttr("sellerId").toString();
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		Page<StockTaking> page = StockTakingQuery.me().paginate(getPageNumber(), getPageSize(), keyword,seller_id,userId);
		Map<String, Object> map = ImmutableMap.of("total", page.getTotalRow(), "rows", page.getList());
		renderJson(map);
	}

	public void edit() {
		//判断当前是新增  还是  修改
		String id = getPara("id");
		User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
		String userId = user.getId();
		if (id != null) {
			StockTaking stockTaking = StockTakingQuery.me().findById(id);
			setAttr("stockTaking", stockTaking);
		}
		List<Warehouse> wlist = WarehouseQuery.me().findWarehouseByUserId(userId);
		setAttr("wlist", wlist);
		List<User> ulist = UserQuery.me().findUserList(userId);
		setAttr("ulist", ulist);
		List<StockTakingInfo> ilist = StockTakingDetailQuery.me().findByStockTakingDetailId(id);
		setAttr("ilist", ilist);
		
	}

	public void enable() {
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				String id = getPara("id");
				int isEnabled = getParaToInt("isEnabled");
				StockTaking stockTaking = StockTakingQuery.me().findById(id);
				String warehouse_id=stockTaking.getWarehouseId();
				String seller_id=stockTaking.getSellerId();
				//初始化仓库
				Warehouse warehouse=WarehouseQuery.me().findById(warehouse_id);
				if (warehouse.getIsDefault()!=1) {
					warehouse.setIsInited(1);
					boolean updateWarehouse = warehouse.update();
					if (!updateWarehouse) {
						renderAjaxResultForError("更新Warehouse失败 ");
						return false;
					}
				}
				stockTaking.setStatus(isEnabled);
				if (stockTaking.saveOrUpdate()) {
					//遍历此次盘点的所有商品
					List<Map<String, Object>>listMap=StockTakingDetailQuery.me().findByStockTakingDetailId1(id);
					for (int i = 0; i < listMap.size(); i++) {
						Inventory inventory=new Inventory();
						String seller_product_id=(String) listMap.get(i).get("seller_product_id");
						String product_id=(String) listMap.get(i).get("product_id");
						//判断此商品是否已经在仓库中
						List<Record> findByInventory = StockTakingDetailQuery.me().findByInventory(product_id,warehouse_id,seller_id);
						//当前商品的存入数量  大单位换算关系  商品大单位的价格  商品总价格
						BigDecimal productCount = new BigDecimal(listMap.get(i).get("product_count").toString());
						BigDecimal price=new BigDecimal(listMap.get(i).get("price").toString());
						BigDecimal amount=productCount.multiply(price);
						if (findByInventory.size()!=0) {
							//存在--只更改数量 总价格
							inventory=InventoryQuery.me().findById(findByInventory.get(0).getStr("id"));
							if ( productCount.compareTo(BigDecimal.ZERO)>0) {
								inventory.setInCount( inventory.getInCount().add(productCount));
								inventory.setInAmount(inventory.getInAmount().add(amount));
							}else {
								inventory.setOutCount( inventory.getOutCount().add(productCount.abs()));
								inventory.setOutAmount(inventory.getOutAmount().add(amount.abs()));
							}
							inventory.setModifyDate(new Date());
							inventory.setBalanceCount(inventory.getBalanceCount().add(productCount));
							inventory.setBalanceAmount(inventory.getBalanceAmount().add(amount));
							boolean updateInventory = inventory.update();
							if (!updateInventory) {
								renderAjaxResultForError("更新Inventory失败");
								return false;
							}
						}else {
							//不存在--添加新的记录
							inventory.setId(StrKit.getRandomUUID());
							inventory.setWarehouseId(warehouse_id);
							inventory.setProductId(product_id);
							inventory.setSellerId(seller_id);
							inventory.setInCount(productCount);
							inventory.setInAmount(amount);
							inventory.setInPrice(price);
							inventory.setOutAmount(new BigDecimal(0));
							inventory.setOutCount(new BigDecimal(0));
							inventory.setOutPrice(price);
							inventory.setBalanceCount(productCount);
							inventory.setBalanceAmount(amount);
							inventory.setBalancePrice(price);
							inventory.setDataArea(stockTaking.getDataArea());
							inventory.setDeptId(stockTaking.getDeptId());
							inventory.setCreateDate(new Date());
							boolean saveInventory = inventory.save();
							if (!saveInventory) {
								renderAjaxResultForError("添加Inventory失败");
								return false;
							}
						}
						//添加库存明细
						InventoryDetail inventoryDetail=new InventoryDetail();
						inventoryDetail.setId(StrKit.getRandomUUID());
						inventoryDetail.setWarehouseId(warehouse_id);
						inventoryDetail.setSellProductId(seller_product_id);
						
						//根据seller_product_id添加各自的库存明细
						InventoryDetail findByInventoryDetail = InventoryDetailQuery.me().findBySellerProductId(seller_product_id,warehouse_id);
						if (findByInventoryDetail==null) {
							inventoryDetail.setBalanceCount(productCount);
							inventoryDetail.setBalanceAmount(amount);
						}else {
							inventoryDetail.setBalanceCount(findByInventoryDetail.getBalanceCount().add(productCount));
							inventoryDetail.setBalanceAmount(findByInventoryDetail.getBalanceAmount().add(amount));
						}
						
						//业务类型  盘盈入库--100208  盘亏出库--100209
						int compareTo = productCount.compareTo(new BigDecimal(0));
						if (compareTo<0) {
							inventoryDetail.setOutCount(productCount.abs());
							inventoryDetail.setOutAmount(amount.abs());
							inventoryDetail.setOutPrice(price);
							inventoryDetail.setBizType(Consts.BIZ_TYPE_TRANSFER_REDUCE_OUTSTOCK);
						}else {
							inventoryDetail.setInCount(productCount);
							inventoryDetail.setInAmount(amount);
							inventoryDetail.setInPrice(price);
							inventoryDetail.setBizType(Consts.BIZ_TYPE_TRANSFER_PLUS_INSTOCK);
						}
						inventoryDetail.setBalancePrice(price);
						inventoryDetail.setBizBillSn(stockTaking.getStockTakingSn());
						inventoryDetail.setBizDate(stockTaking.getBizDate());
						inventoryDetail.setBizUserId(stockTaking.getBizUserId());
						inventoryDetail.setRemark((String) listMap.get(i).get("remark"));
						inventoryDetail.setDataArea(stockTaking.getDataArea());
						inventoryDetail.setDeptId(stockTaking.getDeptId());
						inventoryDetail.setCreateDate(new Date());
						boolean saveInventoryDetail = inventoryDetail.save();
						if (!saveInventoryDetail) {
							renderAjaxResultForError("更新InventoryDetail失败");
							return false;
						}
						
						//获取经销商此商品的信息 更新库存
						SellerProduct sellerProduct = SellerProductQuery.me().findById(seller_product_id);
						BigDecimal storeCount = sellerProduct.getStoreCount();
						if (storeCount==null) {
							sellerProduct.setStoreCount(productCount);
						}else {
							sellerProduct.setStoreCount(productCount.add(storeCount));
						}
						boolean updateSellerProduct = sellerProduct.update();
						if (!updateSellerProduct) {
							renderAjaxResultForError("更新SellerProduct失败");
							return false;
						}
					}
				} else {
					renderAjaxResultForError("更新失败");
					return false;
				}
				renderAjaxResultForSuccess("更新成功");
				return true;
		
			}
		});
	}

	@Override
	public void save() {
		StockTaking stockTaking = getModel(StockTaking.class);
		if (stockTaking.getStatus() == null || stockTaking.getStatus() == 0) {
			User user = getSessionAttr(Consts.SESSION_LOGINED_USER);
			stockTaking.setDeptId(user.getDepartmentId());
			stockTaking.setDataArea(user.getDataArea());
			stockTaking.setInputUserId(user.getId());
			stockTaking.setSellerId( getSessionAttr("sellerId").toString());
			Map<String, String[]> map = getParaMap();
			boolean update = false;
			if (StringUtils.isBlank(stockTaking.getId())) {
				stockTaking.setId(StrKit.getRandomUUID());
				stockTaking.setCreateDate(new Date());
				stockTaking.setBizDate(new Date());
				stockTaking.setStatus(0);
				String billno = this.getBillSn();
				stockTaking.setStockTakingSn(billno);
			} else {
				update = true;
			}
			boolean status = this.saveStockTakingInfo(map, stockTaking, update);
			if (status) {
				renderAjaxResultForSuccess("ok");
			}
		} else {
			renderAjaxResultForError("保存失败,该单子已盘点完成！");
		}
	}

	//添加盘点明细
	public boolean saveStockTakingInfo(final Map<String, String[]> map, final StockTaking stockTaking,
			final boolean update) {
		boolean isSave = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				if (update) {
					stockTaking.saveOrUpdate();
					List<StockTakingDetail> iSaveList = new ArrayList<>();
					int loopEnd = 1;
					// 先根据主表ID，删除盘点单子表相关记录
					List<StockTakingDetail> list = StockTakingDetailQuery.me()
							.deleteByStockTakingId(stockTaking.getId());
					for (StockTakingDetail stockTakingDetail : list) {
						stockTakingDetail.delete();
					}
					// 再把修改的内容插入子表
					String[] factIndex = map.get("factIndex");
					for (int i = 1; i < factIndex.length; i++) {
						StockTakingDetail stockTakingDetail = getModel(StockTakingDetail.class);
					
						String sellerProductId = StringUtils
								.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].seller_product_id"));
						String productCount = StringUtils
								.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].product_count"));
						String remark = StringUtils
								.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].remark"));
						
						if ("0".equals(productCount)||null==productCount) {
							continue;
						}
						stockTakingDetail.setSellerProductId(sellerProductId);
						stockTakingDetail.setProductCount(new BigDecimal(productCount));
						stockTakingDetail.setRemark(remark);
						stockTakingDetail.setStockTakingId(stockTaking.getId());
						stockTakingDetail.setId(StrKit.getRandomUUID());
						stockTakingDetail.setProductAmount(BigDecimal.valueOf(10000));
						stockTakingDetail.setDeptId(stockTaking.getDeptId());
						stockTakingDetail.setDataArea(stockTaking.getDataArea());
						stockTakingDetail.setCreateDate(stockTaking.getCreateDate());
						stockTakingDetail.setModifyDate(new Date());
						iSaveList.add(stockTakingDetail);
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
				} else {
					stockTaking.save();
					// 存储盘点单子表信息
					List<StockTakingDetail> iSaveList = new ArrayList<>();
					String[] factIndex = map.get("factIndex");
					for (int i = 1; i < factIndex.length; i++) {
						
						StockTakingDetail stockTakingDetail = getModel(StockTakingDetail.class);
						String sellerProductId = StringUtils.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].seller_product_id"));
						String productCount = StringUtils
								.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].product_count"));
						String remark = StringUtils.getArrayFirst(map.get("stockTakingList[" + factIndex[i] + "].remark"));
						
						if ("0".equals(productCount)||null==productCount) {
							continue;
						}
						stockTakingDetail.setSellerProductId(sellerProductId);
						stockTakingDetail.setProductCount(new BigDecimal(productCount));
						stockTakingDetail.setRemark(remark);
						stockTakingDetail.setStockTakingId(stockTaking.getId());
						stockTakingDetail.setId(StrKit.getRandomUUID());
						stockTakingDetail.setProductAmount(BigDecimal.valueOf(10000));
						stockTakingDetail.setDeptId(stockTaking.getDeptId());
						stockTakingDetail.setDataArea(stockTaking.getDataArea());
						stockTakingDetail.setCreateDate(new Date());
						iSaveList.add(stockTakingDetail);
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

	// 删除盘点单主表及其子表的信息
	@Override
	public void delete() {
		String id = getPara("id");
		final StockTaking stockTaking = StockTakingQuery.me().findById(id);
		boolean status = StockTakingQuery.me().deleteAbout(stockTaking);
		if (status) {
			renderAjaxResultForSuccess("ok");
		}
	}

	// 通过数据库查最大的单据号加1返回去
	public String getBillSn() {
		int newNo = 0;
		List<Integer> list = new ArrayList<>();
		String startNo = "000001";
		StringBuilder sBuilder = new StringBuilder(BILLTYPE);
		sBuilder.append(COMPANYCODE);
		String Number = DateUtils.dateString();
		// 查询数据库当天最大的单据号，并在此基础上加1
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DEFAULT_NORMAL_FORMATTER);
		String today = null;
		today = sdf.format(new Date());
		sBuilder.append(Number);
		List<StockTaking> stockTakings = StockTakingQuery.me().findByBillSn(today);
		// 如果为空说明是每天的第一次插入数据，则后三位的话从1开始，即001开始
		if (stockTakings.size() == 0) {
			sBuilder.append(startNo);
			return sBuilder.toString();
		}
		// 获取当天的所有单据号，并截取后三位来进行比大小，找出最大的那位
		for (StockTaking stockTaking : stockTakings) {
			stockTaking.setStockTakingSn(stockTaking.getStockTakingSn().substring(16));
			list.add(Integer.valueOf(stockTaking.getStockTakingSn()));
		}
		Integer arr[] = new Integer[list.size()];
		for (int i = 0; i < list.size(); i++) {
			arr[i] = list.get(i);
		}
		// 比较找出单据号最大的那位
		int max = arr[0];
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		newNo = max + 1;
		// 如果单据号是个位数，前面要补一个0
		if (newNo <= 9) {
			sBuilder.append("00000");
			sBuilder.append(String.valueOf(newNo));
		} else if (newNo <= 99) {
			sBuilder.append("0000");
			sBuilder.append(String.valueOf(newNo));
		} else {
			sBuilder.append(String.valueOf(newNo));
		}

		return sBuilder.toString();
	}

	public void getProductInfo() {
		String  warehouseId = getPara("warehouse_id");
		String seller_id=getSessionAttr("sellerId").toString();
		List<Record>list=StockTakingDetailQuery.me().findByWarehouseIdAndSellerId(warehouseId,seller_id);
		renderJson(list);
	}
}
