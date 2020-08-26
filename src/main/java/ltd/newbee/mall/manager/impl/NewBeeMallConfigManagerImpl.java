package ltd.newbee.mall.manager.impl;

import com.google.common.collect.Lists;
import ltd.newbee.mall.app.dto.AppCategoryConfigDto;
import ltd.newbee.mall.dao.GoodsCategoryMapper;
import ltd.newbee.mall.entity.GoodsCategory;
import ltd.newbee.mall.manager.NewBeeMallConfigManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhanghenan on 2020/2/17.
 */
@Service
public class NewBeeMallConfigManagerImpl implements NewBeeMallConfigManager {

    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public List<AppCategoryConfigDto> listCategoryConfig() {
        List<GoodsCategory> list = goodsCategoryMapper.listCategoryInfo();
        return buildAppCategoryConfigDto(list);
    }

    private List<AppCategoryConfigDto> buildAppCategoryConfigDto(List<GoodsCategory> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<AppCategoryConfigDto> resultList = Lists.newArrayListWithCapacity(list.size());
        for (GoodsCategory goodsCategory : list) {
            AppCategoryConfigDto appCategoryConfigDto = new AppCategoryConfigDto();
            appCategoryConfigDto.setGoodsCategoryId(goodsCategory.getCategoryId());
            appCategoryConfigDto.setGoodsCategoryName(goodsCategory.getCategoryName());
            resultList.add(appCategoryConfigDto);
        }
        return resultList;
    }
}
