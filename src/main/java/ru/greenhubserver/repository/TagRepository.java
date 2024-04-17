package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
