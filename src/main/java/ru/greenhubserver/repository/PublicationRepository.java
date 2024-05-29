package ru.greenhubserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Tag;
import ru.greenhubserver.entity.User;

import java.util.Set;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query("SELECT DISTINCT p FROM Publication p JOIN p.tags t WHERE t IN :tags")
    Page<Publication> findAllByTags(Pageable pageable, Set<Tag> tags);

    Page<Publication> findAllByUser(User user, Pageable pageable);

    @Query("SELECT p FROM Publication p WHERE p.user IN :users")
    Page<Publication> findAllInUsers(Pageable pageable, Set<User> users);

    @Query("SELECT p FROM Publication p JOIN p.tags t WHERE p.user IN :users AND t IN :tags")
    Page<Publication> findAllInUsersByTags(Pageable pageable, Set<User> users, Set<Tag> tags);
}
