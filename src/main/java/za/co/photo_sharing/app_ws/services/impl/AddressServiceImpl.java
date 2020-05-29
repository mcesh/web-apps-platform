package za.co.photo_sharing.app_ws.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.photo_sharing.app_ws.entity.AddressEntity;
import za.co.photo_sharing.app_ws.entity.UserEntity;
import za.co.photo_sharing.app_ws.repo.AddressRepository;
import za.co.photo_sharing.app_ws.repo.UserRepo;
import za.co.photo_sharing.app_ws.services.AddressService;
import za.co.photo_sharing.app_ws.shared.dto.AddressesDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
   private UserRepo userRepo;
    @Autowired
   private AddressRepository addressRepository;
   private ModelMapper modelMapper = new ModelMapper();
    @Override
    public List<AddressesDTO> getAddresses(Long userId) {
        List<AddressesDTO> addressDTOS = new ArrayList<>();

        UserEntity userEntity = userRepo.findByUserId(userId);
        if (userEntity== null) return new ArrayList<>();

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);

        addresses.forEach(addressEntity -> addressDTOS.add(modelMapper.map(addressEntity, AddressesDTO.class)));

        return addressDTOS;
    }
}
