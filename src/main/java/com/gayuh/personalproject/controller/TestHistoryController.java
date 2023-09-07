package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.testhistory.TestHistoryService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1")
@RequiredArgsConstructor
public class TestHistoryController {

    private final TestHistoryService testHistoryService;

    /**
     * Get all test histories who already finished the question title user created using pagination
     * Sort by : score, recent, fastest
     * Filter by : difficulty, topic
     * Search by : title
     * Who can access : the user who made the question
     */

    @GetMapping(value = "question-titles/{questionTitleId}/test-histories")
    public ResponseEntity<Object> getAllTestHistoryByQuestionTitleId(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "sortBy", required = false, defaultValue = "finishedAt") String sortBy,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "row", required = false, defaultValue = "10") Integer row,
            UserObject userObject
    ) {
        PaginationRequest pagination = PaginationRequest.builder()
                .page(page)
                .sort(sort)
                .sortBy(sortBy)
                .search(search)
                .row(row)
                .build();

        PaginationResponse<UserTestHistoryResponse> response = testHistoryService.getTestHistoryByQuestionTitleId(
                questionTitleId, pagination
        );

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.result().size(),
                HttpStatus.OK,
                response
        );
    }

    @GetMapping(value = "test-histories")
    public ResponseEntity<Object> getAllTestHistoryByUserId(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "desc") String sort,
            @RequestParam(name = "sortBy", required = false, defaultValue = "finishedAt") String sortBy,
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "topic", required = false, defaultValue = "") Long topicId,
            @RequestParam(name = "difficulty", required = false, defaultValue = "") Long difficultyId,
            @RequestParam(name = "row", required = false, defaultValue = "10") Integer row,
            UserObject userObject
    ) {
        PaginationRequest pagination = PaginationRequest.builder()
                .page(page)
                .sortBy(sortBy)
                .sort(sort)
                .search(search)
                .topicId(topicId)
                .difficultyId(difficultyId)
                .row(row)
                .build();

        PaginationResponse<TestHistoryResponse> response = testHistoryService.getTestHistoryByUserId(
                userObject.id(), pagination
        );

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.result().size(),
                HttpStatus.OK,
                response
        );
    }

    @GetMapping(value = "test-histories/{testHistoryId}")
    public ResponseEntity<Object> getTestHistoryDetail(
            @PathVariable(name = "testHistoryId") String testHistoryId,
            UserObject userObject
    ) {
        TestHistoryDetailResponse response = testHistoryService.getTestHistoryDetail(testHistoryId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }
}
