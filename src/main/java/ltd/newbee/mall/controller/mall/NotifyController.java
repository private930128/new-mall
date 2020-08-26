package ltd.newbee.mall.controller.mall;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.common.PayCodeEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notify")
public class NotifyController {

    @RequestMapping(value = {"/alipayNotify"}, method = {RequestMethod.POST})
    public Result alipayNotify(@RequestBody String body) {
        // Log.i(LogScene.NOTIFY_ALIPAY,"收到支付宝回调:"+ body);
        // Log.i(LogScene.NOTIFY_ALIPAY,"应答给支付宝:"+ JSON.toJSONString(responseEntity));
        return ResultGenerator.genFailResult(PayCodeEnum.WECHAT.getDesc());
    }

    @RequestMapping(value = {"/wechatNotify"}, method = {RequestMethod.POST})
    public Result wechatNotify(@RequestBody String body) {
        // Log.i(LogScene.NOTIFY_WECHAT,"收到微信回调:"+ body);
        // Log.i(LogScene.NOTIFY_WECHAT,"应答给微信:"+ JSON.toJSONString(responseEntity));
        return ResultGenerator.genFailResult(PayCodeEnum.WECHAT.getDesc());
    }

    @RequestMapping(value = {"/test"}, method = {RequestMethod.POST})
    public ResponseEntity<Object> test(@RequestBody String body) {
        // Log.i(LogScene.NOTIFY_WECHAT,"收到测试请求:"+ body);
        return new ResponseEntity<Object>("SUCCESS", HttpStatus.OK);
    }
}
