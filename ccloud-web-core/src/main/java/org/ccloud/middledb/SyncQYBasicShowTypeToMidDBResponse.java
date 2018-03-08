
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
 *         &lt;element name="SyncQYBasicShowTypeToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncQYBasicShowTypeToMidDBResult"
})
@XmlRootElement(name = "SyncQYBasicShowTypeToMidDBResponse")
public class SyncQYBasicShowTypeToMidDBResponse {

    @XmlElement(name = "SyncQYBasicShowTypeToMidDBResult")
    protected int syncQYBasicShowTypeToMidDBResult;

    /**
     * ��ȡsyncQYBasicShowTypeToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncQYBasicShowTypeToMidDBResult() {
        return syncQYBasicShowTypeToMidDBResult;
    }

    /**
     * ����syncQYBasicShowTypeToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncQYBasicShowTypeToMidDBResult(int value) {
        this.syncQYBasicShowTypeToMidDBResult = value;
    }

}
