package ltd.newbee.mall.service.fegin;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ltd.newbee.mall.config.feign.FeignConfig;
import ltd.newbee.mall.controller.vo.SmsResultVO;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Map;

@FeignClient(url = "${sms.host}", name = "smsApiService", primary = false, configuration = FeignConfig.class)
@Headers("Accept: application/json")
public interface SmsApiService {

    @RequestLine("GET /sms/send?param={param}")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    SmsResultVO sendMessage(@Param("param") String param);
}