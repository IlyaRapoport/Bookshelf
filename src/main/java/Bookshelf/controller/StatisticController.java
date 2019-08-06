package Bookshelf.controller;

import Bookshelf.repos.StatisticRepo;
import Bookshelf.service.BookStatistic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/statistic")
@PreAuthorize("hasAuthority('STATISTIC')")

public class StatisticController {

    List<Object> statisticFinishResult;
    @Autowired
    private StatisticRepo statisticRepo;
    @Autowired
    BookStatistic bookStatistic;

    @GetMapping
    public String statistic(Map<String, Object> model) {
        model.put("stat", statisticFinishResult);
        return "statistic";
    }

    @PostMapping("/date")
    public String date(@RequestParam(defaultValue = "0-0-0") String from, @RequestParam(defaultValue = "0-0-0") String till) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        statisticFinishResult = bookStatistic.finishStaticCount(sdf.parse(from), sdf.parse(till));

        return "redirect:/statistic";
    }
}