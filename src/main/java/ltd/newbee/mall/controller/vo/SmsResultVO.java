package ltd.newbee.mall.controller.vo;

import lombok.Data;
import lombok.ToString;
import ltd.newbee.mall.util.Result;

/**
 * 短信返回结果
 */
@Data
@ToString
public class SmsResultVO {
    private String code;
    private String msg;
    private String result;
}
