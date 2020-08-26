package ltd.newbee.mall.util.wxpay;

import java.util.Properties;
//import com.tea.payment.api.contants.PayConstant;

public class PayConfig {

	//微信支付类型
	//NATIVE--原生支付
	//JSAPI--公众号支付-小程序支付
	//MWEB--H5支付
	//APP -- app支付
	public static final String TRADE_TYPE_NATIVE = "NATIVE";
	public static final String TRADE_TYPE_JSAPI = "JSAPI";
	public static final String TRADE_TYPE_MWEB = "MWEB";
	public static final String TRADE_TYPE_APP = "APP";
			
	//微信公众号参数
	public static String WX_APP_ID;
	public static String WX_MCH_ID;
	public static String WX_KEY;
	public static String WX_APP_SECRET;
	//小程序支付参数
	public static String XCX_APP_ID;
	public static String XCX_MCH_ID;
	public static String XCX_KEY;

	//参数
	static{
		Properties properties = new Properties();
		try {
//			properties.load(PayConstant.class.getClassLoader().getResourceAsStream("payment_config.properties"));
			//wx pay
			WX_APP_ID = (String) properties.get("wx.pay.appid");
			WX_MCH_ID = (String) properties.get("wx.pay.mchid");
            WX_KEY = (String) properties.get("wx.pay.key");
            WX_APP_SECRET = (String) properties.get("wx.pay.secret");
            //xcx
            XCX_APP_ID=(String) properties.get("xcx.pay.appid");
            XCX_MCH_ID=(String) properties.get("xcx.pay.mchid");
            XCX_KEY=(String) properties.get("xcx.pay.key");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
