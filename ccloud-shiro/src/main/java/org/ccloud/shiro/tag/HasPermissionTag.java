package org.ccloud.shiro.tag;

public class HasPermissionTag extends BasePermissionTag {

	@Override
	protected boolean showTagBody(String permission) {
		return isPermitted(permission);
	}

}
