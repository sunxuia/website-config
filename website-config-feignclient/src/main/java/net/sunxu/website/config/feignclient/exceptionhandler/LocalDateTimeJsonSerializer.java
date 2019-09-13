package net.sunxu.website.config.feignclient.exceptionhandler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {

    private static final StdDateFormat format = new StdDateFormat();

    public LocalDateTimeJsonSerializer() {
        format.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        var date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        String str = format.format(date);
        jsonGenerator.writeString(str);
    }
}
