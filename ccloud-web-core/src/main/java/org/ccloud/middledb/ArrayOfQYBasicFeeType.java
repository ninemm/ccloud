
package org.ccloud.middledb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfQY_BasicFeeType complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQY_BasicFeeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QY_BasicFeeType" type="{http://MiddleDBJPFeiYong.com/}QY_BasicFeeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQY_BasicFeeType", propOrder = {
    "qyBasicFeeType"
})
public class ArrayOfQYBasicFeeType {

    @XmlElement(name = "QY_BasicFeeType", nillable = true)
    protected List<QYBasicFeeType> qyBasicFeeType;

    /**
     * Gets the value of the qyBasicFeeType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qyBasicFeeType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQYBasicFeeType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QYBasicFeeType }
     * 
     * 
     */
    public List<QYBasicFeeType> getQYBasicFeeType() {
        if (qyBasicFeeType == null) {
            qyBasicFeeType = new ArrayList<QYBasicFeeType>();
        }
        return this.qyBasicFeeType;
    }

}
