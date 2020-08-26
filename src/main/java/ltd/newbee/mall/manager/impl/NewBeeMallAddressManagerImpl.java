package ltd.newbee.mall.manager.impl;

import ltd.newbee.mall.common.DefaultAddressStatusEnum;
import ltd.newbee.mall.entity.AddressManagement;
import ltd.newbee.mall.manager.NewBeeMallAddressManager;
import ltd.newbee.mall.service.AddressManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
@Service
public class NewBeeMallAddressManagerImpl implements NewBeeMallAddressManager {

    @Resource
    private AddressManagementService addressManagementService;

    @Override
    public List<AddressManagement> listAddressInfoByUser(Long userId) {
        return addressManagementService.listAddressManagementByUser(userId);
    }

    @Override
    public AddressManagement getDefaultAddressInfoByUser(Long userId) {
        return addressManagementService.getDefaultAddressByUser(userId);
    }

    @Override
    public int saveAddressManagement(AddressManagement addressManagement) {
        if (addressManagement.getDefaultStatus() == DefaultAddressStatusEnum.DEFAULT.getValue()) {
            addressManagementService.setNotDefaultWhichIsDefault(addressManagement.getUserId());
        }
        return addressManagementService.saveAddressManagement(addressManagement);
    }

    @Override
    public int updateAddressManagement(AddressManagement addressManagement) {
        if (addressManagement.getDefaultStatus() == DefaultAddressStatusEnum.DEFAULT.getValue()) {
            addressManagementService.setNotDefaultWhichIsDefault(addressManagement.getUserId());
        }
        return addressManagementService.updateAddressManagement(addressManagement);
    }

    @Override
    public void setDefaultAddressManagement(Long id, Long userId) {
        // 将原来的默认地址取消
        addressManagementService.setNotDefaultWhichIsDefault(userId);
        // 设置新的默认地址
        addressManagementService.setAddressDefaultOrNot(id, DefaultAddressStatusEnum.DEFAULT);
    }

    @Override
    public AddressManagement getAddressInfoById(Long userId, Long id) {
        return addressManagementService.getAddressInfoById(userId, id);
    }

    @Override
    public int deleteAddress(Long userId, Long id) {
        return addressManagementService.deleteAddress(userId, id);
    }
}
