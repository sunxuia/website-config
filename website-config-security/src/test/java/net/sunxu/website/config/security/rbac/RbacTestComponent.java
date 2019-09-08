package net.sunxu.website.config.security.rbac;

import net.sunxu.website.config.security.rbac.annotation.AccessResource;
import org.springframework.stereotype.Component;

@Component
@AccessResource("classLevel")
public class RbacTestComponent {

    public void noAccessResource() {}

    @AccessResource("A")
    @AccessResource("B")
    public void resourceAB() {}

    @AccessResource("C")
    public void resourceC() {}

    @AccessResource("D.D2")
    public void resourceD2() {}

    @AccessResource("E")
    public void resourceE() {}
}
