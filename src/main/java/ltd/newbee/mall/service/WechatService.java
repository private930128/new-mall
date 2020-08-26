package ltd.newbee.mall.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.controller.vo.WechatAuthCodeResponseVO;
import ltd.newbee.mall.controller.vo.WechatAuthTokenVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.properties.WechatAuthProperties;
import ltd.newbee.mall.service.fegin.WeChatApiService;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WechatService {

    private static Logger logger = LoggerFactory.getLogger(PaymentControllerbak.class);

    private static final Long EXPIRES = 86400L;

    @Autowired
    private WeChatApiService weChatApiService;
    @Autowired
    private WechatAuthProperties wechatAuthProperties;
    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 微信登录
     * @param user
     * @return
     * @throws Exception
     */
    public WechatAuthTokenVO wechatLogin(MallUser user) throws Exception {
        logger.info("wechatLogin param : user = {}", JSON.toJSON(user));
        //1.获取openId
        WechatAuthCodeResponseVO response = this.weChatApiService.jscode2session(wechatAuthProperties.getAppId(),
                wechatAuthProperties.getSecret(),
                user.getCode(),
                wechatAuthProperties.getGrantType());

        //2.保存用户信息
        String wxOpenId = response.getOpenid();
        String wxSessionKey = response.getSession_key();
        logger.info("wxOpenId = {}, wxSessionKey = {}", wxOpenId, wxSessionKey);
        //3.查询用户是否存在，用户不存在提示错误，需要注册
        List<MallUser> mallUser = mallUserMapper.selectByOpenId(wxOpenId);
        if (CollectionUtils.isEmpty(mallUser)) {
            return null;
        }
        //4.生成token
        return new WechatAuthTokenVO(create3rdToken(wxOpenId, wxSessionKey, EXPIRES));

    }

    public String create3rdToken(String wxOpenId, String wxSessionKey, Long expires) {
        String thirdSessionKey = RandomStringUtils.randomAlphanumeric(64);
        StringBuffer sb = new StringBuffer();
        sb.append(wxSessionKey).append("#").append(wxOpenId);

        redisUtil.set(thirdSessionKey, wxOpenId, expires);
        return thirdSessionKey;
    }

    public WechatAuthTokenVO registry(MallUser user) throws Exception {
        logger.info("registry param : user = {}", JSON.toJSON(user));
        //1.获取openId
        WechatAuthCodeResponseVO response = this.weChatApiService.jscode2session(wechatAuthProperties.getAppId(),
                wechatAuthProperties.getSecret(),
                user.getCode(),
                wechatAuthProperties.getGrantType());

        //2.保存用户信息
        String wxOpenId = response.getOpenid();
        String wxSessionKey = response.getSession_key();
        logger.info("wxOpenId = {}, wxSessionKey = {}", wxOpenId, wxSessionKey);
        user.setWechatOpenid(wxOpenId);
        this.mallUserMapper.insert(user);
        //3.生成token
        return new WechatAuthTokenVO(create3rdToken(wxOpenId, wxSessionKey, EXPIRES));

    }

}