package net.sunxu.website.config.security.rbac.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import net.sunxu.website.config.security.rbac.RbacProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;

@Log4j2
public class PermissionServiceImpl implements PermissionService {

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Lock readLock = readWriteLock.readLock();

    private Lock writeLock = readWriteLock.writeLock();

    private PermissionEvaluator permissionEvaluator;

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void setPermissionsWhenRefresh() {
        refreshPermission();
    }

    @PostConstruct
    public void refreshPermission() {
        var properties = applicationContext.getBean(RbacProperties.class);
        writeLock.lock();
        try {
            if (properties.isEnabled()) {
                permissionEvaluator = new CompositePermissionEvaluator(properties.getPermissions(),
                        properties.isDefaultAllow());
            } else {
                permissionEvaluator = new CompositePermissionEvaluator(Collections.emptyMap(), true);
            }
        } catch (Exception err) {
            log.error("error while parse permission evaluator : " + err);
        }
        writeLock.unlock();
    }

    @Override
    public boolean isPermitted(String resourceName, Collection<? extends GrantedAuthority> authorities) {
        String[] resources = resourceName.split("\\.");
        readLock.lock();
        boolean res = permissionEvaluator.isPermitted(resources, 0, authorities);
        readLock.unlock();
        return res;
    }
}
