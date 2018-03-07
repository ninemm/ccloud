
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
 *         &lt;element name="SyncYX_EntryCostGiftInfoToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXEntryCostGiftInfoToMidDBResult"
})
@XmlRootElement(name = "SyncYX_EntryCostGiftInfoToMidDBResponse")
public class SyncYXEntryCostGiftInfoToMidDBResponse {

    @XmlElement(name = "SyncYX_EntryCostGiftInfoToMidDBResult")
    protected int syncYXEntryCostGiftInfoToMidDBResult;

    /**
     * ��ȡsyncYXEntryCostGiftInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXEntryCostGiftInfoToMidDBResult() {
        return syncYXEntryCostGiftInfoToMidDBResult;
    }

    /**
     * ����syncYXEntryCostGiftInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXEntryCostGiftInfoToMidDBResult(int value) {
        this.syncYXEntryCostGiftInfoToMidDBResult = value;
    }

}
