package org.ccloud.shiro.tag;

import org.apache.shiro.subject.Subject;

public class HasAnyRolesTag extends BaseRoleTag {

	private static final String ROLE_NAMES_DELIMETER = ",";
	
	@Override
	protected boolean showTagBody(String roleNames) {

		boolean hasAnyRole = false;
		Subject subject = getSubject();
		
		if (subject != null) {
			for (String role : roleNames.split(ROLE_NAMES_DELIMETER)) {
				if (subject.hasRole(role.trim())) {
					hasAnyRole = true;
					break;
				}
			}
		}
		
		return hasAnyRole;
	}

}
