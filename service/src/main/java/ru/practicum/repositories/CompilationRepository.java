package ru.practicum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Compilation;

import java.util.List;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(value = "SELECT * FROM compilations offset ? LIMIT ?", nativeQuery = true)
    List<Compilation> getAllCompilations(Integer from, Integer size);

    @Query(value = "SELECT * FROM compilations WHERE pinned = true offset ? LIMIT ?", nativeQuery = true)
    List<Compilation> getAllPinnedCompilations(Integer from, Integer size);

}
