package ltd.newbee.mall.service.fegin;

import feign.Headers;
import feign.RequestLine;
import ltd.newbee.mall.config.feign.FeignConfig;
import ltd.newbee.mall.controller.vo.SmsResultVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://www.btom.cn:8080", name = "newSmsApiService", primary = false, configuration = FeignConfig.class)
@Headers("Accept: application/json")
public interface NewSmsApiService {

    @RequestLine("GET /simpleinter/sendSMS")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    SmsResultVO sendMessage(@RequestParam("param") String param);
}
