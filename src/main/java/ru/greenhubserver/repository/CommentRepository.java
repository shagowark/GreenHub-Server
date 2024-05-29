package ru.greenhubserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Comment;
import ru.greenhubserver.entity.Publication;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByPublication(Publication publication, Pageable pageable);
}
