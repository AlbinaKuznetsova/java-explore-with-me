package ru.yandex.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.compilation.CompilationMapper;
import ru.yandex.practicum.compilation.dto.CompilationDto;
import ru.yandex.practicum.compilation.dto.NewCompilationDto;
import ru.yandex.practicum.compilation.dto.UpdateCompilationRequest;
import ru.yandex.practicum.compilation.model.Compilation;
import ru.yandex.practicum.compilation.repository.CompilationRepository;
import ru.yandex.practicum.event.service.EventService;
import ru.yandex.practicum.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationService {
    private final CompilationRepository repository;
    private final EventService eventService;
    private final CompilationMapper compilationMapper;

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        if (!newCompilationDto.getEvents().isEmpty()) {
            for (Integer id : newCompilationDto.getEvents()) {
                compilation.getEvents().add(eventService.getEventByIdForCompilation(id));
            }
        }
        return compilationMapper.toCompilationDto(repository.save(compilation));
    }

    public void deleteCompilation(int compId) {
        Optional<Compilation> optionalCompilation = repository.findById(compId);
        if (optionalCompilation.isPresent()) {
            repository.deleteById(compId);
        } else {
            log.info("Compilation with id={} was not found", compId);
            throw new ObjectNotFoundException("Compilation with id=" + compId + " was not found", new Throwable("The required object was not found."));
        }

    }

    public CompilationDto updateCompilation(int compId,
                                            UpdateCompilationRequest newCompilation) {
        Compilation compFromDb = null;
        Optional<Compilation> optionalCompilation = repository.findById(compId);
        if (optionalCompilation.isPresent()) {
            compFromDb = optionalCompilation.get();
        } else {
            log.info("Compilation with id={} was not found", compId);
            throw new ObjectNotFoundException("Compilation with id=" + compId + " was not found", new Throwable("The required object was not found."));
        }
        if (newCompilation.getTitle() != null) {
            compFromDb.setTitle(newCompilation.getTitle());
        }
        if (newCompilation.getPinned() != null) {
            compFromDb.setPinned(newCompilation.getPinned());
        }
        if (!newCompilation.getEvents().isEmpty()) {
            compFromDb.getEvents().clear();
            for (Integer id : newCompilation.getEvents()) {
                compFromDb.getEvents().add(eventService.getEventByIdForCompilation(id));
            }
        }
        return compilationMapper.toCompilationDto(repository.save(compFromDb));
    }

    public CompilationDto getCompilation(Integer compId) {
        if (repository.findById(compId).isEmpty()) {
            log.info(" Compilation with id={} was not found", compId);
            throw new ObjectNotFoundException("Compilation with id=" + compId + " was not found", new Throwable("The required object was not found."));
        } else {
            return compilationMapper.toCompilationDto(repository.findById(compId).get());
        }
    }

    public List<CompilationDto> getAllCompilations(Integer from, Integer size, Boolean pinned) {
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.ASC, "id"));
        if (pinned == null) {
            return compilationMapper.toCompilationDto(repository.findAll(pageable).toList());
        } else {
            return compilationMapper.toCompilationDto(repository.findAllByPinned(pinned, pageable).toList());
        }

    }
}
