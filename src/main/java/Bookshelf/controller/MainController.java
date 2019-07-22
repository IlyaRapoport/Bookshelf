package Bookshelf.controller;


import Bookshelf.domain.Books;
import Bookshelf.domain.Role;
import Bookshelf.domain.User;
import Bookshelf.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    List<Books> messagesToEdit;
    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {

        return "redirect:/main";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<Books> messages = messageRepo.findAll();
        model.put("messages", messages);
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "delete";
    }

    @GetMapping("/update")
    public String update(Map<String, Object> model) {
        Iterable<Books> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "update";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<Books> messages = messageRepo.findAll();
        model.put("messages", messages);

        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }

        return "main";
    }

    @GetMapping("/add")
    public String add(Map<String, Object> model) {
        Iterable<Books> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "add";
    }

    @GetMapping("/message/{id}")
    public String message(@AuthenticationPrincipal User user, @PathVariable Integer id, Map<String, Object> model) {
        Iterable<Books> messages;
        messages = messageRepo.findById(id);
        model.put("messages", messages);


        messagesToEdit = (List<Books>) messages;
        messages = messageRepo.findAll();
        model.put("messages2", messages);
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "message";

    }

    @PostMapping("ad")
    public String ad(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName, @AuthenticationPrincipal User user, @RequestParam String bookAuthor, Map<String, Object> model) throws IOException {
        Books message = new Books(id, bookName, bookAuthor, user);
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            message.setFilename(resultFileName);
        }
        messageRepo.save(message);
        Iterable<Books> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "redirect:/main";
    }

    @PostMapping("filter")
    public String filter(@AuthenticationPrincipal User user, @RequestParam String filter, Map<String, Object> model) {
        Iterable<Books> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByBookName(filter);
        } else {
            messages = messageRepo.findAll();
        }
        model.put("messages", messages);
        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "main";
    }

    @PostMapping("message/del")
    public String del(@AuthenticationPrincipal Integer id, Map<String, Object> model) {
        Iterable<Books> messages;
        if (messagesToEdit.get(0).getId() != null) {
            messages = messageRepo.findById(messagesToEdit.get(0).getId());
            messageRepo.deleteAll(messages);
        } else {
            messages = messageRepo.findAll();
        }

        return "redirect:/main";
    }

    @PostMapping("message/upd")
    public String upd(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String bookName, @AuthenticationPrincipal User user, @RequestParam String bookAuthor, Map<String, Object> model) throws IOException {

        if (bookName != null && !bookName.isEmpty()) {

            messagesToEdit.get(0).setBookName(bookName);
        }
        if (bookAuthor != null && !bookAuthor.isEmpty()) {
            messagesToEdit.get(0).setBookAuthor(bookAuthor);
        }
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));

            messagesToEdit.get(0).setFilename(resultFileName);
        }
        messageRepo.saveAll(messagesToEdit);

        return "redirect:/main";
    }
    @PostMapping("message/updselect")
    public String updSelect(@RequestParam String bookAuthor){


        if (bookAuthor != null && !bookAuthor.isEmpty()) {
            messagesToEdit.get(0).setBookAuthor(bookAuthor);
        }
        messageRepo.saveAll(messagesToEdit);

        return "redirect:/main";
    }
}