
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
 *         &lt;element name="SyncYX_ShowTypeIDToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXShowTypeIDToMidDBResult"
})
@XmlRootElement(name = "SyncYX_ShowTypeIDToMidDBResponse")
public class SyncYXShowTypeIDToMidDBResponse {

    @XmlElement(name = "SyncYX_ShowTypeIDToMidDBResult")
    protected int syncYXShowTypeIDToMidDBResult;

    /**
     * ��ȡsyncYXShowTypeIDToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXShowTypeIDToMidDBResult() {
        return syncYXShowTypeIDToMidDBResult;
    }

    /**
     * ����syncYXShowTypeIDToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXShowTypeIDToMidDBResult(int value) {
        this.syncYXShowTypeIDToMidDBResult = value;
    }

}
