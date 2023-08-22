package com.gayuh.personalproject.service.difficulty;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.entity.Difficulty;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.DifficultyRepository;
import com.gayuh.personalproject.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DifficultyServiceImpl extends ParentService implements DifficultyService {

    private final DifficultyRepository difficultyRepository;

    @Override
    public List<MasterResponse> getAll() {
        return difficultyRepository.findAll()
                .stream().map(difficulty -> new MasterResponse(difficulty.getId(), difficulty.getName())).toList();
    }

    @Override
    public MasterResponse getById(Long difficultyId) {
        var difficulty = findById(difficultyId);
        return new MasterResponse(difficulty.getId(), difficulty.getName());
    }

    @Override
    public MasterResponse create(MasterRequest request) {
        validationService.validate(request);
        Difficulty difficulty = difficultyRepository.save(Difficulty.builder()
                .name(request.name())
                .build());

        return new MasterResponse(difficulty.getId(), difficulty.getName());
    }

    @Override
    public MasterResponse update(MasterRequest request, Long difficultyId) {
        validationService.validate(request);
        Difficulty difficulty = findById(difficultyId);
        difficulty.setName(request.name());
        difficultyRepository.save(difficulty);

        return new MasterResponse(difficulty.getId(), difficulty.getName());
    }

    @Override
    public void deleteById(Long difficultyId) {
        findById(difficultyId);
        difficultyRepository.deleteById(difficultyId);
    }

    private Difficulty findById(Long difficultyId) {
        return difficultyRepository.findById(difficultyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }
}
