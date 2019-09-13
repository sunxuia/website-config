package net.sunxu.website.config.feignclient;

import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import net.sunxu.website.config.feignclient.exceptionhandler.FeignErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class FeignAutoConfiguration {

    @Primary
    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new CustomEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return new FeignErrorDecoder();
    }
}
