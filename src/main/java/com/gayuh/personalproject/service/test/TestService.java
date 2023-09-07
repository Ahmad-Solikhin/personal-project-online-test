package com.gayuh.personalproject.service.test;

import com.gayuh.personalproject.dto.TestResponse;
import com.gayuh.personalproject.dto.UserObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TestService {
    @Transactional
    List<String> startTest(String questionTitleId, UserObject userObject);

    TestResponse getTestQuestionByTestId(String testId, UserObject userObject);

    @Transactional
    void updateAnswerTest(String testId, Long choiceId);

    @Transactional
    void finishTest(String testId);
}
