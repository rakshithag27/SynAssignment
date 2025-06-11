package com.synassignment01.controller;

import com.synassignment01.dto.LoginRequest;
import com.synassignment01.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/synassignment/images")
@RequiredArgsConstructor
public class ImageController {

    @PostMapping("/upload")
    public ResponseEntity upload() {
        return ResponseEntity.ok().body(new String("Image uploaded successfully!"));
    }
}
