package ru.greenhubserver.service;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.exceptions.NotFoundException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ImageCloudService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public void saveImage(MultipartFile file, String name) {
        try {
            createBucket();
            InputStream inputStream = file.getInputStream();
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

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }
}
