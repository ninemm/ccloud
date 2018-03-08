
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
 *         &lt;element name="SyncQYBasicFlowFeeRelationToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncQYBasicFlowFeeRelationToMidDBResult"
})
@XmlRootElement(name = "SyncQYBasicFlowFeeRelationToMidDBResponse")
public class SyncQYBasicFlowFeeRelationToMidDBResponse {

    @XmlElement(name = "SyncQYBasicFlowFeeRelationToMidDBResult")
    protected int syncQYBasicFlowFeeRelationToMidDBResult;

    /**
     * ��ȡsyncQYBasicFlowFeeRelationToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncQYBasicFlowFeeRelationToMidDBResult() {
        return syncQYBasicFlowFeeRelationToMidDBResult;
    }

    /**
     * ����syncQYBasicFlowFeeRelationToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncQYBasicFlowFeeRelationToMidDBResult(int value) {
        this.syncQYBasicFlowFeeRelationToMidDBResult = value;
    }

}
