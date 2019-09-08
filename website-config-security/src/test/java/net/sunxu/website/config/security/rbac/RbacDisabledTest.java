package net.sunxu.website.config.security.rbac;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("rbac-disable")
public class RbacDisabledTest extends AbstractTest {

    @Test
    public void testWithoutGranted() {
        setUserWithRole("ROLE_TEST");
        rbacTestComponent.resourceC();
    }
}
