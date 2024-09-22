package com.files.controller;


import com.files.service.S3_Bucket_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class AwsController {

    @Autowired
    private S3_Bucket_Service service;

    @PostMapping("/upload/image")
    public String uploadImage(@RequestParam MultipartFile file) throws IOException, InterruptedException {
        service.imageUpload(file);
        return "file uploaded successfully !!";
    }


    @DeleteMapping("/delete/{fileName}")
    public String deleteFile(@PathVariable String fileName) {
        service.deleteFile(fileName);
        return "File deleted successfully!";
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] data = service.downloadFile(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .body(data);
    }

    @PutMapping("/update/{fileName}")
    public String updateFile(@PathVariable String fileName, @RequestParam MultipartFile file) throws IOException, InterruptedException {
        return service.updateFile(fileName, file);
    }

    @GetMapping("/exists/{fileName}")
    public boolean checkFileExists(@PathVariable String fileName) {
        return service.doesFileExist(fileName);
    }

}
