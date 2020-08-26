package ltd.newbee.mall.app.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.UserInfoDto;
import ltd.newbee.mall.app.dto.UserRegistryDto;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanghenan on 2020/5/4.
 */
@RestController
@RequestMapping("/h5/user/")
public class H5UserController {

    private static Logger logger = LoggerFactory.getLogger(PaymentControllerbak.class);
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MallUserMapper mallUserMapper;

    @RequestMapping(value = "/registry", method = RequestMethod.POST)
    @ResponseBody
    public Result registry(@RequestBody UserRegistryDto userRegistryDto, HttpServletRequest request) {
        try {
            logger.info("registry param : userRegistryDto = {}", JSON.toJSON(userRegistryDto));
            if (StringUtils.isEmpty(userRegistryDto.getPhone()) || StringUtils.isEmpty(userRegistryDto.getVerCode())) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.MSG_VERIFY_PARAM_IS_NULL.getCode(), ResultMsgEnum.MSG_VERIFY_PARAM_IS_NULL.getMsg());
            }
//            Object object = redisUtil.get(userRegistryDto.getPhone());
//            logger.info("registry getVerCodeFromRedis : object = {}", JSON.toJSON(object));
//            if (object == null) {
//                return ResultGenerator.genErrorResult(ResultMsgEnum.VERIFICATION_CODE_OVERDUE.getCode(), ResultMsgEnum.VERIFICATION_CODE_OVERDUE.getMsg());
//            }
//            String verCodeFromRedis = object.toString();
//            if (StringUtils.isEmpty(verCodeFromRedis)) {
//                return ResultGenerator.genErrorResult(ResultMsgEnum.VERIFICATION_CODE_OVERDUE.getCode(), ResultMsgEnum.VERIFICATION_CODE_OVERDUE.getMsg());
//            }
//            if (!verCodeFromRedis.equals(userRegistryDto.getVerCode())) {
//                return ResultGenerator.genErrorResult(ResultMsgEnum.VERIFICATION_CODE_ERROR.getCode(), ResultMsgEnum.VERIFICATION_CODE_ERROR.getMsg());
//            }
            if (StringUtils.isEmpty(userRegistryDto.getPassword()) || StringUtils.isEmpty(userRegistryDto.getConfirmPassword())) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.PASSWORD_IS_NULL.getCode(), ResultMsgEnum.PASSWORD_IS_NULL.getMsg());
            }
            if (!userRegistryDto.getPassword().equals(userRegistryDto.getConfirmPassword())) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.REGISTRY_PASSWORD_CONFIRM_ERROR.getCode(), ResultMsgEnum.REGISTRY_PASSWORD_CONFIRM_ERROR.getMsg());
            }

            MallUser mallUser = mallUserMapper.selectByLoginName(userRegistryDto.getPhone());
            if (mallUser != null) {
                MallUser user = new MallUser();
                user.setUserId(mallUser.getUserId());
                user.setPasswordMd5(userRegistryDto.getPassword());
                user.setAddress("");
                mallUserMapper.updateByPrimaryKeySelective(user);
                logger.info("registry2 response user = {}", JSON.toJSON(user));
            } else {
                MallUser user = new MallUser();
                user.setLoginName(userRegistryDto.getPhone());
                user.setPasswordMd5(userRegistryDto.getPassword());
                user.setAddress("");
                user.setCreateTime(new Date());
                user.setCode(userRegistryDto.getCode());
                user.setNickName("");
                user.setIntroduceSign("");
                user.setIsDeleted((byte) 0);
                user.setLockedFlag((byte) 0);
                user.setWechatOpenid("");
                mallUserMapper.insert(user);
                logger.info("registry response user = {}", JSON.toJSON(user));
            }
            MallUser mallUser2 = mallUserMapper.selectByLoginName(userRegistryDto.getPhone());
            logger.info("registry set session user = {}", JSON.toJSON(mallUser2));
            request.getSession().setAttribute("loginUserId", mallUser2.getUserId());
            request.getSession().setMaxInactiveInterval(60 * 60 * 2);
            logger.info("registry response user12312321 = {}", request.getSession().getAttribute("loginUserId"));
            return ResultGenerator.genSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("注册失败");
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public Result login(MallUser user, HttpServletRequest request) {
        logger.info("login param : user = {}", JSON.toJSON(user));
        try {
            MallUser mallUser = mallUserMapper.selectByLoginName(user.getLoginName());
            if (mallUser == null) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.USER_NOT_EXIST.getCode(), ResultMsgEnum.USER_NOT_EXIST.getMsg());
            }
            if (StringUtils.isEmpty(mallUser.getPasswordMd5())) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.PASSWORD_NOT_SET_ERROR.getCode(), ResultMsgEnum.PASSWORD_NOT_SET_ERROR.getMsg());
            }
            if (!user.getPassword().equals(mallUser.getPasswordMd5())) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.PASSWORD_ERROR.getCode(), ResultMsgEnum.PASSWORD_ERROR.getMsg());
            }
            request.getSession().setAttribute("loginUserId", mallUser.getUserId());
            request.getSession().setMaxInactiveInterval(60 * 60 * 2);
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setLoginName(mallUser.getLoginName());
            userInfoDto.setAddress(mallUser.getAddress());
            return ResultGenerator.genSuccessDateResult(userInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("微信登录失败");
        }
    }

    @RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public Result getUserInfo(HttpServletRequest request) {
        Object loginUserIdObj = request.getSession().getAttribute("loginUserId");
        try {
            logger.info(" getUserInfo : loginUserId = {}", loginUserIdObj);
            if (loginUserIdObj == null) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
            }
            Long loginUserId = Long.valueOf(loginUserIdObj.toString());
            MallUser mallUser = mallUserMapper.selectByPrimaryKey(Long.valueOf(loginUserId));
            if (mallUser == null) {
                return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
            }
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setLoginName(mallUser.getLoginName());
            userInfoDto.setAddress(mallUser.getAddress());
            return ResultGenerator.genSuccessDateResult(userInfoDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genFailResult("获取用户信息失败");
        }
    }

}
