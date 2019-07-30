package Bookshelf.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Integer id;
    private String comments;
    private String commentsAuthor;
    private Integer bookId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCommentsAuthor() {
        return commentsAuthor;
    }

    public void setCommentsAuthor(String commentsAuthor) {
        this.commentsAuthor = commentsAuthor;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Comments() {
    }

    public Comments(Integer id, String comments, String commentsAuthor, Integer bookId) {

        this.id = id;
        this.comments = comments;
        this.commentsAuthor = commentsAuthor;
        this.bookId = bookId;
    }
}
