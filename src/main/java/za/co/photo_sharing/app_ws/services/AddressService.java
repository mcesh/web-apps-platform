package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;

import java.util.List;
import java.util.Set;

public interface AddressService {
    Set<AddressDTO> getAddresses(Long userId);
    AddressDTO getAddress(String addressId);
    AddressDTO updateUserAddress(String addressId, AddressDTO addressDTO);
}
