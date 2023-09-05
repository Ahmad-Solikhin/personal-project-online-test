package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.QuestionRequest;
import com.gayuh.personalproject.dto.QuestionResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.question.QuestionService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/v1")
public class QuestionController {
    private final QuestionService questionService;
    private static final String QUESTION_TITLE_DETAIL_URL = "question-titles/";

    //Question Start

    /**
     * Get all question by questionTitleId
     * Will retrieve all question in question title with data included media if exist
     */
    @GetMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions")
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
    @PostMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions")
    public ResponseEntity<Object> createQuestionForQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "data") QuestionRequest request,
            UserObject userObject
    ) {
        Long response = questionService.addQuestion(questionTitleId, file, request, userObject);

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
    @PutMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}")
    public ResponseEntity<Object> updateQuestionByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @PathVariable(name = "questionId") Long questionId,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestPart(name = "data") QuestionRequest request,
            UserObject userObject
    ) {
        Long response = questionService.updateQuestion(questionTitleId, questionId, file, request, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                Map.of("link", QUESTION_TITLE_DETAIL_URL + questionTitleId + "/questions/" + response)
        );
    }

    /**
     * Get detail question by questionTitleId and questionId itself
     */
    @GetMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}")
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
    @DeleteMapping(value = QUESTION_TITLE_DETAIL_URL + "{questionTitleId}/questions/{questionId}")
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
}
