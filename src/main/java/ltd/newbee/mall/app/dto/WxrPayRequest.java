package ltd.newbee.mall.app.dto;

/**
 * Created by zhanghenan on 2020/4/11.
 */
public class WxrPayRequest {

    private String token;

    private String orderNo;

    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
