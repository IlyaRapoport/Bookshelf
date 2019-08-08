package Bookshelf.repos;

import Bookshelf.domain.Statistic;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface StatisticRepo extends CrudRepository<Statistic, Long> {

    Iterable<Statistic> findAll(Sort by);
    Iterable<Statistic> findById(Integer id);
}
