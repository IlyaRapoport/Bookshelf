package hello.controller;


import hello.domain.Message;
import hello.domain.User;
import hello.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {
    List<Message> messagesToEdit;
    @Autowired
    private MessageRepo messageRepo;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {

        return "greeting";
    }

    @GetMapping("/delete")
    public String delete(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "delete";
    }

    @GetMapping("/update")
    public String update(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "update";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @GetMapping("/add")
    public String add(Map<String, Object> model) {
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "add";
    }

    @GetMapping("/message/{id}")
    public String message(@PathVariable Integer id, Map<String, Object> model) {
        Iterable<Message> messages;
        messages = messageRepo.findById(id);
        model.put("messages", messages);
        messagesToEdit = (List<Message>) messages;
        return "message";

    }

    @PostMapping("ad")
    public String ad(@AuthenticationPrincipal Integer id, @RequestParam String text, @AuthenticationPrincipal User user, @RequestParam String tag, Map<String, Object> model) {
        Message message = new Message(id, text, tag, user);
        messageRepo.save(message);
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("filter")
    public String filter(@RequestParam String filter, Map<String, Object> model) {
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByText(filter);
        } else {
            messages = messageRepo.findAll();
        }
        model.put("messages", messages);
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
    public String upd(@AuthenticationPrincipal Integer id, @RequestParam String text, @AuthenticationPrincipal User user, @RequestParam String tag, Map<String, Object> model) {

        //  Message message = new Message(id,text, tag, user);
        if (text != null && !text.isEmpty()) {
            messagesToEdit.get(0).setText(text);
        }
        if (tag != null && !tag.isEmpty()) {
            messagesToEdit.get(0).setTag(tag);
        }

        messageRepo.saveAll(messagesToEdit);

        return "redirect:/main";
    }
}