package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.difficulty.DifficultyService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/difficulties")
@RequiredArgsConstructor
public class DifficultyController {

    private final DifficultyService difficultyService;

    @GetMapping
    public ResponseEntity<Object> getAllDifficulty() {

        List<MasterResponse> responses = difficultyService.getAll();

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + responses.size(),
                HttpStatus.OK,
                responses
        );
    }

    @GetMapping(value = "{difficultyId}")
    public ResponseEntity<Object> getDifficultyById(
            @PathVariable(name = "difficultyId") Long difficultyId
    ) {
        MasterResponse response = difficultyService.getById(difficultyId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping
    public ResponseEntity<Object> createDifficulty(
            @RequestBody MasterRequest request
    ) {
        MasterResponse response = difficultyService.create(request);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                response
        );
    }

    @PutMapping(value = "{difficultyId}")
    public ResponseEntity<Object> updateDifficulty(
            @PathVariable(name = "difficultyId") Long difficultyId,
            @RequestBody MasterRequest request
    ) {
        MasterResponse response = difficultyService.update(request, difficultyId);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping(value = "{difficultyId}")
    public ResponseEntity<Object> deleteDifficulty(
            @PathVariable(name = "difficultyId") Long difficultyId
    ) {
        difficultyService.deleteById(difficultyId);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

}
