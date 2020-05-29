package za.co.photo_sharing.app_ws.services;

import za.co.photo_sharing.app_ws.shared.dto.AddressesDTO;

import java.util.List;

public interface AddressService {
    List<AddressesDTO> getAddresses(Long userId);
}
