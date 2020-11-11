package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.About;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AboutRepository extends JpaRepository<About, Long> {

    About findByEmail(String email);
}
