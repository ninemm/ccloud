package org.ccloud.shiro;

import java.util.List;
import java.util.Map;

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
	    Map<String, List<String>> map = RoleQuery.me().getPermissions(userInPrincipal.getId());
	    List<String> permission = OperationQuery.me().getPermissionsByUser(userInPrincipal);
	    SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
	    info.addRoles(map.get("roleCodes"));
	    info.addStringPermissions(permission);
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
	    	//String password = EncryptUtils.encryptPassword(new String(authcToken.getPassword()), user.getSalt());
	    	//String password = user.getPassword();
	        if(!user.getPassword().equals(new String(authcToken.getPassword()))){
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
    public void clearCachedAuthorizationInfo(Object principal) {
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
    
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
    	 User userInPrincipal = (User) principals.getPrimaryPrincipal();
        return userInPrincipal.getId();
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
