package com.gayuh.personalproject.service.test;

import com.gayuh.personalproject.dto.TestChoiceResponse;
import com.gayuh.personalproject.dto.TestResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.query.*;
import com.gayuh.personalproject.repository.*;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl extends ParentService implements TestService {
    private final UserRepository userRepository;
    private final QuestionTitleRepository questionTitleRepository;
    private final TestRepository testRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    @Value("${BASE_URL}")
    private String baseUrl;

    @Override
    public List<String> startTest(String questionTitleId, UserObject userObject) {

        //Find the user
        UserQuery userQuery = userRepository.findUserQueryByEmail(userObject.email()).orElseThrow(
                ResponseStatusExceptionUtil::unauthorized
        );

        User user = User.builder()
                .id(userQuery.id())
                .build();

        //Find the question title
        QuestionTitleQuery titleQuery = questionTitleRepository.findQuestionTitleQuery(questionTitleId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        QuestionTitle questionTitle = QuestionTitle.builder()
                .id(titleQuery.id())
                .build();

        //Find all question id by question title idl
        List<Question> questions = questionRepository.findListQuestionIdByQuestionTitleId(questionTitleId)
                .stream().map(question -> Question.builder().id(question).build()).toList();

        //Save the test history to db
        TestHistory testHistory = TestHistory.builder()
                .startedAt(LocalDateTime.now())
                .user(user)
                .questionTitle(questionTitle)
                .build();
        testHistoryRepository.save(testHistory);

        //Create all test base on number of question
        List<Test> tests = questions.stream().map(question -> Test.builder()
                .testHistory(testHistory)
                .question(question)
                .build()).toList();
        testRepository.saveAll(tests);

        //Update the started at
        testHistory.setStartedAt(LocalDateTime.now());
        testHistoryRepository.save(testHistory);

        return tests.stream().map(Test::getId).toList();
    }

    @Override
    public TestResponse getTestQuestionByTestId(String testId, UserObject userObject) {
        TestQuery testQuery = getTestQueryById(testId);

        TestHistoryQuery testHistoryQuery = getTestHistoryQueryById(testQuery.testHistoryId());

        if (!testHistoryQuery.email().equals(userObject.email())) ResponseStatusExceptionUtil.unauthorizedVoid();

        checkFinishTest(testHistoryQuery);

        QuestionQuery questionQuery = questionRepository
                .findQuestionQuery(testHistoryQuery.questionTitleId(), testQuery.questionId()).orElseThrow(
                        ResponseStatusExceptionUtil::notFound
                );

        List<TestChoiceResponse> testChoiceResponses = choiceRepository.findAllChoiceQueryByQuestionId(testQuery.questionId())
                .stream().map(query -> new TestChoiceResponse(query.id(), query.choiceText())).toList();

        return new TestResponse(
                testHistoryQuery.questionTitleId(),
                testQuery.questionId(),
                questionQuery.questionText(),
                questionQuery.time(),
                questionQuery.mediaId() != null ? baseUrl + "medias/" + questionQuery.mediaId() : null,
                testChoiceResponses
        );
    }

    @Override
    public void updateAnswerTest(String testId, Long choiceId) {
        TestQuery testQuery = testRepository.findTestQueryById(testId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        TestHistoryQuery testHistoryQuery = getTestHistoryQueryById(testQuery.testHistoryId());

        checkFinishTest(testHistoryQuery);

        Test test = Test.builder()
                .id(testId)
                .question(Question.builder().id(testQuery.questionId()).build())
                .testHistory(TestHistory.builder().id(testQuery.testHistoryId()).build())
                .choice(Choice.builder().id(choiceId).build())
                .build();
        testRepository.save(test);
    }

    @Override
    public void finishTest(String testId) {
        TestQuery testQuery = getTestQueryById(testId);

        TestHistoryQuery testHistoryQuery = getTestHistoryQueryById(testQuery.testHistoryId());

        checkFinishTest(testHistoryQuery);

        Double score = testRepository.findFinalScoreByTestHistoryId(testQuery.testHistoryId());

        testHistoryRepository.updateTestHistoryScoreById(score, testQuery.testHistoryId(), LocalDateTime.now());
    }

    private TestQuery getTestQueryById(String testId) {
        return testRepository.findTestQueryById(testId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private TestHistoryQuery getTestHistoryQueryById(String testHistoryId) {
        return testHistoryRepository.findTestHistoryQueryById(testHistoryId)
                .orElseThrow(ResponseStatusExceptionUtil::notFound);
    }

    private void checkFinishTest(TestHistoryQuery testHistoryQuery) {
        if (testHistoryQuery.finishedAt() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test already finished");
    }
}
