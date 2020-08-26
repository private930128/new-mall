package ltd.newbee.mall.manager;

import ltd.newbee.mall.entity.AddressManagement;

import java.util.List;

/**
 * Created by zhanghenan on 2020/6/13.
 */
public interface NewBeeMallAddressManager {

    List<AddressManagement> listAddressInfoByUser(Long userId);

    AddressManagement getDefaultAddressInfoByUser(Long userId);

    int saveAddressManagement(AddressManagement addressManagement);

    int updateAddressManagement(AddressManagement addressManagement);

    void setDefaultAddressManagement(Long id, Long userId);

    AddressManagement getAddressInfoById(Long userId, Long id);

    int deleteAddress(Long userId, Long id);
}
