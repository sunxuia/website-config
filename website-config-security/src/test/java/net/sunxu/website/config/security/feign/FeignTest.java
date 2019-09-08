package net.sunxu.website.config.security.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.io.IOException;
import java.net.URI;
import net.sunxu.website.config.security.authentication.AuthTokenDefine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    @LoadBalanced
    private RestTemplate ribbonTemplate;

    @Autowired
    private TestService testService;

    @Autowired
    private RequestInterceptor requestInterceptor;

    @Test
    public void testRequestInterceptor() {
        var template = new RequestTemplate();
        requestInterceptor.apply(template);
        var headers = template.headers().get(AuthTokenDefine.TOKEN_HEADER_NAME);
        Assert.assertTrue(headers.iterator().next().startsWith(AuthTokenDefine.TOKEN_PREFIX));
    }

    @Test
    public void testFeignClient() {
        try {
            testService.test();
        } catch (RuntimeException err) {
        }
    }

    @Test
    public void testRibbonTemplateInterceptor() throws IOException {
        var interceptor = ribbonTemplate.getInterceptors().get(0);
        var request = Mockito.mock(HttpRequest.class);
        var httpHeaders = Mockito.spy(new HttpHeaders());
        Mockito.when(request.getHeaders()).thenReturn(httpHeaders);

        Mockito.when(request.getURI()).thenReturn(URI.create("http://app-service/test"));
        interceptor.intercept(request, null, Mockito.mock(ClientHttpRequestExecution.class));
        Mockito.verify(httpHeaders).add(Mockito.eq(AuthTokenDefine.TOKEN_HEADER_NAME), Mockito.startsWith("basic "));

        Mockito.when(request.getURI()).thenReturn(URI.create("http://other-service/test"));
        interceptor.intercept(request, null, Mockito.mock(ClientHttpRequestExecution.class));
        Mockito.verify(httpHeaders).add(Mockito.eq(AuthTokenDefine.TOKEN_HEADER_NAME),
                Mockito.startsWith(AuthTokenDefine.TOKEN_PREFIX));
    }

}
