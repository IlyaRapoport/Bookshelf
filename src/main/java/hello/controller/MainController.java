package hello.controller;


import com.mysql.cj.x.protobuf.MysqlxCrud;
import hello.domain.Message;
import hello.domain.Role;
import hello.domain.User;
import hello.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
import java.sql.*;
import java.util.Scanner;

@Controller
public class MainController {

    @Value("${upload.path}")
    private String uploadPath;

    List<Message> messagesToEdit;
    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {

        return "redirect:/main";
    }

    @GetMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String delete(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
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
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "update";
    }

    @GetMapping("/main")
    public String main(@AuthenticationPrincipal User user, Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
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
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "add";
    }

    @GetMapping("/message/{id}")
    public String message(@AuthenticationPrincipal User user, @PathVariable Integer id, Map<String, Object> model) {
        Iterable<Message> messages;
        messages = messageRepo.findById(id);
        model.put("messages", messages);
        messagesToEdit = (List<Message>) messages;

        for (Role key : user.getRoles()) {
            if (key.getAuthority().contains("ADMIN")) {
                model.put("user", user);
            }
        }
        return "message";

    }

    @PostMapping("ad")
    public String ad(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String text, @AuthenticationPrincipal User user, @RequestParam String tag, Map<String, Object> model) throws IOException {
        Message message = new Message(id, text, tag, user);
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
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("filter")
    public String filter(@AuthenticationPrincipal User user, @RequestParam String filter, Map<String, Object> model) {
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByText(filter);
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

    @PostMapping("del")
    public String del(@RequestParam Integer filter, Map<String, Object> model) {
        Iterable<Message> messages;
        if (filter != null) {
            messages = messageRepo.findById(filter);
            messageRepo.deleteAll(messages);
        } else {
            messages = messageRepo.findAll();
        }

        return "redirect:/main";
    }

    @PostMapping("message/upd")
    public String upd(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Integer id, @RequestParam String text, @AuthenticationPrincipal User user, @RequestParam String tag, Map<String, Object> model) throws IOException {

        //  Message message = new Message(id,text, tag, user);
        if (text != null && !text.isEmpty()) {
            messagesToEdit.get(0).setText(text);
        }
        if (tag != null && !tag.isEmpty()) {
            messagesToEdit.get(0).setTag(tag);
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
}