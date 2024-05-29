package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Achievement;

import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByName (String name);
}
