package ltd.newbee.mall.controller.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单列表页面VO
 */
public class NewBeeMallOrderListVO implements Serializable {

    private Long orderId;

    private String orderNo;

    private String totalPrice;

    private Byte payType;

    private Byte orderStatus;

    private String orderStatusString;

    private String userAddress;

    private String createTime;

    private String payTime;

    private List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS;

    private Long userId;

    private String recipient;

    private String phone;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Byte getPayType() {
        return payType;
    }

    public void setPayType(Byte payType) {
        this.payType = payType;
    }

    public Byte getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Byte orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusString() {
        return orderStatusString;
    }

    public void setOrderStatusString(String orderStatusString) {
        this.orderStatusString = orderStatusString;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public List<NewBeeMallOrderItemVO> getNewBeeMallOrderItemVOS() {
        return newBeeMallOrderItemVOS;
    }

    public void setNewBeeMallOrderItemVOS(List<NewBeeMallOrderItemVO> newBeeMallOrderItemVOS) {
        this.newBeeMallOrderItemVOS = newBeeMallOrderItemVOS;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
