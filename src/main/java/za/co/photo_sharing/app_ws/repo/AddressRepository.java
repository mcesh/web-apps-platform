package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.AddressEntity;
import za.co.photo_sharing.app_ws.entity.UserEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    Set<AddressEntity> findAllByUserDetails(UserEntity userEntity);
    AddressEntity findByAddressId(String addressId);
    Set<AddressEntity> findByUserId(Long userId);
}
