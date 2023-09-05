package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.questiontitle.QuestionTitleService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/question-titles")
@RequiredArgsConstructor
public class QuestionTitleController {
    private static final String QUESTION_TITLE_DETAIL_URL = "question-titles/";

    private final QuestionTitleService questionTitleService;

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
            @RequestParam(name = "topic", required = false, defaultValue = "") Long topicId,
            @RequestParam(name = "difficulty", required = false, defaultValue = "") Long difficultyId,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "row", required = false, defaultValue = "10") Integer row,
            @RequestParam(name = "token", required = false, defaultValue = "") String token
    ) {

        if (!token.isBlank()) {
            QuestionTitleDetailResponse response = questionTitleService.getQuestionTitleDetailByToken(token);

            return CustomResponse.generateResponse(
                    ResponseMessage.GET_DATA.value(),
                    HttpStatus.OK,
                    response
            );
        }

        PaginationRequest pagination = PaginationRequest.builder()
                .page(page)
                .sortBy(sortBy)
                .sort(sort)
                .topicId(topicId)
                .difficultyId(difficultyId)
                .search(search)
                .row(row)
                .build();

        PaginationResponse<QuestionTitleResponse> response = questionTitleService.getAllPublicQuestionTitle(pagination);

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

        PaginationRequest pagination = PaginationRequest.builder()
                .page(page)
                .sort(sort)
                .sortBy(sortBy)
                .topicId(topicId)
                .difficultyId(difficultyId)
                .accessId(accessId)
                .search(search)
                .row(row)
                .build();

        PaginationResponse<QuestionTitleResponse> response = questionTitleService.getAllQuestionTitleCreatedByUser(
                userObject, pagination
        );

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.result().size(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Get question title detail by questionTitleId
     */
    @GetMapping(value = "{questionTitleId}") //Tested : done
    public ResponseEntity<Object> getQuestionTitleDetail(
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        QuestionTitleDetailResponse response = questionTitleService.getQuestionTitleDetail(questionTitleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Create new question title
     * Default access is private when user first time create question title
     */
    @PostMapping //Tested : done
    public ResponseEntity<Object> createQuestionTitle(
            @RequestBody QuestionTitleRequest request,
            UserObject userObject
    ) {
        String response = questionTitleService.addQuestionTitle(request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                Map.of("link", QUESTION_TITLE_DETAIL_URL + response)
        );
    }

    /**
     * Update question title
     */
    @PutMapping(value = "{questionTitleId}") //Tested : done
    public ResponseEntity<Object> updateQuestionTitle(
            @RequestBody QuestionTitleRequest request,
            UserObject userObject,
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        String response = questionTitleService.updateQuestionTitle(request, questionTitleId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                Map.of("link", QUESTION_TITLE_DETAIL_URL + response)
        );
    }

    /**
     * Delete question title
     * If delete question title, all data connected to question title will be deleted to
     */

    @DeleteMapping(value = "{questionTitleId}")
    public ResponseEntity<Object> deleteQuestionTitle(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            UserObject userObject
    ) {
        questionTitleService.deleteQuestionTitle(questionTitleId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

    /**
     * Share private test
     * Create token to question title
     */
    @PatchMapping(value = "{questionTitleId}/share")
    public ResponseEntity<Object> sharePrivateQuestionTitle(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @RequestParam(name = "generate", required = false, defaultValue = "false") String generate,
            UserObject userObject
    ) {
        String token = questionTitleService.sharePrivateQuestionTitle(questionTitleId, generate, userObject);

        return CustomResponse.generateResponse(
                "Success share question title",
                HttpStatus.OK,
                "question-titles?token=" + token
        );
    }
}
