package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.photo_sharing.app_ws.entity.SkillSet;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface SkillSetRepository extends JpaRepository<SkillSet, Long> {
}
