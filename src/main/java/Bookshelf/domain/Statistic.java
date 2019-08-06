package Bookshelf.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.Date;

@Entity
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Integer id;
    private Integer bookId;
    private Date statDate;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public Statistic() {
    }

    public Statistic(Integer bookId, Date statDate, Long userId) {

        this.bookId = bookId;
        this.statDate = statDate;
        this.userId = userId;
    }
}
