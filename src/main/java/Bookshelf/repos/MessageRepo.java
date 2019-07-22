package Bookshelf.repos;

import Bookshelf.domain.Books;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Books, Long> {

    List<Books> findByBookAuthor(String tag);
    List<Books> findByBookName(String bookName);
    List<Books> findById(Integer id);


}