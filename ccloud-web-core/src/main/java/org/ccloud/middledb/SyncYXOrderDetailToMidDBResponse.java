
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
 *         &lt;element name="SyncYX_OrderDetailToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXOrderDetailToMidDBResult"
})
@XmlRootElement(name = "SyncYX_OrderDetailToMidDBResponse")
public class SyncYXOrderDetailToMidDBResponse {

    @XmlElement(name = "SyncYX_OrderDetailToMidDBResult")
    protected int syncYXOrderDetailToMidDBResult;

    /**
     * ��ȡsyncYXOrderDetailToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXOrderDetailToMidDBResult() {
        return syncYXOrderDetailToMidDBResult;
    }

    /**
     * ����syncYXOrderDetailToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXOrderDetailToMidDBResult(int value) {
        this.syncYXOrderDetailToMidDBResult = value;
    }

}
