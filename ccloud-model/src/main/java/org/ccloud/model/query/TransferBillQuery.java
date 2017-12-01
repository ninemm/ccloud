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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.ccloud.model.TransferBill;
import org.ccloud.model.TransferBillDetail;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
public class TransferBillQuery extends JBaseQuery { 

	protected static final TransferBill DAO = new TransferBill();
	private static final TransferBillQuery QUERY = new TransferBillQuery();

	public static TransferBillQuery me() {
		return QUERY;
	}

	public TransferBill findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}

	public Page<TransferBill> paginate(int pageNumber, int pageSize,String keyword, String orderby) {
		String select = "select c.id, c.transfer_bill_sn,w1.name as from_warehouse_id,w2.name as to_warehouse_id,c.biz_date,u.realname,c.status,c.create_date";
		StringBuilder fromBuilder = new StringBuilder("from `cc_transfer_bill` c ");
		fromBuilder.append("INNER JOIN cc_warehouse w1 on c.from_warehouse_id = w1.id ");
		fromBuilder.append("INNER JOIN cc_warehouse w2 on c.to_warehouse_id = w2.id ");
		fromBuilder.append("INNER JOIN `user`  u on c.input_user_id = u.id ");
		LinkedList<Object> params = new LinkedList<Object>();
		appendIfNotEmptyWithLike(fromBuilder, "transfer_bill_sn", keyword, params, true);
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
	
	
	public boolean deleteAbout(final TransferBill transferBill) {
		boolean isDelete = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				transferBill.delete();
		        try {
		        	List<TransferBillDetail> list = TransferBillDetailQuery.me().deleteByTransferBillId(transferBill.getId());
			        for (TransferBillDetail transferBillDetail : list) {
			        	transferBillDetail.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
                   return false;
				}
				return true;
			}
		});		      
		return isDelete;
	}
	

	  public List<TransferBill> findByBillSn(String date){
			return DAO.doFind("biz_date = ?", date);
	  }
	
}