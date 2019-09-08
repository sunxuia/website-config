package net.sunxu.website.config.security.rbac;

import net.sunxu.website.config.security.rbac.permission.PermissionService;
import net.sunxu.website.config.security.rbac.permission.PermissionServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RbacAutoConfiguration {

    @Bean
    public RbacAspect rbacAspect() {
        return new RbacAspect();
    }

    @Bean
    public PermissionService resourceService() {
        return new PermissionServiceImpl();
    }

    @Bean
    @ConfigurationProperties("website.security.rbac")
    public RbacProperties rbacProperties() {
        return new RbacProperties();
    }
}
