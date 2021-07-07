package za.co.web_app_platform.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.web_app_platform.app_ws.entity.AboutPage;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AboutRepository extends JpaRepository<AboutPage, Long> {

    AboutPage findByEmail(String email);
}
