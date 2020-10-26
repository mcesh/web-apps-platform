package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.AddressEntity;
import za.co.photo_sharing.app_ws.entity.UserProfile;
import za.co.photo_sharing.app_ws.exceptions.UserServiceException;
import za.co.photo_sharing.app_ws.model.response.ErrorMessages;
import za.co.photo_sharing.app_ws.repo.AddressRepository;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.AddressService;
import za.co.photo_sharing.app_ws.services.UserService;
import za.co.photo_sharing.app_ws.shared.dto.AddressDTO;
import za.co.photo_sharing.app_ws.shared.dto.UserDto;
import za.co.photo_sharing.app_ws.utility.Utils;

import javax.transaction.Transactional;
import java.util.*;

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
    public UserDto addNewUserAddress(Long userId, AddressDTO addressDTO) {
        UserProfile userById = userRepo.findByUserId(userId);
        if (Objects.isNull(userById)) throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.USER_NOT_FOUND.getErrorMessage());
        userById.setAddress(buildAddresses(addressDTO, userById));
        UserProfile storedUserAddress = userRepo.save(userById);
        return modelMapper.map(storedUserAddress,UserDto.class);
    }

    @Transactional
    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = new AddressDTO();

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null) {
            returnValue = modelMapper.map(addressEntity, AddressDTO.class);
        }
        return returnValue;
    }

    @Transactional
    @Override
    public AddressDTO updateUserAddress(String addressId, AddressDTO addressDTO) {
        AddressEntity byAddressId = addressRepository.findByAddressId(addressId);
        if (Objects.isNull(byAddressId))
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.EMAIL_ADDRESS_NOT_FOUND.getErrorMessage());
        byAddressId.setType(addressDTO.getType());
        byAddressId.setStreetName(addressDTO.getStreetName());
        byAddressId.setPostalCode(addressDTO.getPostalCode());
        byAddressId.setCountry(addressDTO.getCountry());
        byAddressId.setCity(addressDTO.getCity());
        byAddressId.setAddressId(addressId);
        AddressEntity storedAddress = addressRepository.save(byAddressId);
        return modelMapper.map(storedAddress, AddressDTO.class);
    }

    @Override
    public void deleteAddressByAddressId(String addressId) {
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (Objects.isNull(addressEntity)){
            throw new UserServiceException(HttpStatus.NOT_FOUND,ErrorMessages.ADDRESS_NOT_FOUND.getErrorMessage());
        }
        addressRepository.delete(addressEntity);
    }

    private AddressEntity buildAddresses(AddressDTO addressDTO, UserProfile user) {
        AddressEntity address = new AddressEntity();
        address.setAddressId(utils.generateAddressId(30));
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setStreetName(addressDTO.getStreetName());
        address.setType(addressDTO.getType());
        address.setUserId(user.getUserId());
        address.setUserDetails(user);
        return address;
    }
}
