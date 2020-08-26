package ltd.newbee.mall.config.feign;


import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Logger;
import feign.Retryer;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import ltd.newbee.mall.config.feign.support.MapFormHttpMessageConverter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * feign config
 */
@Configuration
public class FeignConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }

    @Bean
    Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }

    /**
     * form编码器，实现支持form表单提交
     */
    @Bean
    public Encoder feignFormEncoder() {
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customObjectMapper());
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        for (HttpMessageConverter<?> converter : messageConverters.getObject().getConverters()) {
            if (!(converter instanceof MappingJackson2HttpMessageConverter)) {
                converters.add(converter);
            }
        }
        converters.add(jacksonConverter);
        converters.add( new MapFormHttpMessageConverter() );
        return new SpringFormEncoder(new SpringEncoder(() -> new HttpMessageConverters(converters)));
    }

    @Bean
    public Decoder feignDecoder() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> supportedMediaTypes = new ArrayList<>(  );
        supportedMediaTypes.add( MediaType.ALL );
        converter.setSupportedMediaTypes( supportedMediaTypes );
        ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(converter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    private ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }

}

