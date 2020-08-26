package ltd.newbee.mall.app.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("短信请求对象")
@Data
public class AppMsgDto {

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 短信类型
     */
    @ApiModelProperty("短信类型")
    private int msgType;

}
