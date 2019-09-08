package net.sunxu.website.config.security.rbac.permission;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * 用于动态载入角色和资源的服务
 */
public interface PermissionService {

    /**
     * 根据角色名和资源名称判断是否有权访问对象.
     *
     * @param resourceName 资源的名称
     * @param authorities 当前用户的角色
     */
    boolean isPermitted(String resourceName, Collection<? extends GrantedAuthority> authorities);

}
