package net.sunxu.website.config.security.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("test-service")
public interface TestService {

    @RequestMapping("/")
    void test();
}
