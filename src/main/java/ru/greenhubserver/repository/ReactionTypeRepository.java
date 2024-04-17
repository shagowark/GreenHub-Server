package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.ReactionType;

public interface ReactionTypeRepository extends JpaRepository<ReactionType, Integer> {
}
