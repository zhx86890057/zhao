package com.zhao.upms.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;

@RestController
public class TestController {



    @GetMapping("/test")
//    @PreAuthorize("hasRole('admin')")
    public int test(Principal user){

        return 1;
    }

}
