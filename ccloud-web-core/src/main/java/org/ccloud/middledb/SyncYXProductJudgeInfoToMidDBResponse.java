
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
 *         &lt;element name="SyncYX_ProductJudgeInfoToMidDBResult" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "syncYXProductJudgeInfoToMidDBResult"
})
@XmlRootElement(name = "SyncYX_ProductJudgeInfoToMidDBResponse")
public class SyncYXProductJudgeInfoToMidDBResponse {

    @XmlElement(name = "SyncYX_ProductJudgeInfoToMidDBResult")
    protected int syncYXProductJudgeInfoToMidDBResult;

    /**
     * ��ȡsyncYXProductJudgeInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public int getSyncYXProductJudgeInfoToMidDBResult() {
        return syncYXProductJudgeInfoToMidDBResult;
    }

    /**
     * ����syncYXProductJudgeInfoToMidDBResult���Ե�ֵ��
     * 
     */
    public void setSyncYXProductJudgeInfoToMidDBResult(int value) {
        this.syncYXProductJudgeInfoToMidDBResult = value;
    }

}
