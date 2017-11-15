package org.ccloud.workflow.query;

import org.ccloud.workflow.model.ActReModel;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.IDataLoader;

/**
 * Generated by JFinal.
 */
public class ActReModelQuery {

	protected static final ActReModel DAO = new ActReModel();
	private static final ActReModelQuery QUERY = new ActReModelQuery();
	
	public static ActReModelQuery me() {
		return QUERY;
	}
	
	public ActReModel findById(final String id) {
		return DAO.getCache(id, new IDataLoader() {
			@Override
			public Object load() {
				return DAO.findById(id);
			}
		});
	}
	
	/***
	 * 查询分页
	 * @param pageNumber
	 * @param pagesize
	 * @return
	 */
	public Page<ActReModel> getModelPage(Integer pageNumber , Integer pageSize) {
		
		String select = "select *";
		StringBuilder fromBuilder = new StringBuilder("from `act_re_model` ");
		fromBuilder.append("ORDER BY VERSION_ DESC");
		
		return DAO.paginate(pageNumber, pageSize, select, fromBuilder.toString());
		//return DAO.paginate(curr, pagesize, true, "select * ", " FROM (select *  from act_re_model ORDER BY VERSION_ DESC) a group by KEY_");
	}
}