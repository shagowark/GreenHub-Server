package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Achievement;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
