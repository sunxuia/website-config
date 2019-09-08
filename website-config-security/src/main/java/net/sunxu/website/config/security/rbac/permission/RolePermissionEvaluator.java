package net.sunxu.website.config.security.rbac.permission;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;

class RolePermissionEvaluator implements PermissionEvaluator {

    private final String DEFAULT_ROLE_REFIX = "ROLE_";

    private Set<String> acceptRoles = new HashSet<>();

    private Set<String> rejectRoles = new HashSet<>();


    RolePermissionEvaluator(String[] roleNames) {
        for (String roleName : roleNames) {
            resolveRoleNames(roleName);
        }
    }

    RolePermissionEvaluator(String roleNames) {
        resolveRoleNames(roleNames);
    }

    private void resolveRoleNames(String roleNames) {
        String[] roleNameArray = roleNames.split(",");
        for (String roleName : roleNameArray) {
            roleName = roleName.trim().toUpperCase();
            if (!roleName.isEmpty()) {
                var roles = acceptRoles;
                if (roleName.startsWith("!")) {
                    roles = rejectRoles;
                    roleName = roleName.substring(1).trim();
                }
                if (!roleName.startsWith(DEFAULT_ROLE_REFIX)) {
                    roleName = DEFAULT_ROLE_REFIX + roleName;
                }
                roles.add(roleName);
            }
        }
    }

    @Override
    public boolean isPermitted(String[] resourceNames, int index, Collection<? extends GrantedAuthority> authorities) {
        if (!rejectRoles.isEmpty()) {
            for (var authority : authorities) {
                if (rejectRoles.contains(authority.getAuthority())) {
                    return false;
                }
            }
        }
        if (!acceptRoles.isEmpty()) {
            for (var authority : authorities) {
                if (acceptRoles.contains(authority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }
}
