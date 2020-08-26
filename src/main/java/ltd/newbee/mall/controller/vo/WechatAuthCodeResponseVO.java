package ltd.newbee.mall.controller.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WechatAuthCodeResponseVO {
    private String session_key;
    private String openid;
    private String unionid;
}
