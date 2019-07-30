package Bookshelf.repos;

import Bookshelf.domain.DBFile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {
    List<DBFile> findByBookId(Integer id);
}