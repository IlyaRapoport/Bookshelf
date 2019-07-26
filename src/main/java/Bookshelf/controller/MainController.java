package Bookshelf.controller;


import Bookshelf.domain.Books;
import Bookshelf.domain.Comments;
import Bookshelf.domain.Role;
import Bookshelf.domain.User;
import Bookshelf.repos.BookRepo;
import Bookshelf.repos.CommentsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    @Value("${upload.path}")
    private String uploadPath;

    List<Books> booksToEdit;
    List<Comments> commentsToEdit;
    @Autowired
    private BookRepo bookRepo;
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

        return "main";
    }

    @GetMapping("/add")
    public String add(Map<String, Object> model) {
        Iterable<Books> books = bookRepo.findAll();
        model.put("books", books);
        return "add";
    }

    @GetMapping("/books/{id}")
    public String book(@AuthenticationPrincipal User user, @PathVariable Integer id, Map<String, Object> model) {
        Iterable<Books> book;
        book = bookRepo.findById(id);
        model.put("books", book);

        Iterable<Comments> comments;
        comments = commentsRepo.findByBookId(id);
        model.put("comments", comments);

        booksToEdit = (List<Books>) book;
        commentsToEdit = (List<Comments>) comments;
        book = bookRepo.findAll();
        model.put("books2", book);

        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "books";

    }

    @PostMapping("ad")
    public String ad(@RequestParam MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName, @RequestParam String bookDescription, @AuthenticationPrincipal User user, @RequestParam String bookAuthor, @RequestParam(defaultValue = "") String bookAuthorSelect, Map<String, Object> model) throws IOException {
        Books book = new Books(id, bookName, bookAuthor, user, bookDescription);
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            book.setFilename(resultFileName);
        }
        if (bookAuthor != null && !bookAuthor.isEmpty()) {
            book.setBookAuthor(bookAuthor);
        } else {
            if (bookAuthorSelect != null && !bookAuthorSelect.isEmpty()) {
                book.setBookAuthor(bookAuthorSelect);
            }
        }
        bookRepo.save(book);
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
        if (commentsToEdit != null && commentsToEdit.size()!=0) {
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