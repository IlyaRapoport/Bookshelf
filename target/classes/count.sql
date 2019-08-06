SELECT  * FROM bookshelf.statistic;
SELECT DISTINCT book_id FROM bookshelf.statistic;
SELECT COUNT( book_id) FROM bookshelf.statistic  where book_id=145;
SELECT * FROM bookshelf.statistic WHERE stat_date BETWEEN (31)  AND  (31 );