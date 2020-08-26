package ltd.newbee.mall.service.impl;

import com.google.common.collect.Lists;
import ltd.newbee.mall.common.DefaultAddressStatusEnum;
import ltd.newbee.mall.dao.AddressManagementMapper;
import ltd.newbee.mall.entity.AddressManagement;
import ltd.newbee.mall.entity.AddressManagementExample;
import ltd.newbee.mall.service.AddressManagementService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
@Service
public class AddressManagementServiceImpl implements AddressManagementService {

    @Resource
    private AddressManagementMapper addressManagementMapper;

    @Override
    public int saveAddressManagement(AddressManagement addressManagement) {
        if (addressManagement == null || addressManagement.getUserId() == null
                || StringUtils.isEmpty(addressManagement.getAddress()) || StringUtils.isEmpty(addressManagement.getConsigneeName())
                || StringUtils.isEmpty(addressManagement.getPhone())) {
            return 0;
        }
        return addressManagementMapper.insertSelective(addressManagement);
    }

    @Override
    public int updateAddressManagement(AddressManagement addressManagement) {
        if (addressManagement == null || addressManagement.getId() == null) {
            return 0;
        }
        return addressManagementMapper.updateByPrimaryKeySelective(addressManagement);
    }

    @Override
    public boolean setAddressDefaultOrNot(Long id, DefaultAddressStatusEnum defaultAddressStatusEnum) {
        AddressManagement addressManagement = new AddressManagement();
        addressManagement.setDefaultStatus(defaultAddressStatusEnum.getValue());
        addressManagement.setId(id);
        return addressManagementMapper.updateByPrimaryKeySelective(addressManagement) > 0;
    }

    @Override
    public AddressManagement getDefaultAddressByUser(Long userId) {
        AddressManagementExample example = new AddressManagementExample();
        example.createCriteria().andUserIdEqualTo(userId).andDefaultStatusEqualTo(DefaultAddressStatusEnum.DEFAULT.getValue());
        List<AddressManagement> list = addressManagementMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public List<AddressManagement> listAddressManagementByUser(Long userId) {
        AddressManagementExample example = new AddressManagementExample();
        example.createCriteria().andUserIdEqualTo(userId);
        example.setOrderByClause("id desc");
        List<AddressManagement> list = addressManagementMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list;
    }

    @Override
    public boolean setNotDefaultWhichIsDefault(Long userId) {
        AddressManagementExample example = new AddressManagementExample();
        example.createCriteria().andUserIdEqualTo(userId).andDefaultStatusEqualTo(DefaultAddressStatusEnum.DEFAULT.getValue());
        AddressManagement addressManagement = new AddressManagement();
        addressManagement.setDefaultStatus(DefaultAddressStatusEnum.NOT.getValue());
        return addressManagementMapper.updateByExampleSelective(addressManagement, example) > 0;
    }

    @Override
    public AddressManagement getAddressInfoById(Long userId, Long id) {
        AddressManagementExample example = new AddressManagementExample();
        example.createCriteria().andUserIdEqualTo(userId).andIdEqualTo(id);
        List<AddressManagement> list = addressManagementMapper.selectByExample(example);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public int deleteAddress(Long userId, Long id) {
        AddressManagementExample example = new AddressManagementExample();
        example.createCriteria().andUserIdEqualTo(userId).andIdEqualTo(id);
        return addressManagementMapper.deleteByExample(example);
    }

}
