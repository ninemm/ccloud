/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.model.query;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.Consts;
import org.ccloud.model.PayablesDetail;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class PayablesDetailQuery extends JBaseQuery { 

	protected static final PayablesDetail DAO = new PayablesDetail();
	private static final PayablesDetailQuery QUERY = new PayablesDetailQuery();

	public static PayablesDetailQuery me() {
		return QUERY;
	}

	public PayablesDetail findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	public PayablesDetail findByObjId(String objId,String deptId,String refSn) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"' and ref_sn='"+refSn+"'";
		return DAO.findFirst(select);
	}
	
	public List<PayablesDetail> findByObjId(String objId,String deptId) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"'";
		return DAO.find(select);
	}
	
	public PayablesDetail findByRefSn(String objId,String deptId,String refSn) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"' and ref_sn ='"+refSn+"' ";
		return DAO.findFirst(select);
	}
//销售退货单应付账款详情
public Page<PayablesDetail> paginate(int pageNumber, int pageSize, String id,String dataArea) {
		
		String select = "SELECT COALESCE(t3.act_amount,0) as act_amount, t3.ref_sn, IF (sod.is_composite = 0,t3.pay_amount,so.total_amount) pay_amount,IF (sod.is_composite = 0,t3.pay_amount,so.total_amount) - COALESCE (t3.act_amount, 0) AS balance_amount, t3.object_id, d.name as ref_Name,t3.ref_type, t3.create_date, t3.biz_date ";
		StringBuilder fromBuilder = new StringBuilder(" FROM (SELECT SUM(r.act_amount) AS act_amount, t2.ref_sn, t2.pay_amount AS pay_amount, t2.pay_amount - r.act_amount AS ");
		fromBuilder.append("balance_amount, t2.object_id, t2.ref_type, t2.create_date, t2.biz_date FROM cc_payment r RIGHT JOIN (SELECT SUM(pay_amount) AS pay_amount, object_id, ref_type, create_date, biz_date, ref_sn FROM `cc_payables_detail` c ");
		fromBuilder.append(" WHERE c.object_id ='"+id+"'");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "data_area", dataArea, params, false);
		fromBuilder.append(" GROUP BY c.ref_sn  ORDER BY c.create_date DESC ) t2 ON r.ref_sn = t2.ref_sn GROUP BY t2.ref_sn ) t3 ");
		fromBuilder.append(" LEFT JOIN cc_sales_refund_instock cri on cri.instock_sn = t3.ref_sn ");
		fromBuilder.append(" LEFT JOIN cc_sales_outstock sok on sok.id = cri.outstock_id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order_join_outstock sojo ON sojo.outstock_id=sok.id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order so ON so.id=sojo.order_id ");
		fromBuilder.append(" LEFT JOIN cc_sales_order_detail sod ON sod.order_id=so.id ");
		fromBuilder.append(" INNER JOIN dict d on t3.ref_type = d.`value`  GROUP BY t3.ref_sn ORDER BY t3.create_date desc ");
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

//直营商采购入库应付账款详情
public Page<PayablesDetail> paginateSeller(int pageNumber, int pageSize, String id,String dataArea) {
	
	String select = "SELECT COALESCE(SUM(t3.act_amount),0) as act_amount, t3.ref_sn, t3.pay_amount,(t3.pay_amount) - COALESCE(SUM(t3.act_amount),0) as balance_amount, t3.object_id, d.name as ref_Name,t3.ref_type, t3.create_date, t3.biz_date ";
	StringBuilder fromBuilder = new StringBuilder(" FROM (SELECT r.act_amount AS act_amount, t2.ref_sn, t2.pay_amount AS pay_amount, t2.pay_amount - r.act_amount AS ");
	fromBuilder.append("balance_amount, t2.object_id, t2.ref_type, t2.create_date, t2.biz_date FROM cc_payment r RIGHT JOIN (SELECT SUM(pay_amount) AS pay_amount, object_id, ref_type, create_date, biz_date, ref_sn FROM `cc_payables_detail` c ");
	fromBuilder.append(" WHERE c.object_id ='"+id+"'");
	LinkedList<Object> params = new LinkedList<Object>();
	appendIfNotEmptyWithLike(fromBuilder, "data_area", dataArea, params, false);
	fromBuilder.append(" GROUP BY c.ref_sn  ORDER BY c.create_date DESC ) t2 ON r.ref_sn = t2.ref_sn ) t3  INNER JOIN dict d on t3.ref_type = d.`value`  GROUP BY t3.ref_sn ORDER BY t3.create_date desc ");
	if (params.isEmpty())
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

	return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
}
	
	public Page<PayablesDetail> findByObjId(int pageNumber, int pageSize,String objId,String dataArea, String orderby) {
		String select = "select cc_d.*,cc_s.`status` ref_status ";
		StringBuilder fromBuilder = new StringBuilder("from cc_payables_detail cc_d inner join `cc_sales_refund_instock` cc_s on cc_d.ref_sn = cc_s.instock_sn and cc_s.payment_type = 0 ");
		fromBuilder.append("where cc_d.object_id = '"+objId+"' ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "cc_d.data_area", dataArea, params, false);
		fromBuilder.append("order by " + orderby);
		if (params.isEmpty())
			return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());

		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString(), params.toArray());
	}

	public int batchDelete(String... ids) {
		if (ids != null && ids.length > 0) {
			int deleteCount = 0;
			for (int i = 0; i < ids.length; i++) {
				if (DAO.deleteById(ids[i])) {
					++deleteCount;
				}
			}
			return deleteCount;
		}
		return 0;
	}

	public boolean insert(Record record, String customerId, String refundSn, Date date) {
		PayablesDetail payablesDetail = new PayablesDetail();
		payablesDetail.setId(StrKit.getRandomUUID());
		payablesDetail.setObjectId(customerId);
		payablesDetail.setObjectType(Consts.RECEIVABLES_OBJECT_TYPE_CUSTOMER);
		payablesDetail.setPayAmount(record.getBigDecimal("reject_amount"));
		payablesDetail.setActAmount(new BigDecimal(0));
		payablesDetail.setBalanceAmount(record.getBigDecimal("reject_amount"));
		payablesDetail.setRefSn(refundSn);
		payablesDetail.setRefType(Consts.BIZ_TYPE_SALES_REFUND_INSTOCK);
		payablesDetail.setDeptId(record.getStr("dept_id"));
		payablesDetail.setDataArea(record.getStr("data_area"));
		payablesDetail.setBizDate(date);
		payablesDetail.setCreateDate(date);
		return payablesDetail.save();
	}

	public PayablesDetail findByObjIdAndDeptId(String objId,String deptId) {
		String select = " select * from cc_payables_detail where object_id= '"+objId+"' and dept_id='"+deptId+"'";
		return DAO.findFirst(select);
	}
}
