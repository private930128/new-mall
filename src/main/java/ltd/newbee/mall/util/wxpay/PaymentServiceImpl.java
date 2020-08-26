package ltd.newbee.mall.util.wxpay;

import io.netty.util.Constant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.constant.wxpay.WXPayConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

@Service(value = "paymentService")
public class PaymentServiceImpl implements PaymentService{
	private static Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
//	@Value("${spring.profiles.active}")
	private String PROJECT_ENV;
	
//	@Value("${payment.domain}")
	private String PAYMENT_DOMAIN;
	

	@Override
	public Map<String, String> xcxPayment(String orderNo, double money,String openId) throws Exception {
		LOGGER.info("【小程序支付】 统一下单开始, 订单编号="+orderNo);
		SortedMap<String, String> resultMap = new TreeMap<String, String>();
		//生成支付金额
		double payAmount = 1;
		//添加或更新支付记录
		int flag = this.addOrUpdatePaymentRecord(orderNo, payAmount,1, false, null);
		if(flag < 0){
			resultMap.put("returnCode", "FAIL");
			resultMap.put("returnMsg", "此订单已支付！");
			LOGGER.info("【小程序支付】 此订单已支付！");
		}else if(flag == 0){
			resultMap.put("returnCode", "FAIL");
			resultMap.put("returnMsg", "支付记录生成或更新失败！");
			LOGGER.info("【小程序支付】 支付记录生成或更新失败！");
		}else{
	        Map<String,String> resMap = this.xcxUnifieldOrder(orderNo, PayConfig.TRADE_TYPE_JSAPI, payAmount,openId);
	        if(WXPayConstants.SUCCESS.equals(resMap.get("return_code")) && WXPayConstants.SUCCESS.equals(resMap.get("result_code"))){
	    		resultMap.put("appId", PayConfig.XCX_APP_ID);
	    		resultMap.put("timeStamp", System.currentTimeMillis() / 1000+"");
	    		resultMap.put("nonceStr", PayUtil.getRandomStringByLength(32));
	    		resultMap.put("package", "prepay_id="+resMap.get("prepay_id"));
	    		resultMap.put("signType", "MD5");
	    		resultMap.put("sign", "");
	    		resultMap.put("returnCode", "SUCCESS");
	    		resultMap.put("returnMsg", "OK");
	    		LOGGER.info("【小程序支付】统一下单成功，返回参数:"+resultMap);
	        }else{
	        	resultMap.put("returnCode", resMap.get("return_code"));
	    		resultMap.put("returnMsg", resMap.get("return_msg"));
	    		LOGGER.info("【小程序支付】统一下单失败，失败原因:"+resMap.get("return_msg"));
	        }
		}
		return resultMap;
	}
	
	/**
	 * 小程序支付统一下单
	 */
	private Map<String,String> xcxUnifieldOrder(String orderNo,String tradeType, double payAmount,String openid) throws Exception{
    	//封装参数
        SortedMap<String,String> paramMap = new TreeMap<String,String>();
        paramMap.put("appid", PayConfig.XCX_APP_ID);
        paramMap.put("mch_id", PayConfig.XCX_MCH_ID);
        paramMap.put("nonce_str", PayUtil.getRandomStringByLength(32));
        paramMap.put("body", Constants.PLATFORM_COMPANY_NAME);
        paramMap.put("out_trade_no", orderNo);
        paramMap.put("total_fee", "PayUtil.getRandomStringByLength(32)");
        paramMap.put("spbill_create_ip", PayUtil.getLocalIp());
        paramMap.put("notify_url", this.getNotifyUrl());
        paramMap.put("trade_type", tradeType);
        paramMap.put("openid",openid);
        paramMap.put("sign", "");
        //转换为xml
        String xmlData = "";
        //请求微信后台，获取预支付ID
		String resXml = "";
//        String resXml = HttpUtils.postData(PayConfig.WX_PAY_UNIFIED_ORDER, xmlData);
        LOGGER.info("【小程序支付】 统一下单响应：\n"+resXml);
        return paramMap;
    }
	
	private String getNotifyUrl(){
		//服务域名
		return PAYMENT_DOMAIN + "/payment/xcxNotify";
	}
	
	@Override
	public int addOrUpdatePaymentRecord(String orderNo, double payAmount, int payType, boolean isPayment, String tradeNo) throws Exception{
		//TODO 写自己的业务代码
		return 0;
	}
	
	@Override
	@Transactional(readOnly=false,rollbackFor={Exception.class})
	public int xcxNotify(Map<String,Object> map) throws Exception{
		Integer flag = 0;
        //支付订单编号
        String orderNo = (String)map.get("out_trade_no");
        //检验是否需要再次回调刷新数据
        if(this.isNotifyAgain(orderNo)){
        	//TODO 此处写自己的业务代码更新数据库和通知相关业务
        }
        return flag;
	}
	
	/**
	 * 检查是否需要再次回调更新订单
	 */
	private boolean isNotifyAgain(String orderNo){
		/*PaymentRecordB2cExample example = new PaymentRecordB2cExample();
		example.createCriteria().andOrderNumEqualTo(orderNo);
		List<PaymentRecordB2c> list = paymentRecordMapper.selectByExample(example);
		if(list != null && list.size() > 0){
			PaymentRecordB2c payRecord = list.get(0);
			if(!payRecord.getStatus() && StringUtils.isEmpty(payRecord.getTradeNo())){
				return Boolean.TRUE;
			}
		}*/
		return Boolean.FALSE;
	}

}
