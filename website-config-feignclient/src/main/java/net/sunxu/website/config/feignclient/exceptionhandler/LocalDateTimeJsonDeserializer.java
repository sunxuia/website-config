package net.sunxu.website.config.feignclient.exceptionhandler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import net.sunxu.website.help.util.ExceptionHelpUtils;

public class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final StdDateFormat format = new StdDateFormat();

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        var date = ExceptionHelpUtils.wrapException(() -> format.parse(text));
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
