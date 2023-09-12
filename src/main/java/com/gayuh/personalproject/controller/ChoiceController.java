package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.ChoiceRequest;
import com.gayuh.personalproject.dto.ChoiceResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.choice.ChoiceService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1")
@RequiredArgsConstructor
public class ChoiceController {

    private final ChoiceService choiceService;
    private static final String QUESTION_TITLE_DETAIL_URL = "question-titles/";

    @PostMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}/choices") //Tested : done
    public ResponseEntity<Object> crateChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @RequestBody ChoiceRequest request,
            UserObject userObject
    ) {

        choiceService.addChoice(questionTitleId, questionId, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}/choices") //Tested : done
    public ResponseEntity<Object> getAllChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            UserObject userObject
    ) {

        List<ChoiceResponse> response = choiceService.getAllChoice(questionTitleId, questionId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.size(),
                HttpStatus.OK,
                response
        );
    }

    @PutMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}/choices/{choiceId}") //Tested : done
    public ResponseEntity<Object> updateChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @PathVariable(name = "choiceId") Long choiceId,
            @RequestBody ChoiceRequest request,
            UserObject userObject
    ) {
        choiceService.updateChoice(questionTitleId, questionId, choiceId, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}/choices/{choiceId}") //Tested : done
    public ResponseEntity<Object> deleteChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @PathVariable(name = "choiceId") Long choiceId,
            UserObject userObject
    ) {
        choiceService.deleteChoice(questionTitleId, questionId, choiceId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }
}
