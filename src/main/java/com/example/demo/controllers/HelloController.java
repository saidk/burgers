package com.example.demo.controllers;

import com.example.demo.models.Photo;
import com.example.demo.service.BurgerPhotosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
public class HelloController {
    @Autowired
    BurgerPhotosService burgerPhotosService;

    @RequestMapping("/")
    public String welcome(Model model) {
        List<Photo> photos = burgerPhotosService.getBurgerPhotos();
        model.addAttribute("photos", photos);
        return "welcome";
    }

}
