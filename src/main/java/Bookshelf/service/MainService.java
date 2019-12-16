package Bookshelf.service;
import Bookshelf.domain.Books;
import Bookshelf.domain.Comments;
import Bookshelf.domain.DBFile;
import Bookshelf.domain.DBFilePDF;
import Bookshelf.domain.Role;
import Bookshelf.domain.Statistic;
import Bookshelf.domain.User;
import Bookshelf.repos.BookRepo;
import Bookshelf.repos.CommentsRepo;
import Bookshelf.repos.DBFilePDFRepository;
import Bookshelf.repos.DBFileRepository;
import Bookshelf.repos.StatisticRepo;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class MainService {

    List<Books> booksToEdit;
    List<Comments> commentsToEdit;
    List<DBFile> fileToEdit;
    List<DBFilePDF> pdfToEdit;
    @Value("${upload.path}")
    private String uploadPath;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private StatisticRepo statisticRepo;
    @Autowired
    private DBFileRepository fileRepo;
    @Autowired
    private DBFilePDFRepository fileRepoPDF;
    @Autowired
    private CommentsRepo commentsRepo;

    public String download(User user, Map<String, Object> model) throws IOException {

        if (pdfToEdit.size() != 0) {

            File someFile = new File("src/main/resources/upload/pdf.pdf");
            FileOutputStream fos = new FileOutputStream(someFile);
            fos.write(pdfToEdit.get(0).getData());
            fos.flush();
            fos.close();
        }
        Integer bookId = booksToEdit.get(0).getId();

        Date statDate = new Date();
        Long userId = user.getId();

        Statistic statistic = new Statistic(bookId, statDate, userId);
        statistic.setBookId(bookId);
        statistic.setStatDate(statDate);
        statistic.setUserId(userId);

        statisticRepo.save(statistic);
        return "redirect:/books/" + booksToEdit.get(0).getId();
    }

    public void getBook(User user, Integer id, Map<String, Object> model) {
        Iterable<Books> book;
        Iterable<DBFile> file;
        file = fileRepo.findByBookId(id);
        fileToEdit = (List<DBFile>) file;

        Iterable<DBFilePDF> filepdf;
        filepdf = fileRepoPDF.findByBookId(id);
        pdfToEdit = (List<DBFilePDF>) filepdf;

        book = bookRepo.findById(id);
        if (fileToEdit.size() != 0) {
            model.put("img", Base64.encode(fileToEdit.get(0).getData()));
        }

        model.put("books", book);

        Iterable<Comments> comments;
        comments = commentsRepo.findByBookId(id);
        model.put("comments", comments);

        booksToEdit = (List<Books>) book;
        commentsToEdit = (List<Comments>) comments;
        book = bookRepo.findAll();
        model.put("books2", book);

        if (pdfToEdit.size() != 0) {
            model.put("pdf", "/upload/pdf.pdf");
        }
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
    }

    public void addBook(MultipartFile filePDF, MultipartFile file, Integer id, String bookName,
                        String bookDescription, User user, String bookAuthor, String bookAuthorSelect, Map<String, Object> model) throws IOException {
        Books book = new Books(bookName, bookAuthor, user, bookDescription);

        if (bookAuthor != null && !bookAuthor.isEmpty()) {
            book.setBookAuthor(bookAuthor);
        } else {
            if (bookAuthorSelect != null && !bookAuthorSelect.isEmpty()) {
                book.setBookAuthor(bookAuthorSelect);
            }
        }
        bookRepo.save(book);

        if (Objects.requireNonNull(filePDF.getContentType()).contains("pdf")) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + filePDF.getOriginalFilename();
            filePDF.transferTo(new File(uploadPath + "/" + resultFileName));
            book.setFilename(resultFileName);

            File pdf = new File(uploadPath + "/" + resultFileName);
            Integer bookId = book.getId();
            String fileName = resultFileName;
            String fileType = filePDF.getContentType();

            FileInputStream input = new FileInputStream(uploadPath + "/" + resultFileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] data;

            byte[] buff = new byte[2048000];
            for (int readNum; (readNum = input.read(buff)) != -1; ) {
                baos.write(buff, 0, readNum);
            }

            data = baos.toByteArray();

            DBFilePDF dbFilePdf = new DBFilePDF(bookId, fileName, fileType, data);
            dbFilePdf.setBookId(bookId);
            dbFilePdf.setData(data);
            dbFilePdf.setFileName(fileName);
            dbFilePdf.setFileType(fileType);
            fileRepoPDF.save(dbFilePdf);
        }

        if (Objects.requireNonNull(file.getContentType()).contains("image")) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            book.setFilename(resultFileName);

            File image = new File(uploadPath + "/" + resultFileName);
            Integer bookId = book.getId();
            String fileName = resultFileName;
            String fileType = file.getContentType();

            BufferedImage originalImage = ImageIO.read(new File(uploadPath + "/" + resultFileName));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpg", baos);
            baos.flush();
            byte[] data = baos.toByteArray();
            baos.close();

            DBFile dbFile = new DBFile(bookId, fileName, fileType, data);
            dbFile.setBookId(bookId);
            dbFile.setData(data);
            dbFile.setFileName(fileName);
            dbFile.setFileType(fileType);
            fileRepo.save(dbFile);
        }
        Iterable<Books> books = bookRepo.findAll();
        model.put("books", books);
    }

    public void filter(User user, String filter, Map<String, Object> model) {
        model.put("name", user.getUsername());
        Iterable<Books> books;
        if (filter != null && !filter.isEmpty()) {
            books = bookRepo.findByBookName(Sort.by("bookName"), filter);
        } else {
            books = bookRepo.findAll();
        }
        model.put("books", books);
        model.put("id", user.getId());
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
    }

    public void delete(Integer id, Map<String, Object> model) {
        Iterable<Books> books;
        Iterable<Comments> comments;

        fileRepo.deleteAll(fileRepo.findByBookId(booksToEdit.get(0).getId()));
        fileRepoPDF.deleteAll(fileRepoPDF.findByBookId(booksToEdit.get(0).getId()));
        if (commentsToEdit != null && commentsToEdit.size() != 0) {
            comments = commentsRepo.findByBookId(commentsToEdit.get(0).getBookId());
            commentsRepo.deleteAll(comments);
        }
        if (booksToEdit.get(0).getId() != null) {
            books = bookRepo.findById(booksToEdit.get(0).getId());

            bookRepo.deleteAll(books);
        }
    }

    public String update(MultipartFile file, Integer id, String bookName, String bookDescription, String bookAuthorSelect,
                         User user, String bookAuthor, Map<String, Object> model) throws IOException {
        if (bookName != null && !bookName.isEmpty()) {

            booksToEdit.get(0).setBookName(bookName);
        }
        if (bookAuthor != null && !bookAuthor.isEmpty()) {
            booksToEdit.get(0).setBookAuthor(bookAuthor);
        } else {
            if (bookAuthorSelect != null && !bookAuthorSelect.isEmpty()) {
                booksToEdit.get(0).setBookAuthor(bookAuthorSelect);
            }
        }
        if (bookDescription != null && !bookDescription.isEmpty()) {

            booksToEdit.get(0).setBookDescription(bookDescription);
        }
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));

            booksToEdit.get(0).setFilename(resultFileName);
        }
        bookRepo.saveAll(booksToEdit);

        return "redirect:/books/" + booksToEdit.get(0).getId();
    }

    public String comments(Integer bookId, Integer id, String commentsAuthor,
                           User user, String comments, Map<String, Object> model) {
        Comments comment = new Comments(id, comments, commentsAuthor, bookId);
        if (comments != null && !comments.isEmpty()) {
            comment.setBookId(booksToEdit.get(0).getId());
            comment.setComments(comments);
            comment.setCommentsAuthor(user.getUsername());
            commentsRepo.save(comment);
        }

        return "redirect:/books/" + booksToEdit.get(0).getId();
    }

    public String deleteComments(String bookId, Integer id, Map<String, Object> model) {

        Iterable<Comments> comments;
        if (commentsToEdit.get(0).getId() != null) {
            comments = commentsRepo.findById(commentsToEdit.get(0).getId());
            commentsRepo.deleteAll(comments);
        } else {
            comments = commentsRepo.findAll();
        }
        return "redirect:/books/" + booksToEdit.get(0).getId();
    }
}
