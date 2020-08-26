package ltd.newbee.mall.app.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.app.constant.ResultMsgEnum;
import ltd.newbee.mall.app.dto.CartDto;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.dao.MallUserMapper;
import ltd.newbee.mall.entity.MallUser;
import ltd.newbee.mall.manager.NewBeeMallCartManager;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "AppCartController", description = "购物车管理")
@Slf4j
@RestController
@RequestMapping("/app/cart")
public class AppCartController extends BaseController {

    @Autowired
    private NewBeeMallCartManager newBeeMallCartManager;
    @Autowired
    private MallUserMapper mallUserMapper;

    @ApiOperation("获取某个用户的购物车列表")
    @GetMapping("list")
    public Result list(String token) {
        log.info("AppCartController [list] method param : token: {}", token);
        MallUser mallUser = getMallUser(token);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        List<NewBeeMallShoppingCartItemVO> cartList =
                newBeeMallCartManager.getMyShoppingCartItems(mallUser.getUserId());

        return ResultGenerator.genSuccessDateResult(cartList);

    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("add")
    public Result add(@RequestBody CartDto cartDto) {
        log.info("AppCartController add method [addCart] param : CartDto = {}",
                JSON.toJSON(cartDto));
        MallUser mallUser = getMallUser(cartDto.getToken());
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String result =
                newBeeMallCartManager.saveNewBeeMallCartItem(cartDto.getGoodsInfo(),
                        mallUser.getUserId());
        if ("success".equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("添加购物车失败");
        }
    }

    @ApiOperation("修改购物车中某个商品的数量")
    @PostMapping("update")
    public Result update(@RequestBody CartDto cartDto) {
        log.info("AppCartController update method [updateCart] param : CartDto = {}",
                JSON.toJSON(cartDto));
        MallUser mallUser = getMallUser(cartDto.getToken());
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        String result = newBeeMallCartManager.updateNewBeeMallCartItem(cartDto.getGoodsInfo());
        if ("success".equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("更新购物车失败");
        }
    }

    @ApiOperation("删除勾选记录")
    @PostMapping("/delete")
    public Result deleteById(@RequestBody CartDto cartDto) {
        log.info("AppCartController delete method [deleteById] param : CartDto = {}",
                JSON.toJSON(cartDto));
        MallUser mallUser = getMallUser(cartDto.getToken());
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        if (newBeeMallCartManager.deleteBatch(cartDto.getIds()) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除购物车商品失败");
        }

    }

    @ApiOperation("清空购物车")
    @PostMapping("clearCart")
    public Result clearCart(String token) {
        log.info("AppCartController clearCart method [clearCart] param : token: {}", token);
        MallUser mallUser = getMallUser(token);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        if (newBeeMallCartManager.clearCart(mallUser.getUserId()) > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("清空购物车失败");
        }
    }

    @ApiOperation("订单提交前的检验和填写相关订单信息")
    @GetMapping("checkout")
    public Result checkout(@RequestParam("ids") List<Long> ids, @RequestParam("token") String token) {
        MallUser mallUser = getMallUser(token);
        if (mallUser == null) {
            return ResultGenerator.genErrorResult(ResultMsgEnum.LOGIN_INFO_IS_NULL.getCode(),
                    ResultMsgEnum.LOGIN_INFO_IS_NULL.getMsg());
        }
        Map<String, Object> resultObj = new HashMap();

        // 默认收货地址
        /*
         * Map param = new HashMap(); param.put("user_id", mallUser.getUserId()); List
         * addressEntities = addressService.queryList(param);
         * 
         * if (null == addressEntities || addressEntities.size() == 0) {
         * resultObj.put("checkedAddress", new AddressVo()); } else {
         * resultObj.put("checkedAddress", addressEntities.get(0)); }
         */
//        resultObj.put("checkedAddress", "北京");
        Long goodsTotalPrice;
        /*
         * Map<String, Object> cartData = (Map<String, Object>) this.getCart(loginUser);
         * 
         * for (CartVo cartEntity : (List<CartVo>) cartData.get("cartList")) { if
         * (cartEntity.getChecked() == 1) { checkedGoodsList.add(cartEntity); } } goodsTotalPrice =
         * (BigDecimal) ((HashMap) cartData.get("cartTotal")).get("checkedGoodsAmount");
         */
        // 获取可用的优惠券信息
        Long couponPrice = 0L;
        /*
         * if (couponId != null && couponId != 0) { CouponVo couponVo =
         * apiCouponMapper.getUserCoupon(couponId); if (couponVo != null) { couponPrice =
         * couponVo.getType_money(); } }
         */

        // 订单的总价
        // Long orderTotalPrice = goodsTotalPrice;

        //
        // Long actualPrice = orderTotalPrice - couponPrice; // 减去其它支付的金额后，要实际支付的金额


        // resultObj.put("goodsTotalPrice", goodsTotalPrice);
        // resultObj.put("orderTotalPrice", orderTotalPrice);
        // resultObj.put("actualPrice", actualPrice);
        int goodsCount = 0;
        int priceTotal = 0;
        List<NewBeeMallShoppingCartItemVO> cartList = newBeeMallCartManager.getCartByIds(ids);
        if (!CollectionUtils.isEmpty(cartList)) {
            goodsCount =
                    cartList.stream().mapToInt(NewBeeMallShoppingCartItemVO::getGoodsCount).sum();
            for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : cartList) {
                priceTotal +=
                        newBeeMallShoppingCartItemVO.getGoodsCount()
                                * newBeeMallShoppingCartItemVO.getSellingPrice();
            }

        }

        for (NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO : cartList) {
            newBeeMallShoppingCartItemVO.setRealSellingPrice(new BigDecimal(newBeeMallShoppingCartItemVO.getSellingPrice()).divide(new BigDecimal(100)));
        }

        resultObj.put("goodsCount", goodsCount);
        resultObj.put("priceTotal", new BigDecimal(priceTotal).divide(new BigDecimal(100)));
        resultObj.put("cartList", cartList);
        resultObj.put("couponPrice", couponPrice);
        return ResultGenerator.genSuccessDateResult(resultObj);
    }
}
