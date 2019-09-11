package net.sunxu.website.config.feignclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Configuration
public class FeignAutoConfiguration {

    private static final String AUTHORIZATION = "Authorization";

    private final long RE_GET_INTERVAL = 5 * 60 * 1000L;

    private AtomicReference<String> tokenHolder = new AtomicReference<>();

    private AtomicLong expireHolder = new AtomicLong(0L);

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public AppServiceAdaptor appServiceAdaptor() {
        return new AppServiceAdaptor(appProperties());
    }

    @Bean
    @ConfigurationProperties("website.app")
    public AppProperties appProperties() {
        return new AppProperties();
    }

    @Bean
    @LoadBalanced
    public RestTemplate ribbonTemplate() {
        var appProperties = appProperties();
        String str = appProperties.getId() + ":" + appProperties.getPassword();
        String basicToken = "basic " + Base64.getEncoder().encodeToString(str.getBytes());

        var restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            if (request.getURI().getHost().equals(AppProperties.APP_SERVICE)) {
                headers.add(AUTHORIZATION, basicToken);
            } else {
                checkRefresh();
                headers.add(AUTHORIZATION, tokenHolder.get());
            }
            return execution.execute(request, body);
        }));
        return restTemplate;
    }

    private void checkRefresh() {
        if (expireHolder.get() < System.currentTimeMillis() + RE_GET_INTERVAL) {
            refreshToken();
        }
    }

    private boolean refreshTokenInvoked = false;

    @PostConstruct
    public synchronized void refreshToken() {
        if (!refreshTokenInvoked) {
            refreshTokenInvoked = true;
            try {
                if (expireHolder.get() > System.currentTimeMillis() + RE_GET_INTERVAL) {
                    return;
                }

                var token = appServiceAdaptor().getServiceToken();
                log.info("refresh service token : " + token);

                String payload = new String(Base64.getDecoder().decode(token.split("\\.")[1]));
                long exp = Long.parseLong("" + objectMapper.readValue(payload, Map.class).get("exp"));
                expireHolder.set(exp * 1000);
                tokenHolder.set("Bearer " + token);
            } catch (Exception err) {
                log.warn("error in refresh service token : " + err);
                if (tokenHolder.get() == null) {
                    throw new RuntimeException(err);
                }
            }
            refreshTokenInvoked = false;
        }
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            checkRefresh();
            if (!requestTemplate.headers().containsKey(AUTHORIZATION)) {
                requestTemplate.header(AUTHORIZATION, tokenHolder.get());
            }
        };
    }

    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new CustomEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
