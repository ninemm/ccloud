
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>QY_BasicShowType complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="QY_BasicShowType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ShowTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ShowTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeeTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeeTypeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Memo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsEnable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "QY_BasicShowType", propOrder = {
    "showTypeID",
    "showTypeName",
    "feeTypeCode",
    "feeTypeID",
    "parentID",
    "memo",
    "isEnable",
    "createTime",
    "modifyTime",
    "flag"
})
public class QYBasicShowType {

    @XmlElement(name = "ShowTypeID")
    protected String showTypeID;
    @XmlElement(name = "ShowTypeName")
    protected String showTypeName;
    @XmlElement(name = "FeeTypeCode")
    protected String feeTypeCode;
    @XmlElement(name = "FeeTypeID")
    protected String feeTypeID;
    @XmlElement(name = "ParentID")
    protected String parentID;
    @XmlElement(name = "Memo")
    protected String memo;
    @XmlElement(name = "IsEnable")
    protected String isEnable;
    @XmlElement(name = "CreateTime")
    protected String createTime;
    @XmlElement(name = "ModifyTime")
    protected String modifyTime;
    @XmlElement(name = "Flag")
    protected int flag;

    /**
     * ��ȡshowTypeID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShowTypeID() {
        return showTypeID;
    }

    /**
     * ����showTypeID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShowTypeID(String value) {
        this.showTypeID = value;
    }

    /**
     * ��ȡshowTypeName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShowTypeName() {
        return showTypeName;
    }

    /**
     * ����showTypeName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShowTypeName(String value) {
        this.showTypeName = value;
    }

    /**
     * ��ȡfeeTypeCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeeTypeCode() {
        return feeTypeCode;
    }

    /**
     * ����feeTypeCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeeTypeCode(String value) {
        this.feeTypeCode = value;
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
     * ��ȡparentID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentID() {
        return parentID;
    }

    /**
     * ����parentID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentID(String value) {
        this.parentID = value;
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
