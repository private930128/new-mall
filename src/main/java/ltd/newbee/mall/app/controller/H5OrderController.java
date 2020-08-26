package ltd.newbee.mall.app.controller;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.CancelOrderRequest;
import ltd.newbee.mall.app.dto.CreateOrderRequest;
import ltd.newbee.mall.config.redis.RedisUtil;
import ltd.newbee.mall.controller.vo.NewBeeMallUserVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.manager.NewBeeMallOrderManager;
import ltd.newbee.mall.service.PaymentService;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import ltd.newbee.mall.util.wxpay.PaymentControllerbak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhanghenan on 2020/5/5.
 */
@RestController
@RequestMapping("/h5/order/")
public class H5OrderController {

    private static Logger logger = LoggerFactory.getLogger(PaymentControllerbak.class);

    @Resource
    private NewBeeMallOrderManager newBeeMallOrderManager;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MallUserMapper mallUserMapper;
    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    @ResponseBody
    public Result createOrder(@RequestBody CreateOrderRequest createOrderRequest, HttpServletRequest request) {
        logger.info("createOrder param : createOrderRequest = {}", JSON.toJSON(createOrderRequest));
        Object loginUserIdObj = request.getSession().getAttribute("loginUserId");
        if (loginUserIdObj == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        Long loginUserId = Long.valueOf(loginUserIdObj.toString());

        MallUser mallUser = mallUserMapper.selectByPrimaryKey(loginUserId);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.USER_NOT_EXIST.getCode(), ResultMsgEnum.USER_NOT_EXIST.getMsg());
        }
        NewBeeMallUserVO userVO = new NewBeeMallUserVO();
        userVO.setUserId(mallUser.getUserId());
        userVO.setChannelId(1);
        userVO.setAddress(mallUser.getAddress());
        String orderNo = newBeeMallOrderManager.createOrder(userVO, createOrderRequest);
        return ResultGenerator.genSuccessDateResult(orderNo);
    }

    @RequestMapping(value = "/myOrderList", method = RequestMethod.GET)
    @ResponseBody
    public Result myOrderList(HttpServletRequest request) {
        Object loginUserIdObj = request.getSession().getAttribute("loginUserId");
        if (loginUserIdObj == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        Long loginUserId = Long.valueOf(loginUserIdObj.toString());

        MallUser mallUser = mallUserMapper.selectByPrimaryKey(loginUserId);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.USER_NOT_EXIST.getCode(), ResultMsgEnum.USER_NOT_EXIST.getMsg());
        }

        Map<String, Object> params = new HashMap();
        params.put("page", 1);
        params.put("limit", 100);
        params.put("userId", mallUser.getUserId());
        PageQueryUtil pageQueryUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessDateResult(newBeeMallOrderManager.getMyOrders(pageQueryUtil));
    }

    @RequestMapping(value = "/orderDetail", method = RequestMethod.GET)
    @ResponseBody
    public Result orderDetail(HttpServletRequest request, String orderNo) {
        Object loginUserIdObj = request.getSession().getAttribute("loginUserId");
        if (loginUserIdObj == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        Long loginUserId = Long.valueOf(loginUserIdObj.toString());

        MallUser mallUser = mallUserMapper.selectByPrimaryKey(loginUserId);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.USER_NOT_EXIST.getCode(), ResultMsgEnum.USER_NOT_EXIST.getMsg());
        }
        return ResultGenerator.genSuccessDateResult(newBeeMallOrderManager.getOrderDetailByOrderNo(orderNo, mallUser.getUserId()));
    }

    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @ResponseBody
    public Result cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest, HttpServletRequest request) {
        Object loginUserIdObj = request.getSession().getAttribute("loginUserId");
        if (loginUserIdObj == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(), ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        Long loginUserId = Long.valueOf(loginUserIdObj.toString());

        MallUser mallUser = mallUserMapper.selectByPrimaryKey(loginUserId);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.USER_NOT_EXIST.getCode(), ResultMsgEnum.USER_NOT_EXIST.getMsg());
        }

        newBeeMallOrderManager.cancelOrder(cancelOrderRequest.getOrderNo(), mallUser.getUserId());
        return ResultGenerator.genSuccessResult();
    }
}
