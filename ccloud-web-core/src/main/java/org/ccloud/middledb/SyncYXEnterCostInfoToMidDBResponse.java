
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
 *         &lt;element name="SyncYX_EnterCostInfoToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXEnterCostInfoToMidDBResult"
})
@XmlRootElement(name = "SyncYX_EnterCostInfoToMidDBResponse")
public class SyncYXEnterCostInfoToMidDBResponse {

    @XmlElement(name = "SyncYX_EnterCostInfoToMidDBResult")
    protected int syncYXEnterCostInfoToMidDBResult;

    /**
     * ��ȡsyncYXEnterCostInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXEnterCostInfoToMidDBResult() {
        return syncYXEnterCostInfoToMidDBResult;
    }

    /**
     * ����syncYXEnterCostInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXEnterCostInfoToMidDBResult(int value) {
        this.syncYXEnterCostInfoToMidDBResult = value;
    }

}
