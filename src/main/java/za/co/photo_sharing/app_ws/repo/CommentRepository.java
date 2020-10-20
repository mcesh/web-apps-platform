package za.co.photo_sharing.app_ws.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.photo_sharing.app_ws.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {

}
