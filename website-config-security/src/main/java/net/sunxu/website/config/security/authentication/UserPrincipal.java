package net.sunxu.website.config.security.authentication;

import java.security.Principal;
import java.util.Collection;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserPrincipal implements Principal {

    private Long id;

    private String name;

    private Collection<String> roles;

}
