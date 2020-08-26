package ltd.newbee.mall.properties;

import lombok.Data;
import ltd.newbee.mall.util.smsUtil.AesUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "sms")
@Data
public class SmsProperties {

    private String sign;
    private String appkey;
    private String key;
    private Integer templateId;

    public String requestParam(String mobile, String code) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("phone=").append(mobile).append("&");
        buffer.append("msgContent=").append(sign).append("您的验证码是").append(code).append("，在10分钟内有效。如非本人操作请忽略此短信").append("&");
        buffer.append("appkey=").append(appkey).append("&");
        buffer.append("templateId=").append(templateId);
        try {
            String encryptBase64 = AesUtils.encryptBase64(buffer.toString(), key);
            return encryptBase64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}