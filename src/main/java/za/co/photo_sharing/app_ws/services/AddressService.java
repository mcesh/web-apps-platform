package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses(Long userId);
    AddressDTO getAddress(String addressId);
}
