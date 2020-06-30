package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(String name);
}
