package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.AddressEntity;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.AddressRepository;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.AddressService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private Utils utils;
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<AddressDTO> getAddresses(Long userId) {
        List<AddressDTO> addressDTOS = new ArrayList<>();

        UserEntity userEntity = userRepo.findByUserId(userId);
        if (userEntity == null) return addressDTOS;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        addresses.forEach(addressEntity -> addressDTOS.add(modelMapper.map(addressEntity, AddressDTO.class)));

        return addressDTOS;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = new AddressDTO();

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null) {
            returnValue = modelMapper.map(addressEntity, AddressDTO.class);
        }
        return returnValue;
    }

    @Override
    public AddressDTO updateUserAddress(String addressId, AddressDTO addressDTO) {
        AddressEntity byAddressId = addressRepository.findByAddressId(addressId);
        if (Objects.isNull(byAddressId))
            throw new UserServiceException(ErrorMessages.EMAIL_ADDRESS_NOT_FOUND.getErrorMessage());
        byAddressId.setType(addressDTO.getType());
        byAddressId.setStreetName(addressDTO.getStreetName());
        byAddressId.setPostalCode(addressDTO.getPostalCode());
        byAddressId.setCountry(addressDTO.getCountry());
        byAddressId.setCity(addressDTO.getCity());
        byAddressId.setAddressId(addressId);
        AddressEntity storedAddress = addressRepository.save(byAddressId);
        return modelMapper.map(storedAddress, AddressDTO.class);
    }
}
