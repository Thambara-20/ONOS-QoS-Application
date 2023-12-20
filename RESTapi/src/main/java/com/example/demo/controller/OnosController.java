package com.example.demo.controller;

import com.example.demo.services.OnosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OnosController {
    @Autowired
    private OnosService service;

    @PostMapping("/onos")
    public ResponseEntity create() {
        return ResponseEntity.ok("Successfully created.");
    }

    @GetMapping("/onos")
    public ResponseEntity get() {
        return ResponseEntity.ok("Provide users");
    }

    @DeleteMapping("/onos")
    public ResponseEntity delete() {
        return ResponseEntity.ok("Deleting onos");
    }
}
