package Bookshelf.repos;

import Bookshelf.domain.Books;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookRepo extends CrudRepository<Books, Long> {

    List<Books> findByBookName(Sort by, String bookName);
    List<Books> findByBookName(String bookName);
    List<Books> findById(Integer id);

    Iterable<Books> findAll(Sort by);
}