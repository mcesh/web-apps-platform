package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;

import java.util.List;
import java.util.Set;

public interface AddressService {
    AddressDTO getAddress(String addressId);
    AddressDTO updateUserAddress(String addressId, AddressDTO addressDTO);
    void deleteAddressByAddressId(String addressId);
    UserDto addNewUserAddress(Long userId, AddressDTO addressDTO);
}
