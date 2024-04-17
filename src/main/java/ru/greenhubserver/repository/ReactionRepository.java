package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
}
