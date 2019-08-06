package Bookshelf.service;

import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
public interface BookStatistic {
    public List<Object> finishStaticCount(Date from, Date till) throws ParseException;
}
