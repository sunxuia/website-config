package net.sunxu.website.config.feignclient;

import java.util.function.Supplier;
import lombok.extern.log4j.Log4j2;
import net.sunxu.website.app.dto.PublicKeyDTO;
import net.sunxu.website.app.feignclient.AppFeignClient;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class AppServiceAdaptor {

    @Autowired
    private AppFeignClient appService;

    private AppProperties appProperties;

    public AppServiceAdaptor(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @SuppressWarnings("unchecked")
    public PublicKeyDTO getPublicKey() throws Exception {
        return withRetry("public key", () -> appService.getPublicKey());
    }

    private <T> T withRetry(String resourceName, Supplier<T> supplier) throws InterruptedException {
        try {
            return supplier.get();
        } catch (Exception err) {
            for (int retry = 0; retry < appProperties.getRetryTimes(); retry++) {
                log.error(String.format("Error while get %s : %s", resourceName, err));
                Thread.sleep((long) (1000 * Math.pow(2, retry)));
                try {
                    return supplier.get();
                } catch (Exception e) {
                    err = e;
                }
            }
            throw new RuntimeException(String.format("Cannot get %s after %d times tries: %s",
                    resourceName, appProperties.getRetryTimes(), err));
        }
    }

    public String getServiceToken() throws Exception {
        return withRetry("service token", () -> appService.getServiceToken());
    }
}
