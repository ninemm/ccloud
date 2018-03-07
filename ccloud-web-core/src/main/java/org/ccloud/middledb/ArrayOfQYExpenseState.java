
package org.ccloud.middledb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfQY_ExpenseState complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ArrayOfQY_ExpenseState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="QY_ExpenseState" type="{http://MiddleDBJPFeiYong.com/}QY_ExpenseState" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfQY_ExpenseState", propOrder = {
    "qyExpenseState"
})
public class ArrayOfQYExpenseState {

    @XmlElement(name = "QY_ExpenseState", nillable = true)
    protected List<QYExpenseState> qyExpenseState;

    /**
     * Gets the value of the qyExpenseState property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qyExpenseState property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQYExpenseState().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QYExpenseState }
     * 
     * 
     */
    public List<QYExpenseState> getQYExpenseState() {
        if (qyExpenseState == null) {
            qyExpenseState = new ArrayList<QYExpenseState>();
        }
        return this.qyExpenseState;
    }

}
