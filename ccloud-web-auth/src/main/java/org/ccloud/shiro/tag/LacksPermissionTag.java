package org.ccloud.shiro.tag;

public class LacksPermissionTag extends BasePermissionTag {

	@Override
	protected boolean showTagBody(String permission) {
		return !isPermitted(permission);
	}

}
