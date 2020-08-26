package ltd.newbee.mall.app.controller;

import ltd.newbee.mall.app.dto.AppGoodsQueryDto;
import ltd.newbee.mall.manager.NewBeeMallGoodsManager;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 用于v1.0 app商品信息相关交互接口
 * 包括商品列表、商品详情等
 * 具体接口信息待与fe确定
 */
@Controller
@RequestMapping("/app/goods")
public class AppGoodsController {

    @Resource
    private NewBeeMallGoodsManager newBeeMallGoodsManager;

    @RequestMapping(value = "/queryGoods", method = RequestMethod.GET)
    @ResponseBody
    public Result queryGoods(AppGoodsQueryDto queryDto) {
        return ResultGenerator.genSuccessDateResult(newBeeMallGoodsManager.queryGoods(queryDto));
    }

    @RequestMapping(value = "/getGoodsInfoById", method = RequestMethod.GET)
    @ResponseBody
    public Result getGoodsInfoById(Long goodsId) {
        if (goodsId == null) {
            return ResultGenerator.genErrorResult(400, "参数错误");
        }
        return ResultGenerator.genSuccessDateResult(newBeeMallGoodsManager.queryGoodsById(goodsId));
    }
}
