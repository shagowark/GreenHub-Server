package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.entity.Tag;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.TagRepository;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag getTagByName(String name){
        return tagRepository.findByName(name).orElseThrow(
                () -> new NotFoundException("Tag not found"));
    }
}
