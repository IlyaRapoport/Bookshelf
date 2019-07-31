package Bookshelf.controller;

import Bookshelf.domain.*;
import Bookshelf.repos.BookRepo;
import Bookshelf.repos.CommentsRepo;
import Bookshelf.repos.DBFilePDFRepository;
import Bookshelf.repos.DBFileRepository;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@Service
public class MainController {

    @Value("${upload.path}")
    private String uploadPath;

    List<Books> booksToEdit;
    List<Comments> commentsToEdit;
    List<DBFile> fileToEdit;
    List<DBFilePDF> pdfToEdit;
    @Autowired
    private BookRepo bookRepo;
    @Autowired
    private DBFileRepository fileRepo;
    @Autowired
    private DBFilePDFRepository fileRepoPDF;
    @Autowired
    private CommentsRepo commentsRepo;

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public String accessDeniedException() {
        return "redirect:/main";
    }

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {

        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<Books> books = bookRepo.findAll();
        model.put("books", books);
        model.put("name", user.getUsername());
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        File folder = new File("upload");
        String[] files = folder.list();
        for (String file : files) {
            String fileForDeleteName = file;
            File fileForDelete = new File("upload/" + fileForDeleteName);
            fileForDelete.delete();
        }
         folder = new File("src/main/resources/upload");
        files = folder.list();
        for (String file : files) {
            String fileForDeleteName = file;
            File fileForDelete = new File("src/main/resources/upload/" + fileForDeleteName);
            fileForDelete.delete();
        }

        return "main";
    }

    @GetMapping("/add")
    public String add(Map<String, Object> model) {
        Iterable<Books> books = bookRepo.findAll();
        model.put("books", books);
        return "add";
    }

    @GetMapping("books/download")
    public String download(Map<String, Object> model) throws IOException {
        if (pdfToEdit.size() != 0) {

            File someFile = new File("src/main/resources/upload/pdf.pdf");
            FileOutputStream fos = new FileOutputStream(someFile);
            fos.write(pdfToEdit.get(0).getData());
            fos.flush();
            fos.close();
        }
        String address = "redirect:/books/" + booksToEdit.get(0).getId();
        return address;
    }

    @GetMapping("/books/{id}")
    public String book(@AuthenticationPrincipal User user, @PathVariable Integer id, Map<String, Object> model) throws IOException {
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
        return "books";
    }

    @PostMapping("ad")
    public String ad(@RequestParam("filePDF") MultipartFile filePDF, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName, @RequestParam String bookDescription, @AuthenticationPrincipal User user, @RequestParam String bookAuthor, @RequestParam(defaultValue = "") String bookAuthorSelect, Map<String, Object> model) throws IOException, SQLException {
        Books book = new Books(id, bookName, bookAuthor, user, bookDescription);

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

        return "redirect:/main";
    }

    @PostMapping("filter")
    public String filter(@AuthenticationPrincipal User user, @RequestParam String filter, Map<String, Object> model) {
        model.put("name", user.getUsername());
        Iterable<Books> books;
        if (filter != null && !filter.isEmpty()) {
            books = bookRepo.findByBookName(filter);
        } else {
            books = bookRepo.findAll();
        }
        model.put("books", books);
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "main";
    }

    @PostMapping("books/del")
    public String del(@AuthenticationPrincipal Integer id, Map<String, Object> model) {
        Iterable<Books> books;
        Iterable<Comments> comments;
        if (commentsToEdit != null && commentsToEdit.size() != 0) {
            comments = commentsRepo.findByBookId(commentsToEdit.get(0).getBookId());
            commentsRepo.deleteAll(comments);
        }
        if (booksToEdit.get(0).getId() != null) {
            books = bookRepo.findById(booksToEdit.get(0).getId());

            bookRepo.deleteAll(books);
        }

        return "redirect:/main";
    }

    @PostMapping("books/upd")
    public String upd(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName, @RequestParam String bookDescription, @RequestParam String bookAuthorSelect, @AuthenticationPrincipal User user, @RequestParam String bookAuthor, Map<String, Object> model) throws IOException {

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

        String address = "redirect:/books/" + booksToEdit.get(0).getId();
        return address;
    }

    @PostMapping("books/comments")
    public String comments(@AuthenticationPrincipal Integer bookId, @AuthenticationPrincipal Integer id, @AuthenticationPrincipal String commentsAuthor, @AuthenticationPrincipal User user, @RequestParam String comments, Map<String, Object> model) {
        Comments comment = new Comments(id, comments, commentsAuthor, bookId);
        if (comments != null && !comments.isEmpty()) {
            comment.setBookId(booksToEdit.get(0).getId());
            comment.setComments(comments);
            comment.setCommentsAuthor(user.getUsername());
            commentsRepo.save(comment);
        }

        String address = "redirect:/books/" + booksToEdit.get(0).getId();
        return address;
    }

    @PostMapping("books/delcom")
    public String delCom(@RequestParam String bookId, @AuthenticationPrincipal Integer id, Map<String, Object> model) {
        Iterable<Comments> comments;
        if (commentsToEdit.get(0).getId() != null) {
            comments = commentsRepo.findById(commentsToEdit.get(0).getId());
            commentsRepo.deleteAll(comments);
        } else {
            comments = commentsRepo.findAll();
        }
        String address = "redirect:/books/" + booksToEdit.get(0).getId();
        return address;
    }
}