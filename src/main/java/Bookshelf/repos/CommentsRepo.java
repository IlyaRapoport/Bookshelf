package Bookshelf.repos;

import Bookshelf.domain.Comments;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentsRepo extends CrudRepository<Comments, Long> {
    List<Comments> findByBookId(Integer id);
    List<Comments> findById(Integer id);

}
