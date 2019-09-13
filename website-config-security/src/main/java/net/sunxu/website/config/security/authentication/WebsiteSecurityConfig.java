package net.sunxu.website.config.security.authentication;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import net.sunxu.website.config.feignclient.AppServiceAdaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class WebsiteSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtAuthenticationTokenFilter(), BasicAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }

    @Bean
    public JwtParser serviceJwtParser(AppServiceAdaptor appService) {
        JwtParser parser = Jwts.parser();
        try {
            var publicKey = appService.getPublicKey();
            parser.setSigningKey(publicKey.readPublicKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return parser;
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }
}
