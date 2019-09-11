package net.sunxu.website.config.feignclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import net.sunxu.website.config.feignclient.exception.ErrorDTO;
import net.sunxu.website.config.feignclient.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

public class FeignErrorDecoder implements ErrorDecoder {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            if (response.body() != null) {
                ErrorDTO msg = mapper.readValue(response.body().asInputStream(), ErrorDTO.class);
                return ServiceException.wrapException(msg);
            } else {
                return ServiceException.newException(HttpStatus.resolve(response.status()), "service error");
            }
        } catch (Exception err) {
            throw ServiceException.wrapException(err);
        }
    }
}