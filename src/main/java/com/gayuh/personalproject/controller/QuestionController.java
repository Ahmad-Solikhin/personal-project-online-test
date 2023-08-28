package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.question.QuestionService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/question-titles")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Get all public question using pagination
     * Filter by : topic, difficulty
     * search by : title
     * Sort by : recent, popular
     * Who can access : every role
     */

    @GetMapping //Tested : done
    public ResponseEntity<Object> getAllQuestionTitlePublic(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "topic", required = false, defaultValue = "0") Long topicId,
            @RequestParam(name = "difficulty", required = false, defaultValue = "0") Long difficultyId,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "row", required = false, defaultValue = "10") Integer row
    ) {
        PaginationResponse<QuestionTitleResponse> response = questionService.getAllPublicQuestionTitle(
                page, sort, sortBy, topicId, difficultyId, search, row
        );

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.result().size(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Get all histories created by user using pagination to
     * Filter by : topic, difficulty, access
     * Sort by : recent, popular
     * who can access : user, admin
     */
    @GetMapping(value = "histories")
    public ResponseEntity<Object> getAllHistoriesCreatedByUser(
            UserObject userObject,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "sortBy", required = false, defaultValue = "recent") String sortBy,
            @RequestParam(name = "topic", required = false, defaultValue = "") Long topicId,
            @RequestParam(name = "difficulty", required = false, defaultValue = "") Long difficultyId,
            @RequestParam(name = "access", required = false, defaultValue = "") Long accessId,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "row", required = false, defaultValue = "10") Integer row
    ) {
        PaginationResponse<QuestionTitleResponse> response = questionService.getAllQuestionTitleCreatedByUser(
                userObject, page, sort, sortBy, topicId, difficultyId, accessId, search, row
        );

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.result().size(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Get all test histories who already finished the question title user created using pagination
     * Sort by : score, recent, fastest
     * Who can access : the user who made the question
     */

    @GetMapping(value = "{questionTitleId}/test-histories")
    public ResponseEntity<Object> getAllTestHistoryByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "sortBy", required = false, defaultValue = "finished_at") String sortBy
    ) {

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value(),
                HttpStatus.OK,
                Map.of(
                        "page", page,
                        "sort", sort
                )
        );
    }

    @GetMapping(value = "{questionTitleId}") //Tested : done
    public ResponseEntity<Object> getQuestionTitleDetail(
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        QuestionTitleDetailResponse response = questionService.getQuestionDetail(questionTitleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping //Tested : done
    public ResponseEntity<Object> createQuestionTitle(
            @RequestBody QuestionTitleRequest request,
            UserObject userObject
    ) {
        String response = questionService.addQuestionTitle(request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                Map.of("link", "question-titles/" + response)
        );
    }

    @PutMapping(value = "{questionTitleId}") //Tested : done
    public ResponseEntity<Object> updateQuestionTitle(
            @RequestBody QuestionTitleRequest request,
            UserObject userObject,
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        String response = questionService.updateQuestionTitle(request, questionTitleId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                Map.of("link", "question-titles/" + response)
        );
    }

    @DeleteMapping(value = "{questionTitleId}")
    public ResponseEntity<Object> deleteQuestionTitle(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            UserObject userObject
    ) {
        questionService.deleteQuestionTitle(questionTitleId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

    @GetMapping(value = "{questionTitleId}/questions")
    public ResponseEntity<Object> getAllQuestionByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            UserObject userObject
    ) {
        List<QuestionResponse> responses = questionService.getAllQuestionByQuestionTitleId(questionTitleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value(),
                HttpStatus.OK,
                responses
        );
    }

    @PostMapping(value = "{questionTitleId}/questions")
    public ResponseEntity<Object> createQuestionForQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            UserObject userObject
    ) {
        List<QuestionResponse> responses = questionService.getAllQuestionByQuestionTitleId(questionTitleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value(),
                HttpStatus.OK,
                responses
        );
    }
}
