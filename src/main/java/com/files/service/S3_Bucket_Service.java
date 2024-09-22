package com.files.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3_Bucket_Service {

    void imageUpload(MultipartFile file) throws IOException, InterruptedException;


    // Delete a file from S3 by file name
    void deleteFile(String fileName);

    // Download a file from S3 by file name
    byte[] downloadFile(String fileName);

    // Update an existing file in S3
    String updateFile(String existingFileName, MultipartFile newFile) throws IOException, InterruptedException;

    // Check if a file exists in the S3 bucket
    boolean doesFileExist(String fileName);
}
