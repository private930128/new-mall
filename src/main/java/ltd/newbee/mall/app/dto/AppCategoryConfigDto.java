package ltd.newbee.mall.app.dto;

/**
 * Created by zhanghenan on 2020/2/17.
 */
public class AppCategoryConfigDto {

    private Long goodsCategoryId;

    private String goodsCategoryName;

    public Long getGoodsCategoryId() {
        return goodsCategoryId;
    }

    public void setGoodsCategoryId(Long goodsCategoryId) {
        this.goodsCategoryId = goodsCategoryId;
    }

    public String getGoodsCategoryName() {
        return goodsCategoryName;
    }

    public void setGoodsCategoryName(String goodsCategoryName) {
        this.goodsCategoryName = goodsCategoryName;
    }
}
