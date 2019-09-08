package net.sunxu.website.config.security.rbac.permission;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

interface PermissionEvaluator {

    /**
     * 判断资源是否可达
     *
     * @param resourceNames 资源按照"." 分出来的数组
     * @param index 资源目前的层级
     * @param authorities 当前用户的权限
     * @return 是否可以调用这个方法
     */
    boolean isPermitted(String[] resourceNames, int index, Collection<? extends GrantedAuthority> authorities);

}
