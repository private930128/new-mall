package ltd.newbee.mall.app.dto;

/**
 * Created by zhanghenan on 2020/2/15.
 */
public class AppGoodsQueryDto {

    private Long goodsId;

    private Long categoryId;

    private Integer pageNum;

    private Integer pageSize;

    private Integer pageStartNum;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageStartNum() {
        return pageStartNum;
    }

    public void setPageStartNum(Integer pageStartNum) {
        this.pageStartNum = pageStartNum;
    }
}
