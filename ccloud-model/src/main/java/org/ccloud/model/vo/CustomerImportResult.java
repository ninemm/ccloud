/**
 * Copyright (c) 2015-2018, Wally Wang 王勇 (wally8292@163.com).
 */
package org.ccloud.model.vo;

import java.util.ArrayList;
import java.util.List;

import org.ccloud.model.Customer;
import org.ccloud.model.CustomerJoinCorp;
import org.ccloud.model.CustomerJoinCustomerType;
import org.ccloud.model.SellerCustomer;
import org.ccloud.model.UserJoinCustomer;

/**
 * @author wally
 *
 */
public class CustomerImportResult {
	private List<String> errorRowIndexes = new ArrayList<String>();
	private Integer successNum;
	private Integer existNum;
	
	private List<Customer> addCustomers = new ArrayList<Customer>();
	private List<SellerCustomer> addSellerCusts = new ArrayList<SellerCustomer>();
	private List<UserJoinCustomer> delUserJoinCustomers = new ArrayList<UserJoinCustomer>();
	private List<UserJoinCustomer> addUserCustomers = new ArrayList<UserJoinCustomer>();
	private List<CustomerJoinCorp> delCustomerCorps = new ArrayList<CustomerJoinCorp>();
	private List<CustomerJoinCorp> addCustomerCorps = new ArrayList<CustomerJoinCorp>();
	private List<CustomerJoinCustomerType> addCustJoinCustTypes = new ArrayList<CustomerJoinCustomerType>();
	private List<CustomerJoinCustomerType> delCustJoinCustTypes = new ArrayList<CustomerJoinCustomerType>();
	
	
	public List<String> getErrorRowIndexes() {
		return errorRowIndexes;
	}
	public void setErrorRowIndexes(List<String> errorRowIndexes) {
		this.errorRowIndexes = errorRowIndexes;
	}
	public Integer getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(Integer successNum) {
		this.successNum = successNum;
	}
	public Integer getExistNum() {
		return existNum;
	}
	public void setExistNum(Integer existNum) {
		this.existNum = existNum;
	}
	public List<Customer> getAddCustomers() {
		return addCustomers;
	}
	public void setAddCustomers(List<Customer> addCustomers) {
		this.addCustomers = addCustomers;
	}
	public List<SellerCustomer> getAddSellerCusts() {
		return addSellerCusts;
	}
	public void setAddSellerCusts(List<SellerCustomer> addSellerCusts) {
		this.addSellerCusts = addSellerCusts;
	}
	public List<UserJoinCustomer> getDelUserJoinCustomers() {
		return delUserJoinCustomers;
	}
	public void setDelUserJoinCustomers(List<UserJoinCustomer> delUserJoinCustomers) {
		this.delUserJoinCustomers = delUserJoinCustomers;
	}
	public List<UserJoinCustomer> getAddUserCustomers() {
		return addUserCustomers;
	}
	public void setAddUserCustomers(List<UserJoinCustomer> addUserCustomers) {
		this.addUserCustomers = addUserCustomers;
	}
	public List<CustomerJoinCorp> getDelCustomerCorps() {
		return delCustomerCorps;
	}
	public void setDelCustomerCorps(List<CustomerJoinCorp> delCustomerCorps) {
		this.delCustomerCorps = delCustomerCorps;
	}
	public List<CustomerJoinCorp> getAddCustomerCorps() {
		return addCustomerCorps;
	}
	public void setAddCustomerCorps(List<CustomerJoinCorp> addCustomerCorps) {
		this.addCustomerCorps = addCustomerCorps;
	}
	public List<CustomerJoinCustomerType> getAddCustJoinCustTypes() {
		return addCustJoinCustTypes;
	}
	public void setAddCustJoinCustTypes(List<CustomerJoinCustomerType> addCustJoinCustTypes) {
		this.addCustJoinCustTypes = addCustJoinCustTypes;
	}
	public List<CustomerJoinCustomerType> getDelCustJoinCustTypes() {
		return delCustJoinCustTypes;
	}
	public void setDelCustJoinCustTypes(List<CustomerJoinCustomerType> delCustJoinCustTypes) {
		this.delCustJoinCustTypes = delCustJoinCustTypes;
	}
	
	
}
