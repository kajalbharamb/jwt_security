package com.example.jwtsecurity.democontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class Controller {

    @GetMapping("/unsecured")
    public ResponseEntity<String> unsecured(){
        return ResponseEntity.ok("Hello from unsecured endpoint");
    }

    @GetMapping("/secured")
    public ResponseEntity<String> sayHello(){
        return ResponseEntity.ok("Hello from secured endpoint");
    }

}
