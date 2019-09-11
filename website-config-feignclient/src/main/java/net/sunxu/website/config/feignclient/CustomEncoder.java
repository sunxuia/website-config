package net.sunxu.website.config.feignclient;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

// 仿照 SpringFormEncoder 做的修改, 添加对MultipartFile[] 类型和Content-Type 的修改
public class CustomEncoder extends FormEncoder {

    public CustomEncoder() {
        this(new Default());
    }

    public CustomEncoder(Encoder delegate) {
        super(delegate);

        MultipartFormContentProcessor processor = (MultipartFormContentProcessor)
                getContentProcessor(ContentType.MULTIPART);
        processor.addWriter(new SpringSingleMultipartFileWriter());
        processor.addWriter(new SpringManyMultipartFilesWriter());
    }

    private static final String CONTENT_TYPE = "Content-Type";

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (bodyType.equals(MultipartFile.class)) {
            // MultipartFile
            MultipartFile file = (MultipartFile) object;
            Map data = Collections.singletonMap(file.getName(), object);
            super.encode(data, MAP_STRING_WILDCARD, template);
            resetHeaders(template);
            return;
        } else if (bodyType.equals(MultipartFile[].class)) {
            // MultipartFile[]
            MultipartFile[] files = (MultipartFile[]) object;
            if (files != null) {
                String name = files.length == 0 ? "" : files[0].getName();
                Map data = Collections.singletonMap(name, object);
                super.encode(data, MAP_STRING_WILDCARD, template);
                resetHeaders(template);
                return;
            }
        }
        // 默认处理
        super.encode(object, bodyType, template);
    }

    private void resetHeaders(RequestTemplate template) {
        var contentTypes = template.headers().get(CONTENT_TYPE);
        String realContentType = null;
        for (String contentType : contentTypes) {
            if (contentType != null && contentType.contains("boundary=")) {
                realContentType = contentType;
                break;
            }
        }
        if (realContentType != null) {
            template.header(CONTENT_TYPE);
            template.header(CONTENT_TYPE, realContentType);
        }
    }
}
