package ltd.newbee.mall.app.controller;


import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.CancelOrderRequest;
import ltd.newbee.mall.app.dto.CreateOrderRequest;
import ltd.newbee.mall.app.dto.PaymentRequestDto;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.AddressManagement;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.manager.NewBeeMallOrderManager;
import ltd.newbee.mall.service.AddressManagementService;
import ltd.newbee.mall.service.PaymentService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于v1.0 app订单信息相关交互接口
 * 包括订单列表、订单详情、生成订单等接口
 * 具体接口信息待与fe确定
 */
@Controller
@RequestMapping("/app/order")
public class AppOrderController {

    private static Logger logger = LoggerFactory.getLogger(PaymentControllerbak.class);

    @Resource
    private NewBeeMallOrderManager newBeeMallOrderManager;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private PaymentService paymentService;

    @Resource
    private AddressManagementService addressManagementService;

    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    @ResponseBody
    public Result createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        logger.info("createOrder param : createOrderRequest = {}", JSON.toJSON(createOrderRequest));
        Object object = redisUtil.get(createOrderRequest.getToken());
        logger.info("createOrder getOpenId : object = {}", object);
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
        if (createOrderRequest.getAddressManagementId() == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.ORDER_ADDRESS_IS_NULL.getCode(), ResultMsgEnum.ORDER_ADDRESS_IS_NULL.getMsg());
        }
        AddressManagement addressManagement = addressManagementService.getAddressInfoById(mallUserList.get(0).getUserId(), createOrderRequest.getAddressManagementId());
        if (addressManagement == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.ORDER_ADDRESS_IS_ERROR.getCode(), ResultMsgEnum.ORDER_ADDRESS_IS_ERROR.getMsg());
        }
        if (createOrderRequest.getFromType() == 1 && CollectionUtils.isEmpty(createOrderRequest.getCartItemIdList())) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.CART_ITEM_LIST_IS_EMPTY.getCode(), ResultMsgEnum.CART_ITEM_LIST_IS_EMPTY.getMsg());
        }
        createOrderRequest.setAddress(addressManagement.getAddress());
        createOrderRequest.setConsigneeName(addressManagement.getConsigneeName());
        createOrderRequest.setPhone(addressManagement.getPhone());
        NewBeeMallUserVO userVO = new NewBeeMallUserVO();
        userVO.setUserId(mallUserList.get(0).getUserId());
        userVO.setChannelId(1);
        userVO.setAddress(mallUserList.get(0).getAddress());
        String orderNo = newBeeMallOrderManager.createOrder(userVO, createOrderRequest);
        return ResultGenerator.genSuccessDateResult(orderNo);
    }

    @RequestMapping(value = "/myOrderList", method = RequestMethod.GET)
    @ResponseBody
    public Result myOrderList(String token) {
        Object object = redisUtil.get(token);
        logger.info("myOrderList getOpenId : object = {}", object);
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
        Map<String, Object> params = new HashMap();
        params.put("page", 1);
        params.put("limit", 100);
        params.put("userId", mallUserList.get(0).getUserId());
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessDateResult(newBeeMallOrderManager.getMyOrders(pageQueryUtil));
    }

    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result orderDetail(String token, String orderNo) {
        Object object = redisUtil.get(token);
        logger.info("orderDetail getOpenId : object = {}", object);
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

        return ResultGenerator.genSuccessDateResult(newBeeMallOrderManager.getOrderDetailByOrderNo(orderNo, mallUserList.get(0).getUserId()));
    }

    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public Result cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest) {
        Object object = redisUtil.get(cancelOrderRequest.getToken());
        logger.info("cancelOrder getOpenId : object = {}", object);
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

        newBeeMallOrderManager.cancelOrder(cancelOrderRequest.getOrderNo(), mallUserList.get(0).getUserId());
        return ResultGenerator.genSuccessResult();
    }
}
