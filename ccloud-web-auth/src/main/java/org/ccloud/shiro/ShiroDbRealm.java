package org.ccloud.shiro;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.ccloud.model.User;
import org.ccloud.model.query.OperationQuery;
import org.ccloud.model.query.RoleQuery;
import org.ccloud.model.query.UserQuery;
import org.ccloud.utils.EncryptUtils;

public class ShiroDbRealm extends AuthorizingRealm {
	
	public ShiroDbRealm(){
        setAuthenticationTokenClass(CaptchaUsernamePasswordToken.class);
    }
	
	/**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	    User userInPrincipal = (User) principals.getPrimaryPrincipal();
	    //根据用户获取权限
	    List<String> roles = RoleQuery.me().getPermissions(userInPrincipal.getGroupId());
	    List<String> stringPermissions = OperationQuery.me().getPermissionsByRole(roles); //通过角色获取权限
	    List<String> stringPermissions1 = OperationQuery.me().getPermissionsByStation(userInPrincipal.getStationId()); //通过岗位获取权限
	    stringPermissions.addAll(stringPermissions1);
	    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
	    info.addRoles(roles);
	    info.addStringPermissions(stringPermissions);
	    return info;
	}

	/**
     * 认证回调函数,登录时调用.
     */ 
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		CaptchaUsernamePasswordToken authcToken = (CaptchaUsernamePasswordToken) token;
	    User user = UserQuery.me().findUserByUsername(authcToken.getUsername());
	    if (user != null) {
	    	String password = EncryptUtils.encryptPassword(new String(authcToken.getPassword()), user.getSalt());
	        if(!user.getPassword().equals(password)){
	            throw new AuthenticationException("密码错误");
	        }
	        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
	    } else {
	        throw new AuthenticationException("用户不存在");
	    }
	}

	/**
     * 更新用户授权信息缓存.
     */
    public void clearCachedAuthorizationInfo(String principal) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清除所有用户授权信息缓存.
     */
    public void clearAllCachedAuthorizationInfo() {
        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            for (Object key : cache.keys()) {
                cache.remove(key);
            }
        }
    }
    
    /** 
     * 认证密码匹配调用方法 
     */  
    @Override  
    protected void assertCredentialsMatch(AuthenticationToken authcToken,  
            AuthenticationInfo info) throws AuthenticationException {
    	return;
    }      
}
