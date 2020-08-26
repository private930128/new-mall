package ltd.newbee.mall.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ltd.newbee.mall.app.constant.MsgConstant;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.AppMsgDto;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.controller.vo.SmsResultVO;
import ltd.newbee.mall.properties.SmsProperties;
import ltd.newbee.mall.service.fegin.NewSmsApiService;
import ltd.newbee.mall.service.fegin.SmsApiService;
import ltd.newbee.mall.util.NumberUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 用于v1.0 app用户信息相关交互接口
 * 短信相关
 */
@RestController
@RequestMapping("/app/msg/")
@Api(value = "app发送短信相关接口")
public class AppMsgController {

    private static Logger logger = LoggerFactory.getLogger(AppMsgController.class);

    //验证码长度
    public static final int LENGTH = 6;
    //验证码超时时间（单位秒）
    public static final int TIME = 60;
    @Autowired
    private SmsApiService smsApiService;
    @Resource
    private NewSmsApiService newSmsApiService;
    @Autowired
    private SmsProperties smsProperties;
    @Autowired
    private RedisUtil redisUtil;

    private final String MSG_LIMIT = "msg_limit:";

    private Integer secondsOfOneDay = 24 * 60 * 60;

    @ApiOperation(value = "短信发送接口")
    @PostMapping("sendMsg2")
    public Result sendMsg2(@RequestBody AppMsgDto appMsgDto) {
        //TODO 1.前后端加密验证签名。 2.请求前后添加日志。 完成 3.请求对象统一校验
        //TODO 1.防刷 2.记录请求成功与否 3.校验验证码 4.添加渠道
        String verCode = String.valueOf(NumberUtil.genRandomNum(LENGTH));
        SmsResultVO smsResultVO = this.smsApiService.sendMessage(this.smsProperties.requestParam(appMsgDto.getPhone(), verCode));
        redisUtil.set(appMsgDto.getPhone(), verCode, TIME);
        return ResultGenerator.genSuccessDateResult(smsResultVO);
    }

    @ApiOperation(value = "短信发送接口")
    @PostMapping("sendMsg")
    public Result sendMsg(@RequestBody AppMsgDto appMsgDto) {
        // 防刷处理,1分钟内如果有缓存码，不发送。 如果没有，验证当天内5条限制。

        Object verCodeObj = redisUtil.get(appMsgDto.getPhone());
        if (verCodeObj != null && StringUtils.isNotEmpty(verCodeObj.toString())) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.MSG_LIMIT_IN_ONE_MINUTE.getCode(), ResultMsgEnum.MSG_LIMIT_IN_ONE_MINUTE.getMsg());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateStr = dateFormat.format(new Date());
        String msgLimitKey = MSG_LIMIT + appMsgDto.getPhone() + ":" + dateStr;
        boolean sendMsgLimit = msgLimitIncrease(msgLimitKey);
        if (sendMsgLimit) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.MSG_LIMIT_IN_ONE_DAY.getCode(), ResultMsgEnum.MSG_LIMIT_IN_ONE_DAY.getMsg());
        }

        String verCode = String.valueOf(NumberUtil.genRandomNum(LENGTH));
        String param = generateParam(appMsgDto.getPhone(), verCode);
        logger.info("发送短信 param = " + param);
        SmsResultVO smsResultVO = this.newSmsApiService.sendMessage(param);
        redisUtil.set(appMsgDto.getPhone(), verCode, TIME);
        return ResultGenerator.genSuccessDateResult(smsResultVO);
    }

    private boolean msgLimitIncrease(String msgLimitKey) {
        logger.info("msgLimitIncrease msgLimitKey = {}", msgLimitKey);
        Object limit = redisUtil.get(msgLimitKey);
        if (limit == null || StringUtils.isEmpty(limit.toString())) {
            redisUtil.set(msgLimitKey, 1);
            return false;
        } else {
            Integer limitCount = Integer.valueOf(limit.toString());
            if (limitCount > 5) {
                return true;
            }
            redisUtil.set(msgLimitKey, ++limitCount);
            return false;
        }
    }

    private String generateParam(String mobile, String verCode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String appId = MsgConstant.APPID;
        String secretKey = MsgConstant.SECRET_KEY;
        String timestamp = dateFormat.format(new Date());
        String text = appId + secretKey + timestamp;
        String sign = DigestUtils.md5Hex(getContentBytes(text, "utf-8"));
        String content = MsgConstant.CONMENT_PREFIX + verCode + MsgConstant.CONMENT_SUFFIX;
        String param = "appId=" + appId + "&timestamp=" + timestamp + "&sign=" + sign + "&mobiles=" + mobile + "&content=" + content;
        return param;
    }

    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
}
