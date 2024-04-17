package ru.greenhubserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.greenhubserver.entity.State;

public interface StateRepository extends JpaRepository<State, Integer> {
}
