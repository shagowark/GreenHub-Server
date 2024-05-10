package ru.greenhubserver.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.exceptions.NotFoundException;

import java.io.File;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageCloudService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.defaultImagePath}")
    private String defaultImagePath;

    @Value("${minio.defaultImageName}")
    private String defaultImageName;

    @PostConstruct
    private void saveDefaultImage(){
        createBucket();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(defaultImagePath)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    .object(defaultImageName)
                    .build());
        } catch (Exception e) {
            throw new BadRequestException("Image upload exception");
        }
    }

    public void saveImage(MultipartFile file, String name) {
        try (InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    .object(name)
                    .build());
        } catch (Exception e) {
            throw new BadRequestException("Image upload exception");
        }
    }


    public byte[] getImage(String name) {
        try {
        return IOUtils.toByteArray(minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(name)
                .build()));
        } catch (Exception e) {
            throw new NotFoundException("Image not found");
        }
    }

    public String generateFileName(Long id, MultipartFile file) {
        return id + file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
    }

    private void createBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (Exception e) {
            System.out.println("===============================================");
            e.printStackTrace();
            throw new BadRequestException("Image upload exception");
        }
    }
}
