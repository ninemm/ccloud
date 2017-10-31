## Shiro 的Freemarker标签使用实例

1. 已经登录判断

    <@shiro.authenticated>
        <li><a href="#"><@shiro.principal name="full_name"></a><li>
    </@shiro.authenticated

2. 没有登录判断

    <@shiro.notAuthenticated>
        <li><a href="/">登录</a></li>
    </@shiro.notAuthenticated>

3. 判断角色

    <@shiro.hasRole name="ROLE_ADMIN">
    我是admin
    </@shiro.hasRole>

4. 判断权限

    <@shiro.hasPermission name="P_ORDER_CONTROL">
    
    </@shiro.hasPermission>

