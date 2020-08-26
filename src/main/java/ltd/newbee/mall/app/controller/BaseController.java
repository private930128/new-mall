package ltd.newbee.mall.app.controller;

import com.alibaba.fastjson.JSON;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class BaseController {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Autowired
    private RedisUtil redisUtil;

    protected MallUser getMallUser(String token) {
        Object object = redisUtil.get(token);
        log.info("BaseController getMallUser getOpenId : object = {}", object);
        if (object == null) {
            return null;
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return null;
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return null;
        }
        return mallUserList.get(0);
    }
}
