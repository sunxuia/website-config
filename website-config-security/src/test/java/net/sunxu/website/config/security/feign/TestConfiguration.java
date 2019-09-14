package net.sunxu.website.config.security.feign;


import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.feignclient.AppFeignClient;
import net.sunxu.website.test.helputil.authtoken.AuthTokenBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(Integer.MIN_VALUE)
@Configuration
public class TestConfiguration {

    @MockBean
    private AppFeignClient appService;

    @Autowired
    public void injectPublicKey() throws Exception {
        var resource = AuthTokenBuilder.publicKey();
        byte[] buffer = resource.getInputStream().readAllBytes();
        String publicKey = Base64.getEncoder().encodeToString(buffer);
        PublicKeyDTO ret = new PublicKeyDTO();
        ret.setPublicKey(publicKey);
        ret.setType(AuthTokenBuilder.publicKeyType());

        AtomicInteger count = new AtomicInteger(1);
        Mockito.when(appService.getPublicKey())
                .thenAnswer(i -> {
                    if (count.getAndDecrement() <= 0) {
                        return ret;
                    }
                    throw new RuntimeException("test exception");
                });
    }

    @Autowired
    public void injectServiceToken(@Value("${spring.application.name}") String applicationName) throws Exception {
        var token = new AuthTokenBuilder().name(applicationName).id(100L).exipreSeconds(100L).service(true).build();
        AtomicInteger count = new AtomicInteger(1);
        Mockito.when(appService.getServiceToken())
                .thenAnswer(i -> {
                    if (count.getAndDecrement() <= 0) {
                        return token;
                    }
                    throw new RuntimeException("test exception");
                });
    }
}
