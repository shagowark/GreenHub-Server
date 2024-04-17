package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
