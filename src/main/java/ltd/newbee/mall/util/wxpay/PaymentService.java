package ltd.newbee.mall.util.wxpay;

import java.util.Map;
/**
 * 
* <p>ClassName: PaymentB2cService.java </p>
* <p>请添加描述信息</p>
* 
* @author lft  
* @date 2018年6月27日
* @since jdk1.8
* @version 1.0
 */
public interface PaymentService {

	Map<String,String> xcxPayment(String orderNum, double money, String openId) throws Exception;
	
	int addOrUpdatePaymentRecord(String orderNum, double payAmount, int payType, boolean isPayment,
      String tradeNo) throws Exception;
	
	int xcxNotify(Map<String, Object> map) throws Exception;
}
