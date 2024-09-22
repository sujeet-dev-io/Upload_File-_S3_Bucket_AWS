package com.files.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class AwsConfig {
    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public AmazonS3 s3Client() {

        return AmazonS3ClientBuilder.standard().withCredentials(
                new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey))
        ).withRegion(awsRegion).build();
    }

    @Bean
    public TransferManager transferManager() {
        return TransferManagerBuilder.standard()
                .withS3Client(s3Client())
                .withExecutorFactory(() -> Executors.newFixedThreadPool(10))
                .withMinimumUploadPartSize((long) 5 * 1024 * 1024)       // size of each sub-file to upload over the network
                .withMultipartUploadThreshold((long) 100 * 1024 * 1024)  // when file size is greater than 5 MB then it will use the multipartUpload startegy (means break the file into smaller subfiles)
                .build();
    }
}
