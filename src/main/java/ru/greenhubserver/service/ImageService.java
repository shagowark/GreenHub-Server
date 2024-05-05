package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.entity.Image;
import ru.greenhubserver.repository.ImageRepository;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public Image save(Image image){
        return imageRepository.save(image);
    }
}
