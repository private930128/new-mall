package ltd.newbee.mall.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import ltd.newbee.mall.app.dto.PaymentRequestDto;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.common.PayStatusEnum;
import ltd.newbee.mall.common.PaymentStatusEnum;
import ltd.newbee.mall.common.ServiceResultEnum;
import ltd.newbee.mall.config.wxpay.WxPayConfig;
import ltd.newbee.mall.dao.NewBeeMallOrderMapper;
import ltd.newbee.mall.dao.PaymentJournalMapper;
import ltd.newbee.mall.entity.NewBeeMallOrder;
import ltd.newbee.mall.entity.PaymentJournal;
import ltd.newbee.mall.manager.NewBeeMallOrderManager;
import ltd.newbee.mall.service.PaymentService;
import ltd.newbee.mall.util.DateUtil;
import ltd.newbee.mall.util.OrderUtil;
import ltd.newbee.mall.util.SnowflakeUtil;
import ltd.newbee.mall.util.wxpay.PayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    @Autowired
    private PaymentJournalMapper paymentJournalDao;

    @Autowired
    private NewBeeMallOrderManager mallOrderManager;

    @Autowired
    private NewBeeMallOrderMapper newBeeMallOrderMapper;

    @Override
    public PaymentJournal buildPaymentJournal(Long userId, String nickName, String payAppId,
                                              String payCode, String merchantId, String merchantOrderNo, String desc,
                                              Integer payAmount) throws NewBeeMallException {
        // 首先该userId下面这个商户订单号是否已经存在
        PaymentJournal where = new PaymentJournal();
        where.setMerchantOrderNo(merchantOrderNo);
        where.setUserId(userId);
        List<PaymentJournal> paymentJournal = paymentJournalDao.select(where);

        long payCount =
                paymentJournal
                        .stream()
                        .filter(p -> p.getPayStatus() == PaymentStatusEnum.PAY.getIndex()
                                || p.getPayStatus() == PaymentStatusEnum.REFUND.getIndex()
                                || p.getPayStatus() == PaymentStatusEnum.WAIT_PAY.getIndex())
                        .count();
        if (payCount > 0) {
            throw new NewBeeMallException(ServiceResultEnum.ORDER_IS_PAYED.getResult());
        }

        long notPayCount =
                paymentJournal
                        .stream()
                        .filter(p -> p.getPayStatus() == PaymentStatusEnum.CREATE_ORDER.getIndex()
                                || p.getPayStatus() == PaymentStatusEnum.WAIT_PAY.getIndex())
                        .count();
        if (notPayCount > 0) {
            return paymentJournal
                    .stream()
                    .filter(p -> p.getPayStatus() == PaymentStatusEnum.CREATE_ORDER.getIndex()
                            || p.getPayStatus() == PaymentStatusEnum.WAIT_PAY.getIndex())
                    .collect(Collectors.toList()).get(0);
        }

        if (payAmount == null) {
            payAmount = 0;
        }

        PaymentJournal insertPaymentJournal = new PaymentJournal();
        insertPaymentJournal.setPaymentJournalId(SnowflakeUtil.getInstance().nextId());
        insertPaymentJournal.setPaymentDealNo(OrderUtil.generatePaymentDealNo());
        insertPaymentJournal.setUserId(userId);
        insertPaymentJournal.setUserName(nickName);
        insertPaymentJournal.setPayAppId(payAppId);
        insertPaymentJournal.setMerchantId(merchantId);
        insertPaymentJournal.setMerchantOrderNo(merchantOrderNo);
        insertPaymentJournal.setPaymentDealId(null);
        insertPaymentJournal.setPayStatus(PaymentStatusEnum.CREATE_ORDER.getIndex());
        insertPaymentJournal.setPayCode(payCode);
        insertPaymentJournal.setPayAmount(payAmount);
        insertPaymentJournal.setAccountAmount(0);
        insertPaymentJournal.setMedicareAmount(0);
        insertPaymentJournal.setInsuranceAmount(0);
        insertPaymentJournal.setTotalAmount(insertPaymentJournal.getPayAmount()
                + insertPaymentJournal.getInsuranceAmount());
        insertPaymentJournal.setDescription(desc);
        insertPaymentJournal.setExtraParams(null);
        insertPaymentJournal.setCreateTime(DateUtil.dateToString(new Date()));
        insertPaymentJournal.setDataSource("web");

        try {
            paymentJournalDao.insert(insertPaymentJournal);
        } catch (Exception ex) {
            throw new NewBeeMallException(ServiceResultEnum.INSRT_PAYMENT_JOURNAL_ERROR.getResult());
        }
        return insertPaymentJournal;
    }

    @Transactional
    @Override
    public void updatePaymentJournalMoney(Long paymentJournalId, Integer payAmount,
                                          Integer payStatus) {
        PaymentJournal updatePaymentJournal = new PaymentJournal();
        updatePaymentJournal.setPaymentJournalId(paymentJournalId);
        updatePaymentJournal.setPayStatus(payStatus);
        updatePaymentJournal.setPayAmount(payAmount);
        updatePaymentJournal.setTotalAmount(payAmount);
        try {
            paymentJournalDao.update(updatePaymentJournal);
        } catch (Exception ex) {
            throw new NewBeeMallException(
                    ServiceResultEnum.UPDATE_PAYMENT_JOURNAL_ERROR.getResult());
        }
    }

    @Override
    public PaymentJournal getPaymentJournalById(Long paymentJournalId) {
        return paymentJournalDao.selectById(String.valueOf(paymentJournalId));
    }

    @Override
    public PaymentJournal getPaymentJournalByNo(String paymentDealNo) {
        PaymentJournal wheres = new PaymentJournal();
        wheres.setPaymentDealNo(paymentDealNo);
        List<PaymentJournal> paymentJournalList = paymentJournalDao.select(wheres);
        if (CollectionUtils.isEmpty(paymentJournalList)) {
            return null;
        }
        return paymentJournalList.get(0);
    }

    @Override
    public void updatePaymentJoural(PaymentJournal paymentJournal) {
        paymentJournalDao.update(paymentJournal);
    }

    @Override
    public PaymentJournal buildRefundPaymentJournal(PaymentJournal paymentJournal)
            throws NewBeeMallException {
        PaymentJournal insertPaymentJournal = new PaymentJournal();
        insertPaymentJournal.setPaymentJournalId(SnowflakeUtil.getInstance().nextId());
        insertPaymentJournal.setPaymentDealNo(OrderUtil.generatePaymentDealNo());
        insertPaymentJournal.setUserId(paymentJournal.getUserId());
        insertPaymentJournal.setUserName(paymentJournal.getUserName());
        insertPaymentJournal.setPayAppId(paymentJournal.getPayAppId());
        insertPaymentJournal.setMerchantId(paymentJournal.getMerchantId());
        insertPaymentJournal.setMerchantOrderNo(paymentJournal.getMerchantOrderNo());
        insertPaymentJournal.setPaymentDealId(null);
        insertPaymentJournal.setPayStatus(PaymentStatusEnum.WAIT_REFUND.getIndex());
        insertPaymentJournal.setPayCode(paymentJournal.getPayCode());
        insertPaymentJournal.setPayAmount(paymentJournal.getPayAmount());
        insertPaymentJournal.setAccountAmount(paymentJournal.getAccountAmount());
        insertPaymentJournal.setMedicareAmount(paymentJournal.getMedicareAmount());
        insertPaymentJournal.setInsuranceAmount(paymentJournal.getInsuranceAmount());
        insertPaymentJournal.setTotalAmount(paymentJournal.getTotalAmount());
        insertPaymentJournal.setDescription("正常退费");
        insertPaymentJournal.setExtraParams(null);
        insertPaymentJournal.setCreateTime(DateUtil.dateToString(new Date()));
        insertPaymentJournal.setDataSource("refund");

        try {
            paymentJournalDao.insert(insertPaymentJournal);
        } catch (Exception ex) {
            throw new NewBeeMallException(ServiceResultEnum.INSRT_PAYMENT_JOURNAL_ERROR.getResult());
        }
        return insertPaymentJournal;
    }

    @Override
    public Map<String, Object> paywxr(String openId, String orderNo) {
        try {
            // 生成的随机字符串
            String nonce_str = PayUtil.getRandomStringByLength(32);
            // 商品名称
            String body = "商品名称";
            // 获取客户端的ip地址
            String spbill_create_ip = PayUtil.getLocalIp(); // 获得终端IP

            //TODO price
            NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
            if (newBeeMallOrder == null) {
                return null;
            }
            int price1 = newBeeMallOrder.getTotalPrice();
            String p = price1 + "";
            // 组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.APPID);
            packageParams.put("mch_id", WxPayConfig.MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", orderNo);// 商户订单号
            packageParams.put("total_fee", p);// 支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.NOTIFY_URL);// 支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.TRADETYPE);// 支付方式
            packageParams.put("openid", openId);
            log.info("packageParams = {}", JSON.toJSON(packageParams));
            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            log.info("prestr = {}", JSON.toJSON(packageParams));
            // MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String sign = PayUtil.sign(prestr, WxPayConfig.API_PAY_KEY, "utf-8").toUpperCase();

            // 拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml =
                    "<xml>" + "<appid>" + WxPayConfig.APPID + "</appid>" + "<body><![CDATA[" + body
                            + "]]></body>" + "<mch_id>" + WxPayConfig.MCH_ID + "</mch_id>"
                            + "<nonce_str>" + nonce_str + "</nonce_str>" + "<notify_url>"
                            + WxPayConfig.NOTIFY_URL + "</notify_url>" + "<openid>" + openId
                            + "</openid>" + "<out_trade_no>" + orderNo + "</out_trade_no>"
                            + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                            + "<total_fee>" + p + "</total_fee>" + "<trade_type>"
                            + WxPayConfig.TRADETYPE + "</trade_type>" + "<sign>" + sign + "</sign>"
                            + "</xml>";

            log.info("paywxr request xml：" + xml);

            // 调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest(WxPayConfig.PAY_URL, "POST", xml);

            log.info("paywxr response xml：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);

            String return_code = (String) map.get("return_code");// 返回状态码

            Map<String, Object> response = new HashMap<String, Object>();// 返回给小程序端需要的参数
            if (return_code.equals("SUCCESS")) {
                String prepay_id = (String) map.get("prepay_id");// 返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");// 这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                // 拼接签名需要的参数
                String stringSignTemp =
                        "appId=" + WxPayConfig.APPID + "&nonceStr=" + nonce_str
                                + "&package=prepay_id=" + prepay_id + "&signType=" + WxPayConfig.SIGNTYPE + "&timeStamp="
                                + timeStamp;
                // 再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign =
                        PayUtil.sign(stringSignTemp, WxPayConfig.API_PAY_KEY, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }

            response.put("appid", WxPayConfig.APPID);

            //TODO 写支付前的业务代码
            this.savePayLog(orderNo, PaymentStatusEnum.WAIT_PAY.getIndex(), price1);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> h5Paywxr(String orderNo) {
        try {
            // 生成的随机字符串
            String nonce_str = PayUtil.getRandomStringByLength(32);
            // 商品名称
            String body = "商品名称";
            // 获取客户端的ip地址
            String spbill_create_ip = PayUtil.getLocalIp(); // 获得终端IP

            //TODO price
            NewBeeMallOrder newBeeMallOrder = newBeeMallOrderMapper.selectByOrderNo(orderNo);
            if (newBeeMallOrder == null) {
                return null;
            }
            int price1 = newBeeMallOrder.getTotalPrice();
            String p = price1 + "";
            // 组装参数，用户生成统一下单接口的签名
            Map<String, String> packageParams = new HashMap<String, String>();
            packageParams.put("appid", WxPayConfig.APPID);
            packageParams.put("mch_id", WxPayConfig.MCH_ID);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("body", body);
            packageParams.put("out_trade_no", orderNo);// 商户订单号
            packageParams.put("total_fee", p);// 支付金额，这边需要转成字符串类型，否则后面的签名会失败
            packageParams.put("spbill_create_ip", spbill_create_ip);
            packageParams.put("notify_url", WxPayConfig.NOTIFY_URL);// 支付成功后的回调地址
            packageParams.put("trade_type", WxPayConfig.H5_TRADETYPE);// 支付方式
            log.info("packageParams = {}", JSON.toJSON(packageParams));
            String prestr = PayUtil.createLinkString(packageParams); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
            log.info("prestr = {}", JSON.toJSON(packageParams));
            // MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String sign = PayUtil.sign(prestr, WxPayConfig.API_PAY_KEY, "utf-8").toUpperCase();

            // 拼接统一下单接口使用的xml数据，要将上一步生成的签名一起拼接进去
            String xml =
                    "<xml>" + "<appid>" + WxPayConfig.APPID + "</appid>" + "<body><![CDATA[" + body
                            + "]]></body>" + "<mch_id>" + WxPayConfig.MCH_ID + "</mch_id>"
                            + "<nonce_str>" + nonce_str + "</nonce_str>" + "<notify_url>"
                            + WxPayConfig.NOTIFY_URL + "</notify_url>" + "<out_trade_no>" + orderNo + "</out_trade_no>"
                            + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                            + "<total_fee>" + p + "</total_fee>" + "<trade_type>"
                            + WxPayConfig.H5_TRADETYPE + "</trade_type>" + "<sign>" + sign + "</sign>"
                            + "</xml>";

            log.info("paywxr request xml：" + xml);

            // 调用统一下单接口，并接受返回的结果
            String result = PayUtil.httpRequest(WxPayConfig.PAY_URL, "POST", xml);

            log.info("paywxr response xml：" + result);

            // 将解析结果存储在HashMap中
            Map map = PayUtil.doXMLParse(result);

            String return_code = (String) map.get("return_code");// 返回状态码

            Map<String, Object> response = new HashMap<String, Object>();// 返回给小程序端需要的参数
            if (return_code.equals("SUCCESS")) {
                String prepay_id = (String) map.get("prepay_id");// 返回的预付单信息
                String mweb_url = (String) map.get("mweb_url");// 返回的预付单信息
                response.put("nonceStr", nonce_str);
                response.put("package", "prepay_id=" + prepay_id);
                response.put("mweb_url", mweb_url + "&redirect_url=" + "http%3a%2f%2fwww.e-gouwu.com%2f%2f%23%2forderDetail%2f" + orderNo + "%3frefresh%3d1");
                response.put("Referer", "http://www.e-gouwu.com/");
                Long timeStamp = System.currentTimeMillis() / 1000;
                response.put("timeStamp", timeStamp + "");// 这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                // 拼接签名需要的参数
                String stringSignTemp =
                        "appId=" + WxPayConfig.APPID + "&nonceStr=" + nonce_str
                                + "&package=prepay_id=" + prepay_id + "&signType=" + WxPayConfig.SIGNTYPE + "&timeStamp="
                                + timeStamp;
                // 再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign =
                        PayUtil.sign(stringSignTemp, WxPayConfig.API_PAY_KEY, "utf-8").toUpperCase();

                response.put("paySign", paySign);
            }

            response.put("appid", WxPayConfig.APPID);

            //TODO 写支付前的业务代码
            this.savePayLog(orderNo, PaymentStatusEnum.WAIT_PAY.getIndex(), price1);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PaymentJournal savePayLog(String merchantOrderNo, Integer payStatus, Integer payAmount) {
        PaymentJournal paymentJournal = new PaymentJournal();
        paymentJournal.setPaymentJournalId(SnowflakeUtil.getInstance().nextId());
        paymentJournal.setPaymentDealNo(OrderUtil.generatePaymentDealNo());
        paymentJournal.setMerchantOrderNo(merchantOrderNo);
        paymentJournal.setPayStatus(payStatus);
        paymentJournal.setPayAmount(payAmount);
        paymentJournal.setAccountAmount(payAmount);
        paymentJournal.setMedicareAmount(payAmount);
        paymentJournal.setInsuranceAmount(payAmount);
        paymentJournal.setTotalAmount(payAmount);
        paymentJournal.setCreateTime(DateUtil.dateToString(new Date()));

        paymentJournalDao.insert(paymentJournal);

        return paymentJournal;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void payResult(String merchantOrderNo, Integer payStatus, Integer payAmount) {

        if (payStatus == 1) {
            mallOrderManager.completeOrderPayment(merchantOrderNo, PayStatusEnum.PAY_SUCCESS);
        }
        this.savePayLog(merchantOrderNo, payStatus, payAmount);
    }

    public static void main(String[] args) {
        String s = "5befb935587318b44985c3cea4c6f57a";
        System.out.println(s.length());
    }

}
