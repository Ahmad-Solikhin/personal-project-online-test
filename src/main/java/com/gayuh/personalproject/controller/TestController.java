package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.TestResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.test.TestService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;
    @Value("${BASE_URL}")
    private String baseUrl;

    /**
     * Start test (post)
     * To start test must log in first
     * After test history created, create new test with testHistoryId created before and post all questionId with answer null
     * Then return all the testId list
     */
    @PostMapping(value = "question-titles/{questionTitleId}/tests") //Tested : done
    public ResponseEntity<Object> startTest(
            @PathVariable(name = "questionTitleId") String questionTitleId,
            UserObject userObject
    ) {
        List<String> testIds = testService.startTest(questionTitleId, userObject);

        return CustomResponse.generateResponse(
                "Start test success",
                HttpStatus.OK,
                testIds
        );
    }

    /**
     * Do test (get)
     * Get the testId from query parameter
     * Check if the test not ended yet and this is your test
     * If the test not ended retrieve the question, media (if exist), and all choice
     */
    @GetMapping(value = "tests/{testId}") //Tested : done
    public ResponseEntity<Object> doTest(
            @PathVariable(name = "testId") String testId,
            UserObject userObject
    ) {
        TestResponse response = testService.getTestQuestionByTestId(testId, userObject);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    /**
     * Answer test (put)
     * For the information is need testId and choiceId (for the answer)
     */
    @PutMapping(value = "tests/{testId}/choices/{choiceId}") //Tested : Done
    public ResponseEntity<Object> answerTest(
            @PathVariable(name = "testId") String testId,
            @PathVariable(name = "choiceId") Long choiceId
    ) {
        testService.updateAnswerTest(testId, choiceId);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK
        );
    }

    /**
     * Finish test (put)
     * After all question is done showing, then the client hit this endpoint to end the test to get score
     * Send data score result
     */
    @PutMapping(value = "tests/{testId}/finish") //Tested : done
    public ResponseEntity<Object> finishTest(
            @PathVariable(name = "testId") String testId
    ) {
        testService.finishTest(testId);

        return CustomResponse.generateResponse(
                "Success finish test",
                HttpStatus.OK,
                Map.of("link", baseUrl + "test-histories")
        );
    }

}
