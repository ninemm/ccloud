
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>QY_BasicFlowFeeRelation complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="QY_BasicFlowFeeRelation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FlowTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeeTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CreateTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ModifyTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Flag" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="IsEnable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QY_BasicFlowFeeRelation", propOrder = {
    "flowTypeID",
    "feeTypeID",
    "createTime",
    "modifyTime",
    "flag",
    "isEnable"
})
public class QYBasicFlowFeeRelation {

    @XmlElement(name = "FlowTypeID")
    protected String flowTypeID;
    @XmlElement(name = "FeeTypeID")
    protected String feeTypeID;
    @XmlElement(name = "CreateTime")
    protected String createTime;
    @XmlElement(name = "ModifyTime")
    protected String modifyTime;
    @XmlElement(name = "Flag")
    protected int flag;
    @XmlElement(name = "IsEnable")
    protected String isEnable;

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
     * ��ȡfeeTypeID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeeTypeID() {
        return feeTypeID;
    }

    /**
     * ����feeTypeID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeTypeID(String value) {
        this.feeTypeID = value;
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

    /**
     * ��ȡisEnable���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsEnable() {
        return isEnable;
    }

    /**
     * ����isEnable���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsEnable(String value) {
        this.isEnable = value;
    }

}
