package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.web_app_platform.app_ws.entity.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    Authority findByAuthorityName(String name);
}
