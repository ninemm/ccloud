
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>QY_Activity complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="QY_Activity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ActivityID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActivityNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ActivityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "QY_Activity", propOrder = {
    "activityID",
    "activityNo",
    "activityName",
    "createTime",
    "modifyTime",
    "flag"
})
public class QYActivity {

    @XmlElement(name = "ActivityID")
    protected String activityID;
    @XmlElement(name = "ActivityNo")
    protected String activityNo;
    @XmlElement(name = "ActivityName")
    protected String activityName;
    @XmlElement(name = "CreateTime")
    protected String createTime;
    @XmlElement(name = "ModifyTime")
    protected String modifyTime;
    @XmlElement(name = "Flag")
    protected int flag;

    /**
     * ��ȡactivityID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityID() {
        return activityID;
    }

    /**
     * ����activityID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityID(String value) {
        this.activityID = value;
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
     * ��ȡactivityName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActivityName() {
        return activityName;
    }

    /**
     * ����activityName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActivityName(String value) {
        this.activityName = value;
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
