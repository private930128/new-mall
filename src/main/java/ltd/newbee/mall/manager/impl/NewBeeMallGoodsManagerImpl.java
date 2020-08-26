package ltd.newbee.mall.manager.impl;

import ltd.newbee.mall.app.dto.AppGoodsInfoDto;
import ltd.newbee.mall.app.dto.AppGoodsQueryDto;
import ltd.newbee.mall.dao.NewBeeMallGoodsMapper;
import ltd.newbee.mall.entity.NewBeeMallGoods;
import ltd.newbee.mall.manager.NewBeeMallGoodsManager;
import ltd.newbee.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghenan on 2020/2/15.
 */
@Service
public class NewBeeMallGoodsManagerImpl implements NewBeeMallGoodsManager {

    @Autowired
    private NewBeeMallGoodsMapper goodsMapper;

    @Override
    public List<AppGoodsInfoDto> queryGoods(AppGoodsQueryDto queryDto) {
        if (queryDto.getPageSize() == null) {
            queryDto.setPageSize(20);
        }
        if (queryDto.getPageNum() == null) {
            queryDto.setPageSize(1);
        }
        Integer pageStartNum = (queryDto.getPageNum() - 1) * queryDto.getPageSize();
        queryDto.setPageStartNum(pageStartNum);
        List<NewBeeMallGoods> list = goodsMapper.queryGoods(queryDto);
        List<AppGoodsInfoDto> resultList = new ArrayList<>();
        for (NewBeeMallGoods newBeeMallGoods : list) {
            AppGoodsInfoDto appGoodsInfoDto = convert2AppGoodsInfoDto(newBeeMallGoods);
            resultList.add(appGoodsInfoDto);
        }
        return resultList;
    }

    @Override
    public AppGoodsInfoDto queryGoodsById(Long goodsId) {
        NewBeeMallGoods newBeeMallGoods = goodsMapper.selectById(goodsId);
        if (newBeeMallGoods == null) {
            return null;
        }
        return convert2AppGoodsInfoDto(newBeeMallGoods);
    }

    private AppGoodsInfoDto convert2AppGoodsInfoDto(NewBeeMallGoods newBeeMallGoods) {
        if (newBeeMallGoods == null) {
            return null;
        }
        AppGoodsInfoDto appGoodsInfoDto = new AppGoodsInfoDto();
        BigDecimal result = new BigDecimal(newBeeMallGoods.getSellingPrice()).divide(new BigDecimal(100));
        appGoodsInfoDto.setSellingPrice(result.toString());
        appGoodsInfoDto.setGoodsName(newBeeMallGoods.getGoodsName());
        appGoodsInfoDto.setGoodsId(newBeeMallGoods.getGoodsId());
        appGoodsInfoDto.setGoodsCoverImg(newBeeMallGoods.getGoodsCoverImg());
        appGoodsInfoDto.setGoodsCategoryId(newBeeMallGoods.getGoodsCategoryId());
        appGoodsInfoDto.setGoodsIntro(newBeeMallGoods.getGoodsIntro());
        appGoodsInfoDto.setTag(newBeeMallGoods.getTag());
        appGoodsInfoDto.setGoodsDetailContent(newBeeMallGoods.getGoodsDetailContent());
        return appGoodsInfoDto;
    }
}
