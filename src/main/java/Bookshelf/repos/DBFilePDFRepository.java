package Bookshelf.repos;

import Bookshelf.domain.DBFilePDF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBFilePDFRepository extends JpaRepository<DBFilePDF, String> {
    List<DBFilePDF> findByBookId(Integer id);
}