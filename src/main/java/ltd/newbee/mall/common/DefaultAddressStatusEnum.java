package ltd.newbee.mall.common;

/**
 * Created by zhanghenan on 2020/6/13.
 */
public enum DefaultAddressStatusEnum {

    NOT(0, "NOT"),
    DEFAULT(1, "DEFAULT");

    private int value;

    private String desc;

    DefaultAddressStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}