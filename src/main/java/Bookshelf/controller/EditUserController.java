package Bookshelf.controller;

import Bookshelf.domain.User;
import Bookshelf.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/edit")

public class EditUserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{user}")
    public String editUserForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping
    public String userSave(@RequestParam String password, @RequestParam String username, @RequestParam Map<String, String> form, @RequestParam("id") User user) {
        user.setUsername(username);
        if (!password.isEmpty()){
        user.setPassword(password);
        user.setPassword(passwordEncoder.encode(user.getPassword()));}
        userRepo.save(user);
        return "redirect:/main";
    }
}
