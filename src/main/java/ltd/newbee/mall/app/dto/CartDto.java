package ltd.newbee.mall.app.dto;

import java.util.List;
import lombok.Data;
import ltd.newbee.mall.entity.NewBeeMallShoppingCartItem;

@Data
public class CartDto {

    private String token;

    private NewBeeMallShoppingCartItem goodsInfo;

    private List<Long> ids;
}
