package net.sunxu.website.config.security;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

@Data
@ToString
@Validated
public class AppProperties {

    public static final String APP_SERVICE = "app-service";

    @NotNull
    private Long id;

    @NotEmpty
    private String password;

    private int retryTimes = 5;

    private boolean examineServiceOnly = false;

}
