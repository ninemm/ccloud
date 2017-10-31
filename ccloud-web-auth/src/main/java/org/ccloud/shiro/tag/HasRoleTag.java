package org.ccloud.shiro.tag;

public class HasRoleTag extends BaseRoleTag {

	@Override
	protected boolean showTagBody(String roleNames) {
		return getSubject() != null && getSubject().hasRole(roleNames);
	}

}
