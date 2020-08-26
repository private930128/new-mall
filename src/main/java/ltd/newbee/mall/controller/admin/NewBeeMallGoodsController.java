package ltd.newbee.mall.controller.admin;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.NewBeeMallCategoryLevelEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallGoodsVo;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallCategoryService;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.service.impl.NewBeeMallGoodsServiceImpl;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.Result;
import ltd.newbee.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Controller
@RequestMapping("/admin")
public class NewBeeMallGoodsController {

    private static Logger LOGGER = LoggerFactory.getLogger(NewBeeMallGoodsController.class);

    @Resource
    private NewBeeMallGoodsService newBeeMallGoodsService;
    @Resource
    private NewBeeMallCategoryService newBeeMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "admin/newbee_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/newbee_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") String goodsId) {
        if (!isNumericzidai(goodsId)) {
            return "admin/newbee_mall_goods_edit";
        }
        request.setAttribute("path", "edit");
        NewBeeMallGoods newBeeMallGoods = newBeeMallGoodsService.getNewBeeMallGoodsById(Long.valueOf(goodsId));
        if (newBeeMallGoods == null) {
            return "error/error_400";
        }
        if (newBeeMallGoods.getGoodsCategoryId() > 0) {
            if (newBeeMallGoods.getGoodsCategoryId() != null || newBeeMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = newBeeMallCategoryService.getGoodsCategoryById(newBeeMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = newBeeMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = newBeeMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (newBeeMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), NewBeeMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = newBeeMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), NewBeeMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        NewBeeMallGoodsVo newBeeMallGoodsVo = new NewBeeMallGoodsVo();
        newBeeMallGoodsVo.setGoodsId(newBeeMallGoods.getGoodsId());
        newBeeMallGoodsVo.setGoodsName(newBeeMallGoods.getGoodsName());
        newBeeMallGoodsVo.setGoodsIntro(newBeeMallGoods.getGoodsIntro());
        newBeeMallGoodsVo.setGoodsCategoryId(newBeeMallGoods.getGoodsCategoryId());
        newBeeMallGoodsVo.setGoodsCoverImg(newBeeMallGoods.getGoodsCoverImg());
        newBeeMallGoodsVo.setGoodsCarousel(newBeeMallGoods.getGoodsCarousel());
        newBeeMallGoodsVo.setOriginalPrice(new BigDecimal(newBeeMallGoods.getOriginalPrice()).divide(new BigDecimal(100)));
        newBeeMallGoodsVo.setSellingPrice(new BigDecimal(newBeeMallGoods.getSellingPrice()).divide(new BigDecimal(100)));
        newBeeMallGoodsVo.setStockNum(newBeeMallGoods.getStockNum());
        newBeeMallGoodsVo.setTag(newBeeMallGoods.getTag());
        newBeeMallGoodsVo.setGoodsSellStatus(newBeeMallGoods.getGoodsSellStatus());
        newBeeMallGoodsVo.setCreateTime(newBeeMallGoods.getCreateTime());
        newBeeMallGoodsVo.setCreateUser(newBeeMallGoods.getCreateUser());
        newBeeMallGoodsVo.setUpdateTime(newBeeMallGoods.getUpdateTime());
        newBeeMallGoodsVo.setUpdateUser(newBeeMallGoods.getUpdateUser());
        newBeeMallGoodsVo.setGoodsDetailContent(newBeeMallGoods.getGoodsDetailContent());
        request.setAttribute("goods", newBeeMallGoodsVo);
        request.setAttribute("path", "goods-edit");
        return "admin/newbee_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        LOGGER.info("--------zhn test--------params = {}", JSON.toJSON(params));
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessDateResult(newBeeMallGoodsService.getNewBeeMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody NewBeeMallGoodsVo newBeeMallGoodsVo) {
        if (StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getTag())
                || Objects.isNull(newBeeMallGoodsVo.getOriginalPrice())
                || Objects.isNull(newBeeMallGoodsVo.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoodsVo.getStockNum())
                || Objects.isNull(newBeeMallGoodsVo.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = newBeeMallGoodsService.saveNewBeeMallGoods(convert2NewBeeMallGoods(newBeeMallGoodsVo));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody NewBeeMallGoodsVo newBeeMallGoodsVo) {
        if (Objects.isNull(newBeeMallGoodsVo.getGoodsId())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsName())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsIntro())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getTag())
                || Objects.isNull(newBeeMallGoodsVo.getOriginalPrice())
                || Objects.isNull(newBeeMallGoodsVo.getGoodsCategoryId())
                || Objects.isNull(newBeeMallGoodsVo.getStockNum())
                || Objects.isNull(newBeeMallGoodsVo.getGoodsSellStatus())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsCoverImg())
                || StringUtils.isEmpty(newBeeMallGoodsVo.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = newBeeMallGoodsService.updateNewBeeMallGoods(convert2NewBeeMallGoods(newBeeMallGoodsVo));
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        NewBeeMallGoods goods = newBeeMallGoodsService.getNewBeeMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessDateResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (newBeeMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

    private NewBeeMallGoods convert2NewBeeMallGoods(NewBeeMallGoodsVo newBeeMallGoodsVo) {
        NewBeeMallGoods newBeeMallGoods = new NewBeeMallGoods();
        newBeeMallGoods.setGoodsId(newBeeMallGoodsVo.getGoodsId());
        newBeeMallGoods.setGoodsName(newBeeMallGoodsVo.getGoodsName());
        newBeeMallGoods.setGoodsIntro(newBeeMallGoodsVo.getGoodsIntro());
        newBeeMallGoods.setGoodsCategoryId(newBeeMallGoodsVo.getGoodsCategoryId());
        newBeeMallGoods.setGoodsCoverImg(newBeeMallGoodsVo.getGoodsCoverImg());
        newBeeMallGoods.setGoodsCarousel(newBeeMallGoodsVo.getGoodsCarousel());
        newBeeMallGoods.setOriginalPrice(newBeeMallGoodsVo.getOriginalPrice().multiply(new BigDecimal(100)).intValue());
        newBeeMallGoods.setSellingPrice(newBeeMallGoodsVo.getSellingPrice().multiply(new BigDecimal(100)).intValue());
        newBeeMallGoods.setStockNum(newBeeMallGoodsVo.getStockNum());
        newBeeMallGoods.setTag(newBeeMallGoodsVo.getTag());
        newBeeMallGoods.setGoodsSellStatus(newBeeMallGoodsVo.getGoodsSellStatus());
        newBeeMallGoods.setCreateTime(newBeeMallGoodsVo.getCreateTime());
        newBeeMallGoods.setCreateUser(newBeeMallGoodsVo.getCreateUser());
        newBeeMallGoods.setUpdateTime(newBeeMallGoodsVo.getUpdateTime());
        newBeeMallGoods.setUpdateUser(newBeeMallGoodsVo.getUpdateUser());
        newBeeMallGoods.setGoodsDetailContent(newBeeMallGoodsVo.getGoodsDetailContent());
        return newBeeMallGoods;
    }

    public static boolean isNumericzidai(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}