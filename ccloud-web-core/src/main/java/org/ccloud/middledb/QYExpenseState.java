
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>QY_ExpenseState complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="QY_ExpenseState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ExpenseID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FlowID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FlowNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="StateName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "QY_ExpenseState", propOrder = {
    "expenseID",
    "flowID",
    "flowNo",
    "stateID",
    "stateName",
    "createTime",
    "modifyTime",
    "flag"
})
public class QYExpenseState {

    @XmlElement(name = "ExpenseID")
    protected String expenseID;
    @XmlElement(name = "FlowID")
    protected String flowID;
    @XmlElement(name = "FlowNo")
    protected String flowNo;
    @XmlElement(name = "StateID")
    protected String stateID;
    @XmlElement(name = "StateName")
    protected String stateName;
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
     * ��ȡstateID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateID() {
        return stateID;
    }

    /**
     * ����stateID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateID(String value) {
        this.stateID = value;
    }

    /**
     * ��ȡstateName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * ����stateName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStateName(String value) {
        this.stateName = value;
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
