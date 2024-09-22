package com.files.serviceImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

import com.amazonaws.util.IOUtils;
import com.files.service.S3_Bucket_Service;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@Slf4j
public class S3Service implements S3_Bucket_Service {

    @Autowired
    private ModelMapper modelMapper;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Autowired
    private TransferManager transferManager;


    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("File deleted successfully: {}", fileName);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the file: " + fileName);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            S3Object s3object = amazonS3.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + fileName);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to download file");
        }
    }

    @Override
    public String updateFile(String existingFileName, MultipartFile newFile) throws IOException, InterruptedException {
        // Delete the existing file
        deleteFile(existingFileName);

        // Upload the new file
        uploadFileUsingS3TransferManager(existingFileName, newFile, getFileContentType(newFile));

        return "File updated successfully";
    }

    @Override
    public boolean doesFileExist(String fileName) {
        return amazonS3.doesObjectExist(bucketName, fileName);
    }

    @Override
    public void imageUpload(MultipartFile file) throws IOException, InterruptedException {
        validateFile(file);
        String fileType = getFileContentType(file);
        StringBuilder fileKey = new StringBuilder("Post");
        uploadFileUsingS3TransferManager(fileKey.toString(), file, fileType);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty() && file.getSize() > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size should not be zero");

        if (file.getName() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name should not be blank");

    }

    private String getFileContentType(MultipartFile file) {
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        assert fileExtension != null;
        if (fileExtension.equalsIgnoreCase("MP4") || fileExtension.equalsIgnoreCase("MOV") || fileExtension.equalsIgnoreCase("AVI") || fileExtension.equalsIgnoreCase("MKV")) {
            return "video/" + fileExtension.toLowerCase();
        } else if (fileExtension.equalsIgnoreCase("JPG") || fileExtension.equalsIgnoreCase("JPEG") || fileExtension.equalsIgnoreCase("PNG")) {
            return "image/" + fileExtension.toLowerCase();
        } else if (fileExtension.equalsIgnoreCase("JSON") || fileExtension.equalsIgnoreCase("CSV") || fileExtension.equalsIgnoreCase("xlsx")) {
            return "file/" + fileExtension.toLowerCase();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid File Format. Please upload the post reel with MP4, AVI, MOV, MKV format and post image with JPG, JPEG, PNG format");
        }
    }

    public void uploadFileUsingS3TransferManager(String key, MultipartFile file, String fileType) throws IOException, AmazonServiceException, AmazonClientException, InterruptedException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getBytes().length);
        metadata.setContentType(fileType);

        // Initiate the upload with TransferManager
        Upload upload = transferManager.upload(bucketName, key, file.getInputStream(), metadata);

        // Wait for the upload to complete
//        upload.waitForCompletion();
    }
}
