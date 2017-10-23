package org.ccloud.listener;

import org.ccloud.message.Actions;
import org.ccloud.message.Message;
import org.ccloud.message.MessageListener;
import org.ccloud.message.annotation.Listener;
import org.ccloud.model.GoodsCategory;
import org.ccloud.model.core.JModel;
import org.ccloud.model.query.GoodsCategoryQuery;

@Listener(action = {Actions.CATEGORY_ADD, Actions.CATEGORY_UPDATE, Actions.CATEGORY_DELETE})
public class GoodsCategoryChangeListener implements MessageListener {
	
	@Override
	public void onMessage(Message message) {
		Object temp = message.getData();
		
		if (temp != null && temp instanceof JModel) {
			GoodsCategory category = (GoodsCategory) temp;
			GoodsCategory parentCategory = GoodsCategoryQuery.me().findById(category.getParentId());
			Integer childNum = GoodsCategoryQuery.me().childNumById(category.getParentId());
			if (childNum > 0) {
				if (parentCategory.getIsParent() == 0) {
					parentCategory.setIsParent(1);
					parentCategory.update();
				}
			} else {
				if (parentCategory.getIsParent() > 0) {
					parentCategory.setIsParent(0);
					parentCategory.update();
				}
			}
		}
	}
}
