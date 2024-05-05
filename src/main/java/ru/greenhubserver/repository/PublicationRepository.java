package ru.greenhubserver.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.Tag;

import java.util.Set;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM publication p JOIN publication_tag pt ON p.id = pt.publication_id WHERE pt.tag_id IN (SELECT id FROM tag WHERE tag.name IN :tagNames)")
    Page<Publication> findAllByTags(Pageable pageable, Set<String> tagNames);
}
