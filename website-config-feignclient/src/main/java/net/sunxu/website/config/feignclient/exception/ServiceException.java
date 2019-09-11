package net.sunxu.website.config.feignclient.exception;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

public class ServiceException extends RuntimeException {

    protected static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

    @Getter
    @Setter
    private HttpStatus httpStatus;

    @Getter
    @Setter
    private List<String> errors;

    public ServiceException(HttpStatus httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public static ServiceException newException(HttpStatus httpStatus, String message, Object... paras) {
        return new ServiceException(httpStatus, format(message, paras), null);
    }

    protected static String format(String message, Object... paras) {
        if (paras.length > 0) {
            return String.format(message, paras);
        }
        return message;
    }

    public static ServiceException newException(String message, Object... paras) {
        return newException(DEFAULT_HTTP_STATUS, message, paras);
    }

    public static ServiceException wrapException(HttpStatus httpStatus, Throwable cause) {
        return new ServiceException(httpStatus, null, cause);
    }

    public static ServiceException wrapException(Throwable cause) {
        return wrapException(DEFAULT_HTTP_STATUS, cause);
    }

    public static ServiceException wrapException(Throwable cause, String message, Object... paras) {
        return new ServiceException(DEFAULT_HTTP_STATUS, format(message, paras), cause);
    }

    public static ServiceException wrapException(ErrorDTO dto) {
        var exception = newException(HttpStatus.valueOf(dto.getStatus()), dto.getMessage());
        exception.setErrors(dto.getErrors());
        return exception;
    }
}
