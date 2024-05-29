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
import ru.greenhubserver.utils.ImagePropertiesUtil;
import ru.greenhubserver.utils.ImageProperty;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageCloudService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    private final ImagePropertiesUtil imagePropertiesUtil;

    @PostConstruct
    private void saveDefaultImages() {
        createBucket();
        for (ImageProperty property : imagePropertiesUtil.getProperties()) {
            saveFromClasspath(property.getPath(), property.getName());
        }
    }

    private void saveFromClasspath(String path, String name){
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .stream(inputStream, inputStream.available(), -1)
                    .bucket(bucketName)
                    .object(name)
                    .build());
        } catch (Exception e) {
            throw new BadRequestException("Image upload exception");
        }
    }

    public void saveImage(MultipartFile file, String name) {
        try (InputStream inputStream = file.getInputStream()) {
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
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        if (!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")) {
            throw new BadRequestException("Image can be only .jpg or .png or .jpeg");
        }
        return id + extension;
    }

    public void deleteImage(String name) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
        } catch (Exception e) {
            throw new NotFoundException("Error while deleting image");
        }
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
