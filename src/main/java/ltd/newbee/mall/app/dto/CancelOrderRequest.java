package ltd.newbee.mall.app.dto;

/**
 * Created by zhanghenan on 2020/4/4.
 */
public class CancelOrderRequest {

    private String token;

    private String orderNo;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
