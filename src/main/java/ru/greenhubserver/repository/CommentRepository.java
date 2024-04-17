package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
