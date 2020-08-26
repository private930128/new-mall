package ltd.newbee.mall.manager.impl;

import java.util.ArrayList;
import java.util.List;
import ltd.newbee.mall.app.dto.CartPromotionItem;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;
import ltd.newbee.mall.manager.PromotionManager;

public class PromotionManagerImpl implements PromotionManager {

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<NewBeeMallShoppingCartItem> cartItemList) {
        // 查询所有商品的优惠相关信息
        // 根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        return cartPromotionItemList;
    }
}
