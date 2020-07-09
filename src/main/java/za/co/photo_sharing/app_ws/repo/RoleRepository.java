package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.Role;

import javax.transaction.Transactional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE from users_roles WHERE users_id=:x", nativeQuery = true)
    public void deleteUserRole(@Param("x") Long id);
}
