package ltd.newbee.mall.app.controller;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.AddressRequestDto;
import ltd.newbee.mall.app.dto.CreateOrderRequest;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.AddressManagement;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.manager.NewBeeMallAddressManager;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
@Controller
@RequestMapping("/app/address")
public class AppAddressController {

    @Autowired
    private RedisUtil redisUtil;

    @Resource
    private NewBeeMallAddressManager newBeeMallAddressManager;

    @Autowired
    private MallUserMapper mallUserMapper;

    private static Logger logger = LoggerFactory.getLogger(AppAddressController.class);

    @RequestMapping(value = "/listAddress", method = RequestMethod.GET)
    @ResponseBody
    public Result listAddress(String token) {
        Object object = redisUtil.get(token);
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }

        return ResultGenerator.genSuccessDateResult(newBeeMallAddressManager.listAddressInfoByUser(mallUserList.get(0).getUserId()));
    }

    @RequestMapping(value = "/getAddressById", method = RequestMethod.GET)
    @ResponseBody
    public Result getAddressById(String token, Long id) {
        Object object = redisUtil.get(token);
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }

        return ResultGenerator.genSuccessDateResult(newBeeMallAddressManager.getAddressInfoById(mallUserList.get(0).getUserId(), id));
    }

    @RequestMapping(value = "/getDefaultAddress", method = RequestMethod.GET)
    @ResponseBody
    public Result getDefaultAddress(String token) {
        Object object = redisUtil.get(token);
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        return ResultGenerator.genSuccessDateResult(newBeeMallAddressManager.getDefaultAddressInfoByUser(mallUserList.get(0).getUserId()));
    }

    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    @ResponseBody
    public Result saveAddress(@RequestBody AddressRequestDto addressRequestDto) {
        Object object = redisUtil.get(addressRequestDto.getToken());
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        logger.info("saveAddress addressRequestDto = {}", JSON.toJSON(addressRequestDto));
        AddressManagement addressManagement = new AddressManagement();
        addressManagement.setAddress(addressRequestDto.getAddress());
        addressManagement.setConsigneeName(addressRequestDto.getConsigneeName());
        addressManagement.setPhone(addressRequestDto.getPhone());
        addressManagement.setUserId(mallUserList.get(0).getUserId());
        addressManagement.setDefaultStatus(addressRequestDto.getDefaultStatus());
        logger.info("saveAddress addressManagement = {}", JSON.toJSON(addressManagement));
        return ResultGenerator.genSuccessDateResult(newBeeMallAddressManager.saveAddressManagement(addressManagement));
    }

    @RequestMapping(value = "/updateAddress", method = RequestMethod.POST)
    @ResponseBody
    public Result updateAddress(@RequestBody AddressRequestDto addressRequestDto) {
        Object object = redisUtil.get(addressRequestDto.getToken());
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        AddressManagement addressManagement = new AddressManagement();
        addressManagement.setId(addressRequestDto.getId());
        addressManagement.setAddress(addressRequestDto.getAddress());
        addressManagement.setConsigneeName(addressRequestDto.getConsigneeName());
        addressManagement.setPhone(addressRequestDto.getPhone());
        addressManagement.setUserId(mallUserList.get(0).getUserId());
        addressManagement.setDefaultStatus(addressRequestDto.getDefaultStatus());
        logger.info("updateAddress addressManagement = {}", JSON.toJSON(addressManagement));
        return ResultGenerator.genSuccessDateResult(newBeeMallAddressManager.updateAddressManagement(addressManagement));
    }

    @RequestMapping(value = "/setDefaultAddress", method = RequestMethod.POST)
    @ResponseBody
    public Result setDefaultAddress(@RequestBody AddressRequestDto addressRequestDto) {
        Object object = redisUtil.get(addressRequestDto.getToken());
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        newBeeMallAddressManager.setDefaultAddressManagement(addressRequestDto.getId(), mallUserList.get(0).getUserId());
        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    @ResponseBody
    public Result deleteAddress(@RequestBody AddressRequestDto addressRequestDto) {
        Object object = redisUtil.get(addressRequestDto.getToken());
        if (object == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String openId = object.toString();
        if (StringUtils.isEmpty(openId)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<MallUser> mallUserList = mallUserMapper.selectByOpenId(openId);
        if (CollectionUtils.isEmpty(mallUserList)) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        newBeeMallAddressManager.deleteAddress(mallUserList.get(0).getUserId(), addressRequestDto.getId());
        return ResultGenerator.genSuccessResult();
    }
}
