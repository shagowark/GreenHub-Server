package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.AchievementDto;
import ru.greenhubserver.entity.Achievement;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.AchievementRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final ImageCloudService imageCloudService;

    public Achievement findByName(String name) {
        return achievementRepository.findByName(name).orElseThrow(() -> new NotFoundException("Achievement not found"));
    }

    public Set<AchievementDto> findAll() {
        return achievementRepository.findAll().stream().map(x ->
                        AchievementDto.builder()
                                .id(x.getId())
                                .name(x.getName())
                                .image(imageCloudService.getImage(x.getImage().getName()))
                                .build())
                .collect(Collectors.toSet());
    }
}
