package ltd.newbee.mall.config.wxpay;

public class WxPayConfig {

    // 小程序appid
    public static final String APPID = "wxd7c9a7b80f0b224c";
    // 微信支付的商户id
    public static final String MCH_ID = "1580027451";
    // 微信支付的商户密钥
    public static final String KEY = "ab463a2de2212c761718d8e236e440c8";
    // 支付成功后的服务器回调url
    public static final String NOTIFY_URL = "http://www.e-gouwu.com/payment/wxNotify";
    // 签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    // 交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    // 微信统一下单接口地址
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String API_PAY_KEY = "5befb935587318b44985c3cea4c6f57a";

    // 交易类型，H5支付的固定值为MWEB
    public static final String H5_TRADETYPE = "MWEB";
}
