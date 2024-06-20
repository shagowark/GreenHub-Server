package ru.greenhubserver.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Reaction;
import ru.greenhubserver.entity.User;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reaction> findByPublicationAndUser(Publication publication, User user);
}
