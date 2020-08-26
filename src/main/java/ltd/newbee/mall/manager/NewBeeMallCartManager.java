package ltd.newbee.mall.manager;

import java.util.List;
import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;

public interface NewBeeMallCartManager {

    /**
     * 保存商品至购物车中
     *
     * @param newBeeMallShoppingCartItem
     * @return
     */
    String saveNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem, Long userId);

    /**
     * 修改购物车中的属性
     *
     * @param newBeeMallShoppingCartItem
     * @return
     */
    String updateNewBeeMallCartItem(NewBeeMallShoppingCartItem newBeeMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    NewBeeMallShoppingCartItem getNewBeeMallCartItemById(Long newBeeMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param newBeeMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long newBeeMallShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param newBeeMallUserId
     * @return
     */
    List<NewBeeMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId);

    /**
     * 根据勾选查询购物车信息
     * 
     * @param ids
     * @return
     */
    List<NewBeeMallShoppingCartItemVO> getCartByIds(List<Long> ids);

    /**
     * 清空购物车项
     * 
     * @param ids
     * @return
     */
    int deleteBatch(List<Long> ids);

    /**
     * 清空购物车
     * 
     * @param userId
     * @return
     */
    int clearCart(Long userId);
}
