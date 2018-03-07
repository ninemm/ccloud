
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
 *         &lt;element name="SyncYX_CusShopInfoItemToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXCusShopInfoItemToMidDBResult"
})
@XmlRootElement(name = "SyncYX_CusShopInfoItemToMidDBResponse")
public class SyncYXCusShopInfoItemToMidDBResponse {

    @XmlElement(name = "SyncYX_CusShopInfoItemToMidDBResult")
    protected int syncYXCusShopInfoItemToMidDBResult;

    /**
     * ��ȡsyncYXCusShopInfoItemToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXCusShopInfoItemToMidDBResult() {
        return syncYXCusShopInfoItemToMidDBResult;
    }

    /**
     * ����syncYXCusShopInfoItemToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXCusShopInfoItemToMidDBResult(int value) {
        this.syncYXCusShopInfoItemToMidDBResult = value;
    }

}
