package com.dodo.jwtreactspring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MainController {

    @GetMapping("")
    public String main() {
        return "MAIN CONTROLLER";
    }

    @GetMapping("test")
    public String test() {
        return "USER ROLE TEST CONTROLLER";
    }
}
