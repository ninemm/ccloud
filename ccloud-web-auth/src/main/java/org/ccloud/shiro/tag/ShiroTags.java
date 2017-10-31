package org.ccloud.shiro.tag;

import freemarker.template.SimpleHash;

/**
 * 
 * Declare a shared variable called "shiro", and assign it to an instance of the ShiroTags class.
 * 
 * cfg.setSharedVariable("shiro", new ShiroTags());
 * 
 * You should then be able to use the tags in your Freemarker templates.
 * 
 * <@shiro.guest>Hello guest!</@shiro.guest>
 * 
 * @author eric
 *
 */

public class ShiroTags extends SimpleHash {

	private static final long serialVersionUID = 1L;
	
	public static final String TAG_NAME = "shiro";

	@SuppressWarnings("deprecation")
	public ShiroTags() {
		
		put("authenticated", new AuthenticatedTag());
		put("guest", new GuestTag());
		put("hasAnyRoles", new HasAnyRolesTag());
		put("hasPermission", new HasPermissionTag());
		
		put("hasRole", new HasRoleTag());
		put("lacksPermission", new LacksPermissionTag());
		put("lacksRole", new LacksRoleTag());
		put("notAuthenticated", new NotAuthenticatedTag());
		
		put("pricipal", new PrincipalTag());
		put("user", new UserTag());
	}
	
}
