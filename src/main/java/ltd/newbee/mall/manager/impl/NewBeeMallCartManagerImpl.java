package ltd.newbee.mall.manager.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.app.controller.AppAddressController;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.dao.NewBeeMallShoppingCartItemMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.manager.NewBeeMallCartManager;
import ltd.newbee.mall.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class NewBeeMallCartManagerImpl implements NewBeeMallCartManager {

    @Autowired
    private NewBeeMallShoppingCartItemMapper newBeeMallShoppingCartItemMapper;

    @Autowired
    private NewBeeMallGoodsMapper newBeeMallGoodsMapper;

    private static Logger logger = LoggerFactory.getLogger(NewBeeMallCartManagerImpl.class);

    @Transactional
    @Override
    public String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem,
            Long userId) {
        logger.info("saveNewBeeMallCartItem newBeeMallShoppingCartItem = {}, userId = {}", JSON.toJSON(newBeeMallShoppingCartItem), userId);
        NewBeeMallShoppingCartItem haveCartItem =
                getCartItem(userId, newBeeMallShoppingCartItem.getGoodsId());
        if (haveCartItem != null) {
            haveCartItem.setGoodsCount(haveCartItem.getGoodsCount() + newBeeMallShoppingCartItem.getGoodsCount());
            return updateNewBeeMallCartItem(haveCartItem);
        }
        NewBeeMallGoods newBeeMallGoods =
                newBeeMallGoodsMapper.selectByPrimaryKey(newBeeMallShoppingCartItem.getGoodsId());
        if (newBeeMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        newBeeMallShoppingCartItem.setUserId(userId);
        if (newBeeMallShoppingCartItemMapper.insertSelective(newBeeMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    /**
     * 根据会员id,商品id和规格获取购物车中商品
     */
    private NewBeeMallShoppingCartItem getCartItem(Long userId, Long goodsId) {
        NewBeeMallShoppingCartItem newBeeMallShoppingCartItem =
                newBeeMallShoppingCartItemMapper.selectByUserIdAndGoodsId(userId, goodsId);
        return newBeeMallShoppingCartItem;
    }

    @Transactional
    @Override
    public String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem) {
        NewBeeMallShoppingCartItem newBeeMallShoppingCartItemUpdate =
                newBeeMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItem
                        .getCartItemId());
        if (newBeeMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }

        newBeeMallShoppingCartItemUpdate.setGoodsCount(newBeeMallShoppingCartItem.getGoodsCount());
        newBeeMallShoppingCartItemUpdate.setUpdateTime(new Date());

        if (newBeeMallShoppingCartItemMapper
                .updateByPrimaryKeySelective(newBeeMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public NewBeeMallShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId) {
        return newBeeMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
    }

    @Transactional
    @Override
    public Boolean deleteById(Long id) {
        return newBeeMallShoppingCartItemMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long userId) {
        List<NewBeeMallShoppingCartItem> newBeeMallShoppingCartItems =
                newBeeMallShoppingCartItemMapper.selectByUserId(userId,
                        Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        return getShoppingCartItemList(newBeeMallShoppingCartItems);
    }

    @Override
    public List<NewBeeMallShoppingCartItemVO> getCartByIds(List<Long> ids) {
        List<NewBeeMallShoppingCartItem> newBeeMallShoppingCartItems =
                newBeeMallShoppingCartItemMapper.getCartByIds(ids);
        return getShoppingCartItemList(newBeeMallShoppingCartItems);
    }

    @Transactional
    @Override
    public int deleteBatch(List<Long> ids) {
        return newBeeMallShoppingCartItemMapper.deleteBatch(ids);
    }

    @Transactional
    @Override
    public int clearCart(Long userId) {
        return newBeeMallShoppingCartItemMapper.clearCart(userId);
    }

    private List<NewBeeMallShoppingCartItemVO> getShoppingCartItemList(
            List<NewBeeMallShoppingCartItem> newBeeMallShoppingCartItems) {
        List<NewBeeMallShoppingCartItemVO> newBeeMallShoppingCartItemVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(newBeeMallShoppingCartItems)) {
            // 查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds =
                    newBeeMallShoppingCartItems.stream()
                            .map(NewBeeMallShoppingCartItem::getGoodsId)
                            .collect(Collectors.toList());
            List<NewBeeMallGoods> newBeeMallGoods =
                    newBeeMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, NewBeeMallGoods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(newBeeMallGoods)) {
                newBeeMallGoodsMap =
                        newBeeMallGoods.stream().collect(
                                Collectors.toMap(NewBeeMallGoods::getGoodsId, Function.identity(),
                                        (entity1, entity2) -> entity1));
            }
            for (NewBeeMallShoppingCartItem newBeeMallShoppingCartItem : newBeeMallShoppingCartItems) {
                NewBeeMallShoppingCartItemVO newBeeMallShoppingCartItemVO =
                        new NewBeeMallShoppingCartItemVO();
                BeanUtil.copyProperties(newBeeMallShoppingCartItem, newBeeMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(newBeeMallShoppingCartItem.getGoodsId())) {
                    NewBeeMallGoods newBeeMallGoodsTemp =
                            newBeeMallGoodsMap.get(newBeeMallShoppingCartItem.getGoodsId());
                    newBeeMallShoppingCartItemVO.setGoodsCoverImg(newBeeMallGoodsTemp
                            .getGoodsCoverImg());
                    String goodsName = newBeeMallGoodsTemp.getGoodsName();

                    newBeeMallShoppingCartItemVO.setGoodsName(goodsName);
                    newBeeMallShoppingCartItemVO.setSellingPrice(newBeeMallGoodsTemp
                            .getSellingPrice());
                    newBeeMallShoppingCartItemVO.setRealSellingPrice(new BigDecimal(newBeeMallGoodsTemp
                            .getSellingPrice()).divide(new BigDecimal("100")));
                    newBeeMallShoppingCartItemVOS.add(newBeeMallShoppingCartItemVO);
                }
            }
        }
        return newBeeMallShoppingCartItemVOS;
    }


}
