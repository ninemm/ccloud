package org.ccloud.shiro.tag;

public class LacksRoleTag extends BaseRoleTag {

	@Override
	protected boolean showTagBody(String roleName) {
		boolean hasRole = getSubject() != null && getSubject().hasRole(roleName);
		return !hasRole;
	}

}
