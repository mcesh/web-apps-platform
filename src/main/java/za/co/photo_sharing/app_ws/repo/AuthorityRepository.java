package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    Authority findByAuthorityName(String name);
}
