
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
 *         &lt;element name="GetQY_BasicFlowTypeFromMidDBResult" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
    "getQYBasicFlowTypeFromMidDBResult"
})
@XmlRootElement(name = "GetQY_BasicFlowTypeFromMidDBResponse")
public class GetQYBasicFlowTypeFromMidDBResponse {

    @XmlElement(name = "GetQY_BasicFlowTypeFromMidDBResult")
    protected byte[] getQYBasicFlowTypeFromMidDBResult;

    /**
     * ��ȡgetQYBasicFlowTypeFromMidDBResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getGetQYBasicFlowTypeFromMidDBResult() {
        return getQYBasicFlowTypeFromMidDBResult;
    }

    /**
     * ����getQYBasicFlowTypeFromMidDBResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setGetQYBasicFlowTypeFromMidDBResult(byte[] value) {
        this.getQYBasicFlowTypeFromMidDBResult = value;
    }

}
