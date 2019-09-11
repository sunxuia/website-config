package net.sunxu.website.config.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.config.feignclient.AppProperties;
import net.sunxu.website.config.feignclient.AppServiceAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtParser parser = Jwts.parser();

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource =
            new WebAuthenticationDetailsSource();

    @Autowired
    private AppServiceAdaptor appService;

    @Autowired
    private AppProperties properties;

    @PostConstruct
    public void initialPublicKey() {
        PublicKeyDTO publicKey = null;
        try {
            publicKey = appService.getPublicKey();
            parser.setSigningKey(publicKey.readPublicKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var authHeaders = request.getHeaders(AuthTokenDefine.TOKEN_HEADER_NAME);
        if (authHeaders != null) {
            for (var iterator = authHeaders.asIterator(); iterator.hasNext(); ) {
                String authHeader = iterator.next();
                if (authHeader != null && authHeader.startsWith(AuthTokenDefine.TOKEN_PREFIX)) {
                    String authToken = authHeader.substring(AuthTokenDefine.TOKEN_PREFIX.length());
                    var claims = parseClaims(authToken);
                    if (claims != null && shouldAuthentication(claims)) {
                        setAuthentication(claims, authToken, request);
                        break;
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    private Claims parseClaims(String token) {
        try {
            return parser.parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException err) {
            log.warn("token expired: " + token);
            if (log.isDebugEnabled()) {
                log.debug(String.format("caused by exception %s: %s", err.getClass().getName(), err.getMessage()));
                err.printStackTrace();
            }
        } catch (Exception err) {
            if (log.isDebugEnabled()) {
                log.debug(String.format("parse claims error %s: %s,\ntoken is: %s",
                        err.getClass().getName(), err.getMessage(), token));
                err.printStackTrace();
            }
        }
        return null;
    }

    protected boolean shouldAuthentication(Claims claims) {
        if (properties.isExamineServiceOnly()) {
            if (!claims.get("service", Boolean.class)) {
                return false;
            }
        }
        return true;
    }

    protected void setAuthentication(Claims claims, String credentials, HttpServletRequest request) {
        var principal = convertToUserprincipal(claims);
        Set<GrantedAuthority> auths = principal.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal, credentials, auths);
        authentication.setDetails(authenticationDetailsSource.buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (log.isDebugEnabled()) {
            log.info(String.format("Authentication success: id: %d, name: %s, token: %s",
                    principal.getId(), principal.getName(), credentials));
        }
    }

    @SuppressWarnings("unchecked")
    private UserPrincipal convertToUserprincipal(Claims claims) {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(claims.get("id", Long.class));
        principal.setName(claims.get("name", String.class));
        var roles = (List<String>) claims.get("roles", List.class);
        if (roles == null) {
            principal.setRoles(Collections.emptySet());
        } else {
            principal.setRoles(Set.copyOf(roles));
        }
        return principal;
    }
}
