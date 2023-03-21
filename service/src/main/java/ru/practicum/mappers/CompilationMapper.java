package ru.practicum.mappers;

import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.model.Compilation;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return new Compilation(
                null,
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle(),
                null
        );
    }

    public static CompilationDto toDto(Compilation compilation) {
        return new CompilationDto(
                compilation.getEvents(),
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }
}
