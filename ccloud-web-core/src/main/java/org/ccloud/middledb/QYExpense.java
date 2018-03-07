
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>QY_Expense complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="QY_Expense">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExpenseID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FlowID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActivityNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FlowTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FlowNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpenseName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FranchiserCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BrandCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProvinceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CityCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpenseBeginDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpenseEndDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InputDay" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InputAccountType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InputPayType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ApplyAmount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="OperateYear" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ApplyPersonNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ApplyPersonName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ApplyTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CreateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModifyTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Flag" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QY_Expense", propOrder = {
    "expenseID",
    "flowID",
    "activityNo",
    "flowTypeID",
    "flowNo",
    "expenseName",
    "franchiserCode",
    "brandCode",
    "provinceCode",
    "cityCode",
    "expenseBeginDate",
    "expenseEndDate",
    "inputDay",
    "memo",
    "inputAccountType",
    "inputPayType",
    "applyAmount",
    "operateYear",
    "applyPersonNumber",
    "applyPersonName",
    "applyTime",
    "createTime",
    "modifyTime",
    "flag"
})
public class QYExpense {

    @XmlElement(name = "ExpenseID")
    protected String expenseID;
    @XmlElement(name = "FlowID")
    protected String flowID;
    @XmlElement(name = "ActivityNo")
    protected String activityNo;
    @XmlElement(name = "FlowTypeID")
    protected String flowTypeID;
    @XmlElement(name = "FlowNo")
    protected String flowNo;
    @XmlElement(name = "ExpenseName")
    protected String expenseName;
    @XmlElement(name = "FranchiserCode")
    protected String franchiserCode;
    @XmlElement(name = "BrandCode")
    protected String brandCode;
    @XmlElement(name = "ProvinceCode")
    protected String provinceCode;
    @XmlElement(name = "CityCode")
    protected String cityCode;
    @XmlElement(name = "ExpenseBeginDate")
    protected String expenseBeginDate;
    @XmlElement(name = "ExpenseEndDate")
    protected String expenseEndDate;
    @XmlElement(name = "InputDay")
    protected int inputDay;
    @XmlElement(name = "Memo")
    protected String memo;
    @XmlElement(name = "InputAccountType")
    protected String inputAccountType;
    @XmlElement(name = "InputPayType")
    protected String inputPayType;
    @XmlElement(name = "ApplyAmount")
    protected float applyAmount;
    @XmlElement(name = "OperateYear")
    protected String operateYear;
    @XmlElement(name = "ApplyPersonNumber")
    protected String applyPersonNumber;
    @XmlElement(name = "ApplyPersonName")
    protected String applyPersonName;
    @XmlElement(name = "ApplyTime")
    protected String applyTime;
    @XmlElement(name = "CreateTime")
    protected String createTime;
    @XmlElement(name = "ModifyTime")
    protected String modifyTime;
    @XmlElement(name = "Flag")
    protected int flag;

    /**
     * ��ȡexpenseID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpenseID() {
        return expenseID;
    }

    /**
     * ����expenseID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpenseID(String value) {
        this.expenseID = value;
    }

    /**
     * ��ȡflowID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlowID() {
        return flowID;
    }

    /**
     * ����flowID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlowID(String value) {
        this.flowID = value;
    }

    /**
     * ��ȡactivityNo���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityNo() {
        return activityNo;
    }

    /**
     * ����activityNo���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityNo(String value) {
        this.activityNo = value;
    }

    /**
     * ��ȡflowTypeID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlowTypeID() {
        return flowTypeID;
    }

    /**
     * ����flowTypeID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlowTypeID(String value) {
        this.flowTypeID = value;
    }

    /**
     * ��ȡflowNo���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlowNo() {
        return flowNo;
    }

    /**
     * ����flowNo���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlowNo(String value) {
        this.flowNo = value;
    }

    /**
     * ��ȡexpenseName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpenseName() {
        return expenseName;
    }

    /**
     * ����expenseName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpenseName(String value) {
        this.expenseName = value;
    }

    /**
     * ��ȡfranchiserCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFranchiserCode() {
        return franchiserCode;
    }

    /**
     * ����franchiserCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFranchiserCode(String value) {
        this.franchiserCode = value;
    }

    /**
     * ��ȡbrandCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * ����brandCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrandCode(String value) {
        this.brandCode = value;
    }

    /**
     * ��ȡprovinceCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvinceCode() {
        return provinceCode;
    }

    /**
     * ����provinceCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvinceCode(String value) {
        this.provinceCode = value;
    }

    /**
     * ��ȡcityCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * ����cityCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityCode(String value) {
        this.cityCode = value;
    }

    /**
     * ��ȡexpenseBeginDate���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpenseBeginDate() {
        return expenseBeginDate;
    }

    /**
     * ����expenseBeginDate���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpenseBeginDate(String value) {
        this.expenseBeginDate = value;
    }

    /**
     * ��ȡexpenseEndDate���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpenseEndDate() {
        return expenseEndDate;
    }

    /**
     * ����expenseEndDate���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpenseEndDate(String value) {
        this.expenseEndDate = value;
    }

    /**
     * ��ȡinputDay���Ե�ֵ��
     * 
     */
    public int getInputDay() {
        return inputDay;
    }

    /**
     * ����inputDay���Ե�ֵ��
     * 
     */
    public void setInputDay(int value) {
        this.inputDay = value;
    }

    /**
     * ��ȡmemo���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMemo() {
        return memo;
    }

    /**
     * ����memo���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMemo(String value) {
        this.memo = value;
    }

    /**
     * ��ȡinputAccountType���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputAccountType() {
        return inputAccountType;
    }

    /**
     * ����inputAccountType���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputAccountType(String value) {
        this.inputAccountType = value;
    }

    /**
     * ��ȡinputPayType���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputPayType() {
        return inputPayType;
    }

    /**
     * ����inputPayType���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputPayType(String value) {
        this.inputPayType = value;
    }

    /**
     * ��ȡapplyAmount���Ե�ֵ��
     * 
     */
    public float getApplyAmount() {
        return applyAmount;
    }

    /**
     * ����applyAmount���Ե�ֵ��
     * 
     */
    public void setApplyAmount(float value) {
        this.applyAmount = value;
    }

    /**
     * ��ȡoperateYear���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperateYear() {
        return operateYear;
    }

    /**
     * ����operateYear���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperateYear(String value) {
        this.operateYear = value;
    }

    /**
     * ��ȡapplyPersonNumber���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplyPersonNumber() {
        return applyPersonNumber;
    }

    /**
     * ����applyPersonNumber���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplyPersonNumber(String value) {
        this.applyPersonNumber = value;
    }

    /**
     * ��ȡapplyPersonName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplyPersonName() {
        return applyPersonName;
    }

    /**
     * ����applyPersonName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplyPersonName(String value) {
        this.applyPersonName = value;
    }

    /**
     * ��ȡapplyTime���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplyTime() {
        return applyTime;
    }

    /**
     * ����applyTime���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplyTime(String value) {
        this.applyTime = value;
    }

    /**
     * ��ȡcreateTime���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * ����createTime���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateTime(String value) {
        this.createTime = value;
    }

    /**
     * ��ȡmodifyTime���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifyTime() {
        return modifyTime;
    }

    /**
     * ����modifyTime���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifyTime(String value) {
        this.modifyTime = value;
    }

    /**
     * ��ȡflag���Ե�ֵ��
     * 
     */
    public int getFlag() {
        return flag;
    }

    /**
     * ����flag���Ե�ֵ��
     * 
     */
    public void setFlag(int value) {
        this.flag = value;
    }

}
