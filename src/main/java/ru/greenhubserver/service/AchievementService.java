package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.entity.Achievement;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.AchievementRepository;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;

    public Achievement findByName(String name) {
        return achievementRepository.findByName(name).orElseThrow(() -> new NotFoundException("Achievement not found"));
    }
}
