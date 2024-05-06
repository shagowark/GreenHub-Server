package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Reaction;
import ru.greenhubserver.entity.User;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByPublicationAndUser(Publication publication, User user);
}
