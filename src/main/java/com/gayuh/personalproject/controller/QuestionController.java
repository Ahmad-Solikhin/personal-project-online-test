package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.question.QuestionService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/v1/question-titles")
@RequiredArgsConstructor
public class QuestionController {
    private static final String QUESTION_TITLE_DETAIL_URL = "question-titles/";

    private final QuestionService questionService;

    //Question Title Start

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
     * Get question title detail by questionTitleId
     */
    @GetMapping(value = "{questionTitleId}") //Tested : done
    public ResponseEntity<Object> getQuestionTitleDetail(
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        QuestionTitleDetailResponse response = questionService.getQuestionTitleDetail(questionTitleId);

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
        String response = questionService.addQuestionTitle(request, userObject);

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
        String response = questionService.updateQuestionTitle(request, questionTitleId, userObject);

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
        questionService.deleteQuestionTitle(questionTitleId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

    //Question Title End

    //Question Start

    /**
     * Get all question by questionTitleId
     * Will retrieve all question in question title with data included media if exist
     */
    @GetMapping(value = "{questionTitleId}/questions")
    public ResponseEntity<Object> getAllQuestionByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId
    ) {
        List<QuestionResponse> responses = questionService.getAllQuestionByQuestionTitleId(questionTitleId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + responses.size(),
                HttpStatus.OK,
                responses
        );
    }

    /**
     * Create question as a child of question title
     * File is optional, if exist only can upload 1 image
     */
    @PostMapping(value = "{questionTitleId}/questions")
    public ResponseEntity<Object> createQuestionForQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "data") QuestionRequest request,
            UserObject userObject
    ) {
        String response = questionService.addQuestionByQuestionTitleId(questionTitleId, file, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                Map.of("link", QUESTION_TITLE_DETAIL_URL + questionTitleId + "/questions/" + response)
        );
    }

    /**
     * Updated question that has been created
     * If the question have media and user resend the same media so the media will not update
     * If the question have media and user delete it, then the media will remove when update
     * If the question don't have media and user upload media, then the question will have media
     * If the question have media and user update the media, old media will replace by the new media
     */
    @PutMapping(value = "{questionTitleId}/questions/{questionId}")
    public ResponseEntity<Object> updateQuestionByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "data") QuestionRequest request,
            UserObject userObject
    ) {
        String response = questionService.updateQuestionByQuestionTitleId(questionTitleId, questionId, file, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                Map.of("link", QUESTION_TITLE_DETAIL_URL + questionTitleId + "/questions/" + response)
        );
    }

    /**
     * Get detail question by questionTitleId and questionId itself
     */
    @GetMapping(value = "{questionTitleId}/questions/{questionId}")
    public ResponseEntity<Object> getQuestionDetailByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            UserObject userObject
    ) {

        QuestionResponse response = questionService.getQuestionDetail(questionTitleId, questionId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Delete question
     * If question deleted, it will delete tests table, choice table and media if exist
     */
    @DeleteMapping(value = "{questionTitleId}/questions/{questionId}")
    public ResponseEntity<Object> deleteQuestion(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            UserObject userObject
    ) {

        questionService.deleteQuestion(questionTitleId, questionId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

    //Question End

    //Choice Start

    @PostMapping(value = "{questionTitleId}/questions/{questionId}/choices")
    public ResponseEntity<Object> crateChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @RequestBody ChoiceRequest request,
            UserObject userObject
    ) {

        questionService.addChoice(questionTitleId, questionId, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED
        );
    }

    @GetMapping(value = "{questionTitleId}/questions/{questionId}/choices")
    public ResponseEntity<Object> getAllChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            UserObject userObject
    ) {

        List<ChoiceResponse> response = questionService.getAllChoice(questionTitleId, questionId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.size(),
                HttpStatus.OK,
                response
        );
    }

    @PutMapping(value = "{questionTitleId}/questions/{questionId}/choices/{choiceId}")
    public ResponseEntity<Object> updateChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @PathVariable(name = "choiceId") Long choiceId,
            @RequestBody ChoiceRequest request,
            UserObject userObject
    ) {
        questionService.updateChoice(questionTitleId, questionId, choiceId, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = "{questionTitleId}/questions/{questionId}/choices/{choiceId}")
    public ResponseEntity<Object> deleteChoice(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @PathVariable(name = "choiceId") Long choiceId,
            UserObject userObject
    ) {
        questionService.deleteChoice(questionTitleId, questionId, choiceId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }

    //Choice End
}
