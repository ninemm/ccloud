
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="passWord" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="feeTypeList" type="{http://MiddleDBJPFeiYong.com/}ArrayOfQY_BasicFeeType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "userName",
    "passWord",
    "feeTypeList"
})
@XmlRootElement(name = "SyncQYBasicFeeTypeToMidDB")
public class SyncQYBasicFeeTypeToMidDB {

    protected String userName;
    protected String passWord;
    protected ArrayOfQYBasicFeeType feeTypeList;

    /**
     * ��ȡuserName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * ����userName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * ��ȡpassWord���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * ����passWord���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassWord(String value) {
        this.passWord = value;
    }

    /**
     * ��ȡfeeTypeList���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfQYBasicFeeType }
     *     
     */
    public ArrayOfQYBasicFeeType getFeeTypeList() {
        return feeTypeList;
    }

    /**
     * ����feeTypeList���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfQYBasicFeeType }
     *     
     */
    public void setFeeTypeList(ArrayOfQYBasicFeeType value) {
        this.feeTypeList = value;
    }

}
