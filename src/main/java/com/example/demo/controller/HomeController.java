package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "message",
                "Hello Teeramet Saikham"
        );
        model.addAttribute(
                 "studentId",
                 "673380273-9"
        );

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute(
            "ShortIntroduce",
            "Hi Teeramet 673380273-9 Sec.2"
        );

        return "about";
    }
}