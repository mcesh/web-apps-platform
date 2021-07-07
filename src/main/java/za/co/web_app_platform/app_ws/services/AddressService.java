package za.co.web_app_platform.app_ws.services;

import za.co.web_app_platform.app_ws.shared.dto.AddressDTO;
import za.co.web_app_platform.app_ws.shared.dto.UserDto;

public interface AddressService {
    AddressDTO getAddress(String addressId);
    AddressDTO updateUserAddress(String addressId, AddressDTO addressDTO);
    void deleteAddressByAddressId(String addressId);
    UserDto addNewUserAddress(Long userId, AddressDTO addressDTO);
}
