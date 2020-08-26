package ltd.newbee.mall.app.dto;

import ltd.newbee.mall.controller.vo.NewBeeMallShoppingCartItemVO;

import java.util.List;

/**
 * Created by zhanghenan on 2020/4/4.
 */
public class CreateOrderRequest {

    private String token;

    private List<NewBeeMallShoppingCartItemVO> goodsInfo;

    private String phone;

    private String consigneeName;

    private String address;

    private Long addressManagementId;

    private List<Long> cartItemIdList;

    private Integer fromType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<NewBeeMallShoppingCartItemVO> getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(List<NewBeeMallShoppingCartItemVO> goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAddressManagementId() {
        return addressManagementId;
    }

    public void setAddressManagementId(Long addressManagementId) {
        this.addressManagementId = addressManagementId;
    }

    public List<Long> getCartItemIdList() {
        return cartItemIdList;
    }

    public void setCartItemIdList(List<Long> cartItemIdList) {
        this.cartItemIdList = cartItemIdList;
    }

    public Integer getFromType() {
        return fromType;
    }

    public void setFromType(Integer fromType) {
        this.fromType = fromType;
    }
}
