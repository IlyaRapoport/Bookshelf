package Bookshelf.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity

public class DBFile {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Integer id;
    private String fileName;
    private String fileType;
    private Integer bookId;
    @Lob
    private byte[] data;

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public DBFile() {

    }

    public DBFile(Integer bookId, String fileName, String fileType, byte[] data) {
        this.bookId = bookId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }
}