package ltd.newbee.mall.service;


import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import ltd.newbee.mall.app.dto.PaymentRequestDto;
import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.entity.PaymentJournal;

public interface PaymentService {

    /**
     * 新增支付流水
     *
     * @param userId
     * @param nickName
     * @param payAppId
     * @param payCode
     * @param merchantId
     * @param merchantOrderNo
     * @param desc
     * @param payAmount
     * @return
     * @throws NewBeeMallException
     */
    PaymentJournal buildPaymentJournal(Long userId, String nickName, String payAppId,
                                       String payCode, String merchantId, String merchantOrderNo, String desc,
                                       Integer payAmount) throws NewBeeMallException;

    PaymentJournal savePayLog(String merchantOrderNo, Integer payStatus, Integer payAmount);

    /**
     * 更新支付流水的金额
     *
     * @param paymentJournalId
     * @param payAmount
     */
    void updatePaymentJournalMoney(Long paymentJournalId, Integer payAmount, Integer payStatus);

    /**
     * 根据Id查询支付流水
     *
     * @param paymentJournalId
     * @return
     */
    PaymentJournal getPaymentJournalById(Long paymentJournalId);

    /**
     * 支付流水No
     *
     * @param paymentDealNo
     * @return
     */
    PaymentJournal getPaymentJournalByNo(String paymentDealNo);

    /**
     * 更新paymentjournal
     *
     * @param paymentJournal
     */
    void updatePaymentJoural(PaymentJournal paymentJournal);


    PaymentJournal buildRefundPaymentJournal(PaymentJournal paymentJournal)
            throws NewBeeMallException;


    Map<String, Object> paywxr(String openId, String orderNo);

    Map<String, Object> h5Paywxr(String orderNo);

    void payResult(String merchantOrderNo, Integer payStatus, Integer payAmount);

}