package com.polarbookshop.catalogservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class HomeController {

    @GetMapping("/")
    public String getGreeting(){
        return String.format("<h1>Greetings %s,</h1><h2>welcome to the book catalog</h2>" +
                        "<h2>The current time is %s</h2>" +
                        "<p>Running with Java %s, %s of %s at %s.</p>" +
                        "<p>OS/Arch/version: %s/%s/%s</p>",

                System.getProperty("user.name"),
                LocalDateTime.now(),
                System.getProperty("java.version"),
                System.getProperty("java.vendor.version"),
                System.getProperty("java.vendor"),
                System.getProperty("java.home"),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"));
    }
}
