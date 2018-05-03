/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.business;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ccloud.Consts;
import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCorp;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.Department;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.User;
import org.ccloud.model.UserJoinCustomer;
import org.ccloud.model.query.CustomerJoinCorpQuery;
import org.ccloud.model.query.CustomerJoinCustomerTypeQuery;
import org.ccloud.model.query.CustomerQuery;
import org.ccloud.model.query.CustomerTypeQuery;
import org.ccloud.model.query.DepartmentQuery;
import org.ccloud.model.query.SellerCustomerQuery;
import org.ccloud.model.query.UserJoinCustomerQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.model.vo.CustomerExcel;
import org.ccloud.model.vo.CustomerImportResult;
import org.ccloud.utils.MyCollectionUtils;
import org.ccloud.utils.MyExcelImportUtil;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import cn.afterturn.easypoi.excel.entity.ImportParams;

/**
 * 后台客户导入业务逻辑
 * @author wally
 */
public class CustomerImportBiz {
	private static final CustomerImportBiz BIZ = new CustomerImportBiz();
	private CustomerImportBiz() {}
	
	public static CustomerImportBiz me() {
		return BIZ;
	}
	
	private Integer maxProcessThreads = 10;
	private Integer maxProcessCount = 100;
	public CustomerImportResult handleCustomerImport(final File file, final User user, 
			final String sellerId, final String dataArea, 
			final String dept_dataArea, final String userIds,
			final String dealerDataArea) {
		ExecutorService threadPool =  Executors.newFixedThreadPool(maxProcessThreads);
		CompletionService<CustomerImportResult> completionService = new ExecutorCompletionService<CustomerImportResult>(threadPool);
		for(int i = 0; i < maxProcessThreads; i++){
			final Integer threadIndex = i;
			completionService.submit(new Callable<CustomerImportResult>() {
				@Override
				public CustomerImportResult call() throws Exception {
					CustomerImportResult customerImportResult = null;
					Calendar calendar = Calendar.getInstance();
					int inCnt = 0;
					int existCnt = 0;
					Department dept =  DepartmentQuery.me().findByDataArea(dept_dataArea);
					List<Department> departmentList = DepartmentQuery.me().findAllParentDepartmentsBySubDeptId(user.getDepartmentId());
					String corpSellerId = departmentList.get(departmentList.size()-1).getStr("seller_id");
					ImportParams params = new ImportParams();
					long readStartTime = System.currentTimeMillis();
					List<CustomerExcel> list = MyExcelImportUtil.importExcel(file, CustomerExcel.class, params, threadIndex, maxProcessThreads);
					System.out.println("读excel耗时：" + (System.currentTimeMillis() - readStartTime));
					List<String> errorRowIndexes = null;
					if (list != null && list.size() > 0) {
						customerImportResult = new CustomerImportResult();
						final List<Customer> addCustomers = new ArrayList<Customer>();
						final List<SellerCustomer> addSellerCusts = new ArrayList<SellerCustomer>();
						final List<UserJoinCustomer> delUserJoinCustomers = new ArrayList<UserJoinCustomer>();
						final List<UserJoinCustomer> addUserCustomers = new ArrayList<UserJoinCustomer>();
						final List<CustomerJoinCorp> delCustomerCorps = new ArrayList<CustomerJoinCorp>();
						final List<CustomerJoinCorp> addCustomerCorps = new ArrayList<CustomerJoinCorp>();
						final List<CustomerJoinCustomerType> addCustJoinCustTypes = new ArrayList<CustomerJoinCustomerType>();
						final List<CustomerJoinCustomerType> delCustJoinCustTypes = new ArrayList<CustomerJoinCustomerType>();
						CustomerJoinCustomerType customerJoinCustomerType = null;
						CustomerJoinCorp delCustomerJoinCorp = null;
						CustomerJoinCorp addCustomerJoinCorp = null;
						UserJoinCustomer delUserJoinCustomer = null;
						CustomerJoinCustomerType delCustJoinCustType = null;
						CustomerExcel excel = null;
						errorRowIndexes = new ArrayList<String>();
						int rowIndex = 0;
						for (Iterator<CustomerExcel> iterator = list.iterator(); iterator.hasNext();) {
							excel = iterator.next();
							long bizQueryStartTime = System.currentTimeMillis();
							String customerTypeName = excel.getCustomerTypeName();
							String[] customerTypeNames = customerTypeName.split(",");
							boolean checkPass = Boolean.TRUE.booleanValue();
							for (String typeName : customerTypeNames) {
								String id = CustomerTypeQuery.me().findIdByName(typeName,
										dealerDataArea);
								if (StrKit.isBlank(id)) {
									checkPass = Boolean.FALSE.booleanValue();
									errorRowIndexes.add(String.valueOf(params.getStartRows() + rowIndex));
									rowIndex++;
									break;
								}
							}
							if(!checkPass)
								continue;
							String customerId = "";
							String sellerCustomerId = "";
							SellerCustomer sellerCustomer = null;
							if(excel.getCustomerName() == null && excel.getMobile() == null) {
								break;
							}
							
							
//							// 检查客户是否存在
//							Customer customer = CustomerQuery.me().findByCustomerNameAndMobile(excel.getCustomerName(), excel.getMobile());
//
//							if (customer == null) {
//								customer = new Customer();
//								customerId = StrKit.getRandomUUID();
//								customer.set("id", customerId);
//								CustomerImportBiz.this.setCustomer(customer, excel);
//								customer.set("create_date", calendar.getTime());
//								addCustomers.add(customer);
//							} else {
//								customerId = customer.getId();
//								// 检查客户是否存在我销售商的客户中
//								sellerCustomerId = SellerCustomerQuery.me().findsellerCustomerBycusId(customerId, dataArea);
//							}
							sellerCustomerId = SellerCustomerQuery.me().findSellerCustomerByNameMobile(excel.getCustomerName().trim(), excel.getMobile().trim(), dataArea);
							

							if (StrKit.isBlank(sellerCustomerId)) {
								sellerCustomerId = StrKit.getRandomUUID();
								sellerCustomer = new SellerCustomer();
								sellerCustomer.set("id", sellerCustomerId);
								sellerCustomer.set("seller_id", sellerId);
//								sellerCustomer.set("customer_id", customerId);
								sellerCustomer.set("is_enabled", 1);
								sellerCustomer.set("is_archive", 1);
								sellerCustomer.setCustomerName(excel.getCustomerName().trim());
								sellerCustomer.setMobile(excel.getMobile().trim());
								sellerCustomer.set("sub_type", Consts.CUSTOMER_SUB_TYPE_A);
								sellerCustomer.set("customer_kind", Consts.CUSTOMER_KIND_COMMON);
								sellerCustomer.set("nickname", excel.getNickname());
								sellerCustomer.set("data_area", dept_dataArea);
								sellerCustomer.set("dept_id", dept.getId());
								sellerCustomer.set("create_date", calendar.getTime());
								addSellerCusts.add(sellerCustomer);
								inCnt++;
							} else {
								existCnt++;
							}

//							UserJoinCustomerQuery.me().deleteBySelerCustomerId(sellerCustomerId);
							delUserJoinCustomer = new UserJoinCustomer();
							delUserJoinCustomer.setSellerCustomerId(sellerCustomerId);
							delUserJoinCustomers.add(delUserJoinCustomer);
							addUserCustomers.addAll(getInsertUserJoinCustomers(sellerCustomerId, userIds));
//							CustomerJoinCustomerTypeQuery.me().deleteBySellerCustomerId(sellerCustomerId);
							delCustJoinCustType = new CustomerJoinCustomerType();
							delCustJoinCustType.setSellerCustomerId(sellerCustomerId);
							delCustJoinCustTypes.add(delCustJoinCustType);
							
							for (String typeName : customerTypeNames) {
								String id = CustomerTypeQuery.me().findIdByName(typeName,
										dealerDataArea);
								customerJoinCustomerType = new CustomerJoinCustomerType();
								customerJoinCustomerType.set("seller_customer_id", sellerCustomerId);
								customerJoinCustomerType.set("customer_type_id", id);
								addCustJoinCustTypes.add(customerJoinCustomerType);
							}
							
//							CustomerJoinCorpQuery.me().deleteByCustomerIdAndSellerId(customerId, corpSellerId);
//							delCustomerJoinCorp = new CustomerJoinCorp();
//							delCustomerJoinCorp.setCustomerId(customerId);
//							delCustomerJoinCorp.setSellerId(corpSellerId);
//							delCustomerCorps.add(delCustomerJoinCorp);
//							
//							addCustomerJoinCorp = new CustomerJoinCorp();
//							addCustomerJoinCorp.setCustomerId(customer.getId());
//							addCustomerJoinCorp.setSellerId(corpSellerId);
//							addCustomerCorps.add(addCustomerJoinCorp);
//							customerJoinCorp.save();
							System.out.println("逻辑查询耗时：" + (System.currentTimeMillis() - bizQueryStartTime));
							rowIndex++;
						}

						if(delUserJoinCustomers.size() > 0)
							customerImportResult.getDelUserJoinCustomers().addAll(delUserJoinCustomers);
						if(delCustJoinCustTypes.size() > 0)
							customerImportResult.getDelCustJoinCustTypes().addAll(delCustJoinCustTypes);
						if(delCustomerCorps.size() > 0)
							customerImportResult.getDelCustomerCorps().addAll(delCustomerCorps);
						if(addCustomers.size() > 0) {
							customerImportResult.getAddCustomers().addAll(addCustomers);
						}
						if(addSellerCusts.size() > 0) {
							customerImportResult.getAddSellerCusts().addAll(addSellerCusts);
						}
						if(addUserCustomers.size() > 0)
							customerImportResult.getAddUserCustomers().addAll(addUserCustomers);
						if(addCustJoinCustTypes.size() > 0)
							customerImportResult.getAddCustJoinCustTypes().addAll(addCustJoinCustTypes);
						if(addCustomerCorps.size() > 0)
							customerImportResult.getAddCustomerCorps().addAll(addCustomerCorps);
						
						customerImportResult.setErrorRowIndexes(errorRowIndexes);
						customerImportResult.setSuccessNum(inCnt);
						customerImportResult.setExistNum(existCnt);
					}
					return customerImportResult;
				}
			});
		}
		
		/*******拿到线程池返回的结果********/
		List<CustomerImportResult> importResults = new ArrayList<CustomerImportResult>();
		CustomerImportResult importResult = null;
		for(int i = 0 ; i < maxProcessThreads; i++){
			try {
				importResult = completionService.take().get();
				if(importResult != null) {
					importResults.add(importResult);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		threadPool.shutdown();
		
		/*****对线程池结果进行汇总*****/
		final CustomerImportResult totalResult = new CustomerImportResult();
		List<String> totalErrorRowIndexes = totalResult.getErrorRowIndexes();
		Integer totalSuccessNum = 0;
		Integer totalExistNum = 0;
		if(importResults.size() > 0) {
			for (Iterator<CustomerImportResult> iterator = importResults.iterator(); iterator.hasNext();) {
				importResult = iterator.next();
				if(importResult.getErrorRowIndexes() != null && importResult.getErrorRowIndexes().size() > 0) {
					totalErrorRowIndexes.addAll(importResult.getErrorRowIndexes());
				}
				totalSuccessNum += importResult.getSuccessNum() != null ? importResult.getSuccessNum() : 0;
				totalExistNum += importResult.getExistNum() != null ? importResult.getExistNum() : 0;
				if(importResult.getDelUserJoinCustomers().size() > 0)
					totalResult.getDelUserJoinCustomers().addAll(importResult.getDelUserJoinCustomers());
				if(importResult.getDelCustJoinCustTypes().size() > 0)
					totalResult.getDelCustJoinCustTypes().addAll(importResult.getDelCustJoinCustTypes());
				if(importResult.getDelCustomerCorps().size() > 0)
					totalResult.getDelCustomerCorps().addAll(importResult.getDelCustomerCorps());
				if(importResult.getAddCustomers().size() > 0) {
					totalResult.getAddCustomers().addAll(importResult.getAddCustomers());
				}
				if(importResult.getAddSellerCusts().size() > 0) {
					totalResult.getAddSellerCusts().addAll(importResult.getAddSellerCusts());
				}
				if(importResult.getAddUserCustomers().size() > 0)
					totalResult.getAddUserCustomers().addAll(importResult.getAddUserCustomers());
				if(importResult.getAddCustJoinCustTypes().size() > 0)
					totalResult.getAddCustJoinCustTypes().addAll(importResult.getAddCustJoinCustTypes());
				if(importResult.getAddCustomerCorps().size() > 0)
					totalResult.getAddCustomerCorps().addAll(importResult.getAddCustomerCorps());
			}
		}
		
		/*******将解析的数据批量入库******/
		long batchStartTime = System.currentTimeMillis();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				try {
					if(totalResult.getDelUserJoinCustomers().size() > 0)
						UserJoinCustomerQuery.me().batchDelete(totalResult.getDelUserJoinCustomers());
					if(totalResult.getDelCustJoinCustTypes().size() > 0)
						CustomerJoinCustomerTypeQuery.me().batchDelete(totalResult.getDelCustJoinCustTypes());
					if(totalResult.getDelCustomerCorps().size() > 0)
						CustomerJoinCorpQuery.me().batchDeleteByCustomerIdAndSellerId(totalResult.getDelCustomerCorps());
					if(totalResult.getAddCustomers().size() > 0) {
						Db.batchSave(totalResult.getAddCustomers(), 10000);
					}
					if(totalResult.getAddSellerCusts().size() > 0) {
						Db.batchSave(MyCollectionUtils.removeRepeat(totalResult.getAddSellerCusts()), 10000);
					}
					if(totalResult.getAddUserCustomers().size() > 0)
						Db.batchSave(MyCollectionUtils.removeRepeat(totalResult.getAddUserCustomers()), 10000);
					if(totalResult.getAddCustJoinCustTypes().size() > 0)
						Db.batchSave(MyCollectionUtils.removeRepeat(totalResult.getAddCustJoinCustTypes()), 10000);
					if(totalResult.getAddCustomerCorps().size() > 0)
						Db.batchSave(MyCollectionUtils.removeRepeat(totalResult.getAddCustomerCorps()), 10000);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		long batchEndTime = System.currentTimeMillis();
		System.out.println("batch耗时：" + (batchEndTime - batchStartTime));
		return totalResult;
	}
	
	private void setCustomer(Customer customer, CustomerExcel excel) {
		customer.set("customer_name", excel.getCustomerName() != null ? excel.getCustomerName().trim() : null);
		customer.set("contact", excel.getContact() != null ? excel.getContact().trim() : null);
		customer.set("mobile", excel.getMobile() != null ? excel.getMobile().trim() : null);
		customer.set("email", excel.getEmail() != null ? excel.getEmail().trim() : null);
		customer.set("prov_name", excel.getProvName());
		customer.set("city_name", excel.getCityName());
		customer.set("country_name", excel.getCountyName());
		customer.set("address", excel.getAddress() != null ? excel.getAddress() : null);
	}
	
	private List<UserJoinCustomer> getInsertUserJoinCustomers(String sellerCustomerId, String userIds) {
		List<UserJoinCustomer> addUserJoinCustomers = new ArrayList<UserJoinCustomer>();
		String[] userIdArray = userIds.split(",");
		UserJoinCustomer userJoinCustomer = null;
		for (String id : userIdArray) {
			User user = UserQuery.me().findById(id);
			userJoinCustomer = new UserJoinCustomer();
			userJoinCustomer.set("seller_customer_id", sellerCustomerId);
			userJoinCustomer.set("user_id", id);
			userJoinCustomer.set("dept_id", user.getDepartmentId());
			userJoinCustomer.set("data_area", user.getDataArea());
			addUserJoinCustomers.add(userJoinCustomer);
		}
		return addUserJoinCustomers;
	}
}
