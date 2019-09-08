package net.sunxu.website.config.security.rbac;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.sunxu.website.config.security.authentication.UserPrincipal;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractTest {

    @Autowired
    protected RbacTestComponent rbacTestComponent;

    protected void setUserWithRole(String... roleNames) {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(100L);
        principal.setName("unit-test");
        principal.setRoles(List.of(roleNames));

        Set<GrantedAuthority> auths = principal.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, "", auths);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
