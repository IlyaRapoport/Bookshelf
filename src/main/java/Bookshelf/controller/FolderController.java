package Bookshelf.controller;

import Bookshelf.domain.User;
import Bookshelf.service.AddFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@Service
public class FolderController {

    @Autowired
    private AddFilesService addFilesService;

    @GetMapping("/addfromfile")
    public String addFromFile(@AuthenticationPrincipal User user) throws IOException {
        addFilesService.addFiles(user);
        return "redirect:/main";
    }
}