
package org.ccloud.middledb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfQY_BasicFlowType complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQY_BasicFlowType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QY_BasicFlowType" type="{http://MiddleDBJPFeiYong.com/}QY_BasicFlowType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQY_BasicFlowType", propOrder = {
    "qyBasicFlowType"
})
public class ArrayOfQYBasicFlowType {

    @XmlElement(name = "QY_BasicFlowType", nillable = true)
    protected List<QYBasicFlowType> qyBasicFlowType;

    /**
     * Gets the value of the qyBasicFlowType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qyBasicFlowType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQYBasicFlowType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QYBasicFlowType }
     * 
     * 
     */
    public List<QYBasicFlowType> getQYBasicFlowType() {
        if (qyBasicFlowType == null) {
            qyBasicFlowType = new ArrayList<QYBasicFlowType>();
        }
        return this.qyBasicFlowType;
    }

}
