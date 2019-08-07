package Bookshelf.service;

import Bookshelf.domain.Statistic;
import Bookshelf.repos.StatisticRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookStatisticImpl implements BookStatistic {

    private String bookId;
    private Integer count;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BookStatisticImpl() {

    }

    public BookStatisticImpl(String bookId, Integer count) {
        this.bookId = bookId;
        this.count = count;
    }

    @Autowired
    private StatisticRepo statisticRepo;

    @Override
    public List<Object> finishStaticCount(Date from, Date till) throws ParseException {

        Calendar c = Calendar.getInstance();
        c.setTime(till);

        c.add(Calendar.HOUR, 23);
        c.add(Calendar.MINUTE, 59);
        c.add(Calendar.SECOND, 59);
        till = c.getTime();

        Map<String, Integer> statModel = new HashMap<>();

        Iterable<Statistic> statistic = statisticRepo.findAll(Sort.by("bookId"));
        for (Statistic statValue : statistic) {

            String key = statValue.getBookId().toString();
            Date newDate = statValue.getStatDate();

            if (newDate.compareTo(from) >= 0 && newDate.compareTo(till) <= 0) {
                if (statModel.containsKey(statValue.getBookId().toString())) {
                    statModel.put(key, statModel.get(key) + 1);
                } else {
                    statModel.put(key, 1);
                }
            }
        }
        List<Object> listAnything = new ArrayList<Object>();

        for (Map.Entry<String, Integer> entry : statModel.entrySet()) {
            setBookId(entry.getKey());
            setCount(entry.getValue());
            BookStatisticImpl ResultStatistics = new BookStatisticImpl(bookId, count);
            listAnything.add(ResultStatistics);
        }
        return listAnything;
    }
}