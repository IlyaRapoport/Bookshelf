package Bookshelf.controller;

import Bookshelf.domain.Books;
import Bookshelf.domain.Comments;
import Bookshelf.domain.DBFile;
import Bookshelf.domain.DBFilePDF;
import Bookshelf.domain.Role;
import Bookshelf.domain.User;
import Bookshelf.repos.BookRepo;
import Bookshelf.repos.CommentsRepo;
import Bookshelf.repos.DBFilePDFRepository;
import Bookshelf.repos.DBFileRepository;
import Bookshelf.repos.StatisticRepo;
import Bookshelf.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
@Service
public class MainController {

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

    @Autowired
    private MainService mainService;

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
    public String main(@AuthenticationPrincipal User user, Map<String, Object> model) throws IOException {
        for (Role key : user.getRoles()) {
            if (key.getAuthority().equals("STATISTIC")) {
                return "redirect:/statistic";
            }
        }
        Iterable<Books> books = bookRepo.findAll(Sort.by("bookName"));
        model.put("books", books);
        model.put("name", user.getUsername());
        model.put("id", user.getId());
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        File folder = new File("upload");
        String[] files = folder.list();

        if (files!=null) {
            for (String file : files) {
                File fileForDelete = new File("upload/" + file);

                fileForDelete.delete();
            }
        }
        folder = new File("src/main/resources/upload");
        files = folder.list();
        for (String file : files) {
            File fileForDelete = new File("src/main/resources/upload/" + file);
            fileForDelete.delete();
        }

        return "main";
    }

    @GetMapping("/add")
    public String add(Map<String, Object> model) {
        Iterable<Books> books = bookRepo.findAll(Sort.by("bookAuthor"));
        model.put("books", books);
        return "add";
    }

    @GetMapping("books/download")
    public String download(@AuthenticationPrincipal User user, Map<String, Object> model) throws IOException {

        return mainService.download(user, model);
    }

    @GetMapping("/books/{id}")
    public String book(@AuthenticationPrincipal User user, @PathVariable Integer id, Map<String, Object> model) throws IOException {
        mainService.getBook(user, id, model);
        return "books";
    }

    @PostMapping("ad")
    public String ad(@RequestParam("filePDF") MultipartFile filePDF, @RequestParam("file") MultipartFile file,
                     @AuthenticationPrincipal Integer id, @RequestParam String bookName, @RequestParam String bookDescription,
                     @AuthenticationPrincipal User user, @RequestParam String bookAuthor, @RequestParam(defaultValue = "") String bookAuthorSelect,
                     Map<String, Object> model) throws IOException, SQLException {
        mainService.addBook(filePDF, file, id, bookName, bookDescription, user, bookAuthor, bookAuthorSelect, model);

        return "redirect:/main";
    }

    @PostMapping("filter")
    public String filter(@AuthenticationPrincipal User user, @RequestParam String filter, Map<String, Object> model) {
        mainService.filter(user, filter, model);
        return "main";
    }

    @PostMapping("books/del")
    public String del(@AuthenticationPrincipal Integer id, Map<String, Object> model) {
        mainService.delete(id, model);
        return "redirect:/main";
    }

    @PostMapping("books/upd")
    public String upd(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName,
                      @RequestParam String bookDescription, @RequestParam String bookAuthorSelect, @AuthenticationPrincipal User user,
                      @RequestParam String bookAuthor, Map<String, Object> model) throws IOException {

        return mainService.update(file, id, bookName, bookDescription, bookAuthorSelect, user, bookAuthor, model);
    }

    @PostMapping("books/comments")
    public String comments(@AuthenticationPrincipal Integer bookId, @AuthenticationPrincipal Integer id, @AuthenticationPrincipal String commentsAuthor, @AuthenticationPrincipal User user, @RequestParam String comments, Map<String, Object> model) {

        return mainService.comments(bookId, id, commentsAuthor, user, comments, model);
    }

    @PostMapping("books/delcom")
    public String delCom(@RequestParam String bookId, @AuthenticationPrincipal Integer id, Map<String, Object> model) {

        return mainService.deleteComments(bookId, id, model);
    }
}