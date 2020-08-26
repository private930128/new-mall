package ltd.newbee.mall.service.fegin;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import ltd.newbee.mall.config.feign.FeignConfig;
import ltd.newbee.mall.controller.vo.SmsResultVO;
import ltd.newbee.mall.controller.vo.WechatAuthCodeResponseVO;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "${wechat.host}", name = "weChatApiService", primary = false, configuration = FeignConfig.class)
@Headers("Accept: application/json")
public interface WeChatApiService {

    @RequestLine("GET /sns/jscode2session?appid={appid}&secret={srcret}&js_code={code}&grant_type={grantType}")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    WechatAuthCodeResponseVO jscode2session(@Param("appid") String appid, @Param("srcret") String srcret, @Param("code") String code, @Param("grantType") String grantType);
}