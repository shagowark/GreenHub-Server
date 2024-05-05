package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Tag;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
