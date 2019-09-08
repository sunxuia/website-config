package net.sunxu.website.config.security.rbac;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("rbac")
public class RbacTest extends AbstractTest {

    @Test
    public void testWithGranted() {
        setUserWithRole("ROLE_ADMIN");
        rbacTestComponent.noAccessResource();
    }

    @Test(expected = AccessDeniedException.class)
    public void testWithoutGranted() {
        setUserWithRole("ROLE_TEST");
        rbacTestComponent.resourceAB();
    }

    @Test(expected = AccessDeniedException.class)
    public void testNoRolesAllowed() {
        setUserWithRole("ROLE_ADMIN");
        rbacTestComponent.resourceD2();
    }

    @Test(expected = AccessDeniedException.class)
    public void testRoleReject() {
        setUserWithRole("ROLE_ADMIN");
        rbacTestComponent.resourceD2();
    }
}
