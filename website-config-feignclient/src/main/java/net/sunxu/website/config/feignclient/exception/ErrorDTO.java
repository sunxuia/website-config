package net.sunxu.website.config.feignclient.exception;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@ToString
public class ErrorDTO implements Serializable {

    private LocalDateTime timestamp;

    private String path;

    private Integer status;

    private String error;

    private List<String> errors;

    private String message;


    public static ErrorDTO newInstance(HttpStatus httpStatus, String message, String path) {
        ErrorDTO dto = new ErrorDTO();
        dto.timestamp = LocalDateTime.now();
        dto.status = httpStatus.value();
        dto.error = httpStatus.getReasonPhrase();
        dto.message = message;
        dto.path = path;
        return dto;
    }

    public static ErrorDTO newInstance(Integer status, String message, String path) {
        return newInstance(Objects.requireNonNull(HttpStatus.resolve(status)), message, path);
    }
}
