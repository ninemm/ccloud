
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
 *         &lt;element name="SyncYX_BasicCommodityTypeInfoToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXBasicCommodityTypeInfoToMidDBResult"
})
@XmlRootElement(name = "SyncYX_BasicCommodityTypeInfoToMidDBResponse")
public class SyncYXBasicCommodityTypeInfoToMidDBResponse {

    @XmlElement(name = "SyncYX_BasicCommodityTypeInfoToMidDBResult")
    protected int syncYXBasicCommodityTypeInfoToMidDBResult;

    /**
     * ��ȡsyncYXBasicCommodityTypeInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXBasicCommodityTypeInfoToMidDBResult() {
        return syncYXBasicCommodityTypeInfoToMidDBResult;
    }

    /**
     * ����syncYXBasicCommodityTypeInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXBasicCommodityTypeInfoToMidDBResult(int value) {
        this.syncYXBasicCommodityTypeInfoToMidDBResult = value;
    }

}
