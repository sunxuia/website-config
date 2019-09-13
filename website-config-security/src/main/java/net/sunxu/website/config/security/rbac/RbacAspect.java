package net.sunxu.website.config.security.rbac;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.sunxu.website.config.security.authentication.UserPrincipal;
import net.sunxu.website.config.security.rbac.annotation.AccessResource;
import net.sunxu.website.config.security.rbac.permission.PermissionService;
import net.sunxu.website.help.webutil.RequestHelpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 进行权限判断的拦截类
 */
@Aspect
public class RbacAspect {

    private Logger logger = LoggerFactory.getLogger(RbacAspect.class);

    private final List<GrantedAuthority> anonymousRoles = List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

    @Autowired
    private PermissionService permissionService;

    @Before("(@within(net.sunxu.website.config.security.rbac.annotation.AccessResource)" +
            " || @annotation(net.sunxu.website.config.security.rbac.annotation.AccessResource))" +
            " && target(target)")
    private void accessAuthorization(JoinPoint point, Object target) {
        boolean debug = logger.isDebugEnabled();
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Class<?> targetClass = targetMethod.getDeclaringClass();

        Authentication authentication = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .orElse(null);
        Object principal = Optional.ofNullable(authentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .orElse(null);
        Collection<? extends GrantedAuthority> roles;
        var request = RequestHelpUtils.getRequest();
        if (authentication == null || authentication.getAuthorities() == null
                || authentication.getAuthorities().isEmpty()) {
            if (debug) {
                logger.info("user [{}] trying to access method [{}#{}]",
                        RequestHelpUtils.getIpAddress(request), targetClass, targetMethod.getName());
            }
            roles = anonymousRoles;
        } else {
            if (principal instanceof UserPrincipal) {
                UserPrincipal user = (UserPrincipal) principal;
                if (debug) {
                    logger.info("user [{}][{}] trying to access method [{}#{}]",
                            user.getUserName(), RequestHelpUtils.getIpAddress(request), targetClass,
                            targetMethod.getName());
                }
            } else {
                if (debug) {
                    logger.info("user [{}][{}] trying to access method [{}#{}]", authentication.getName(),
                            RequestHelpUtils.getIpAddress(request), targetClass, targetMethod.getName());
                }
            }
            roles = authentication.getAuthorities();
        }

        AccessResource[] classResources = targetClass.getAnnotationsByType(AccessResource.class);
        AccessResource[] methodResources = targetMethod.getAnnotationsByType(AccessResource.class);
        boolean hasAuthorization = false;
        if (methodResources.length == 0) {
            for (int i = 0; i < classResources.length && !hasAuthorization; i++) {
                hasAuthorization = permissionService.isPermitted(classResources[i].value(), roles);
            }
        } else if (classResources.length == 0) {
            for (int i = 0; i < methodResources.length && !hasAuthorization; i++) {
                hasAuthorization = permissionService.isPermitted(methodResources[i].value(), roles);
            }
        } else {
            for (int i = 0; i < classResources.length && !hasAuthorization; i++) {
                for (int j = 0; j < methodResources.length && !hasAuthorization; j++) {
                    hasAuthorization = permissionService.isPermitted(
                            classResources[i].value() + "." + methodResources[j].value(), roles);
                }
            }
        }
        if (!hasAuthorization) {
            throw new AccessDeniedException("Resource Not Allowed");
        }
    }
}
