package net.sunxu.website.config.security.rbac;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("rbac-default-disallow")
public class RbacDefaultDisallowTest extends AbstractTest {

    @Test(expected = AccessDeniedException.class)
    public void testWithoutGranted() {
        setUserWithRole("ROLE_TEST");
        rbacTestComponent.resourceC();
    }
}
