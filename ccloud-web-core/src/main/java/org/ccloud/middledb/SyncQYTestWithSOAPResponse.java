
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
 *         &lt;element name="SyncQYTestWithSOAPResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "syncQYTestWithSOAPResult"
})
@XmlRootElement(name = "SyncQYTestWithSOAPResponse")
public class SyncQYTestWithSOAPResponse {

    @XmlElement(name = "SyncQYTestWithSOAPResult")
    protected String syncQYTestWithSOAPResult;

    /**
     * ��ȡsyncQYTestWithSOAPResult���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSyncQYTestWithSOAPResult() {
        return syncQYTestWithSOAPResult;
    }

    /**
     * ����syncQYTestWithSOAPResult���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSyncQYTestWithSOAPResult(String value) {
        this.syncQYTestWithSOAPResult = value;
    }

}
