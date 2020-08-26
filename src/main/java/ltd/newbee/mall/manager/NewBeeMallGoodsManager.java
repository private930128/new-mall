package ltd.newbee.mall.manager;

import ltd.newbee.mall.app.dto.AppGoodsInfoDto;
import ltd.newbee.mall.app.dto.AppGoodsQueryDto;
import ltd.newbee.mall.entity.NewBeeMallGoods;

import java.util.List;

/**
 * Created by zhanghenan on 2020/2/15.
 */
public interface NewBeeMallGoodsManager {

    List<AppGoodsInfoDto> queryGoods(AppGoodsQueryDto queryDto);

    AppGoodsInfoDto queryGoodsById(Long goodsId);

}
