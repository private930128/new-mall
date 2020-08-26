package ltd.newbee.mall.service.impl;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallGoodsVo;
import ltd.newbee.mall.controller.vo.NewBeeMallSearchGoodsVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.service.NewBeeMallGoodsService;
import ltd.newbee.mall.util.BeanUtil;
import ltd.newbee.mall.util.PageQueryUtil;
import ltd.newbee.mall.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NewBeeMallGoodsServiceImpl implements NewBeeMallGoodsService {

    private static Logger LOGGER = LoggerFactory.getLogger(NewBeeMallGoodsServiceImpl.class);

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;


    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        LOGGER.info("--------zhn test--------pageUtil = {}", JSON.toJSON(pageUtil));
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsList(pageUtil);
        LOGGER.info("--------zhn test--------goodsList = {}", JSON.toJSON(goodsList));
        List<NewBeeMallGoodsVo> resultlist = new ArrayList<>();
        for (NewBeeMallGoods goods : goodsList) {
            NewBeeMallGoodsVo newBeeMallGoodsVo = new NewBeeMallGoodsVo();
            newBeeMallGoodsVo.setGoodsId(goods.getGoodsId());
            newBeeMallGoodsVo.setGoodsName(goods.getGoodsName());
            newBeeMallGoodsVo.setGoodsIntro(goods.getGoodsIntro());
            newBeeMallGoodsVo.setGoodsCategoryId(goods.getGoodsCategoryId());
            newBeeMallGoodsVo.setGoodsCoverImg(goods.getGoodsCoverImg());
            newBeeMallGoodsVo.setGoodsCarousel(goods.getGoodsCarousel());
            newBeeMallGoodsVo.setOriginalPrice(new BigDecimal(goods.getOriginalPrice()).divide(new BigDecimal(100)));
            newBeeMallGoodsVo.setSellingPrice(new BigDecimal(goods.getSellingPrice()).divide(new BigDecimal(100)));
            newBeeMallGoodsVo.setStockNum(goods.getStockNum());
            newBeeMallGoodsVo.setTag(goods.getTag());
            newBeeMallGoodsVo.setGoodsSellStatus(goods.getGoodsSellStatus());
            newBeeMallGoodsVo.setCreateTime(goods.getCreateTime());
            newBeeMallGoodsVo.setCreateUser(goods.getCreateUser());
            newBeeMallGoodsVo.setUpdateTime(goods.getUpdateTime());
            newBeeMallGoodsVo.setUpdateUser(goods.getUpdateUser());
            newBeeMallGoodsVo.setGoodsDetailContent(goods.getGoodsDetailContent());
            resultlist.add(newBeeMallGoodsVo);
        }
        int total = goodsMapper.getTotalNewBeeMallGoods(pageUtil);
        PageResult pageResult = new PageResult(resultlist, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNewBeeMallGoods(NewBeeMallGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveNewBeeMallGoods(List<NewBeeMallGoods> newBeeMallGoodsList) {
        if (!CollectionUtils.isEmpty(newBeeMallGoodsList)) {
            goodsMapper.batchInsert(newBeeMallGoodsList);
        }
    }

    @Override
    public String updateNewBeeMallGoods(NewBeeMallGoods goods) {
        NewBeeMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallGoods getNewBeeMallGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchNewBeeMallGoods(PageQueryUtil pageUtil) {
        List<NewBeeMallGoods> goodsList = goodsMapper.findNewBeeMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalNewBeeMallGoodsBySearch(pageUtil);
        List<NewBeeMallSearchGoodsVO> newBeeMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            newBeeMallSearchGoodsVOS = BeanUtil.copyList(goodsList, NewBeeMallSearchGoodsVO.class);
            for (NewBeeMallSearchGoodsVO newBeeMallSearchGoodsVO : newBeeMallSearchGoodsVOS) {
                String goodsName = newBeeMallSearchGoodsVO.getGoodsName();
                String goodsIntro = newBeeMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    newBeeMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    newBeeMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(newBeeMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
