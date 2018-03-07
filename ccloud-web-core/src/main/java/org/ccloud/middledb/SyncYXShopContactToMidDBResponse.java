
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
 *         &lt;element name="SyncYX_ShopContactToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXShopContactToMidDBResult"
})
@XmlRootElement(name = "SyncYX_ShopContactToMidDBResponse")
public class SyncYXShopContactToMidDBResponse {

    @XmlElement(name = "SyncYX_ShopContactToMidDBResult")
    protected int syncYXShopContactToMidDBResult;

    /**
     * ��ȡsyncYXShopContactToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXShopContactToMidDBResult() {
        return syncYXShopContactToMidDBResult;
    }

    /**
     * ����syncYXShopContactToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXShopContactToMidDBResult(int value) {
        this.syncYXShopContactToMidDBResult = value;
    }

}
