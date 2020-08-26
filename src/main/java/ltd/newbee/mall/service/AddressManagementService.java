package ltd.newbee.mall.service;

import ltd.newbee.mall.common.DefaultAddressStatusEnum;
import ltd.newbee.mall.entity.AddressManagement;

import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
public interface AddressManagementService {

    int saveAddressManagement(AddressManagement addressManagement);

    int updateAddressManagement(AddressManagement addressManagement);

    boolean setAddressDefaultOrNot(Long id, DefaultAddressStatusEnum defaultAddressStatusEnum);

    AddressManagement getDefaultAddressByUser(Long userId);

    List<AddressManagement> listAddressManagementByUser(Long userId);

    boolean setNotDefaultWhichIsDefault(Long userId);

    AddressManagement getAddressInfoById(Long userId, Long id);

    int deleteAddress(Long userId, Long id);
}
