package ltd.newbee.mall.config.feign.support;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * map编码器
 */
public class MapFormHttpMessageConverter implements HttpMessageConverter<Map<String, ?>> {

    protected FormHttpMessageConverter formHttpMessageConverter;

    public MapFormHttpMessageConverter() {
        this.formHttpMessageConverter = new MultipartFormHttpMessageConverter();
    }


    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        this.formHttpMessageConverter.addPartConverter(partConverter);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return formHttpMessageConverter.canRead(clazz, mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return formHttpMessageConverter.getSupportedMediaTypes();
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (mediaType == null || MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<String, ?> read(Class<? extends Map<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return formHttpMessageConverter.read(null, inputMessage);
    }

    @Override
    public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MultiValueMap<String, Object> multiMap = null;
        if (map != null) {
            if (map instanceof MultiValueMap) {
                multiMap = (MultiValueMap<String, Object>) map;
            } else {
                multiMap = new LinkedMultiValueMap<>();
                for (Map.Entry<String, ?> entry : map.entrySet()) {
                    multiMap.add(entry.getKey(), entry.getValue());
                }
            }
        }
        formHttpMessageConverter.write(multiMap, contentType, outputMessage);
    }

    public static class MultipartFormHttpMessageConverter extends FormHttpMessageConverter {
        @Override
        protected String getFilename(Object part) {
            String rt = super.getFilename(part);
            if (rt == null && part instanceof MultipartFile) {
                return ((MultipartFile) part).getOriginalFilename();
            }
            return null;
        }
    }
}
