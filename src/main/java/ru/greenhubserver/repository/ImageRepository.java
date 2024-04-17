package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
