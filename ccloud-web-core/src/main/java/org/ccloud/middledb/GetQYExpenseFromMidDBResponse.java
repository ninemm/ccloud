
package org.ccloud.middledb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetQY_ExpenseFromMidDBResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "getQYExpenseFromMidDBResult"
})
@XmlRootElement(name = "GetQY_ExpenseFromMidDBResponse")
public class GetQYExpenseFromMidDBResponse {

    @XmlElement(name = "GetQY_ExpenseFromMidDBResult")
    protected byte[] getQYExpenseFromMidDBResult;

    /**
     * ��ȡgetQYExpenseFromMidDBResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetQYExpenseFromMidDBResult() {
        return getQYExpenseFromMidDBResult;
    }

    /**
     * ����getQYExpenseFromMidDBResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetQYExpenseFromMidDBResult(byte[] value) {
        this.getQYExpenseFromMidDBResult = value;
    }

}
