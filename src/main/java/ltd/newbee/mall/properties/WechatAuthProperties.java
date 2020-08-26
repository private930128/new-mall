package ltd.newbee.mall.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "auth.wechat")
@Data
public class WechatAuthProperties {

    private String appId;

    private String secret;

    private String grantType;
}