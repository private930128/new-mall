package ltd.newbee.mall.manager;

import java.util.List;
import ltd.newbee.mall.app.dto.CartPromotionItem;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;


public interface PromotionManager {

    /**
     * 计算购物车中的促销活动
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<NewBeeMallShoppingCartItem> cartItemList);

}
