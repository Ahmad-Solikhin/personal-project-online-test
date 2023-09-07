package com.gayuh.personalproject.service.testhistory;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.query.QuestionTitleQuery;
import com.gayuh.personalproject.query.TestHistoryQuery;
import com.gayuh.personalproject.query.UserQuery;
import com.gayuh.personalproject.repository.QuestionTitleRepository;
import com.gayuh.personalproject.repository.TestHistoryRepository;
import com.gayuh.personalproject.repository.UserRepository;
import com.gayuh.personalproject.util.PaginationUtil;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestHistoryServiceImpl implements TestHistoryService {
    private final TestHistoryRepository testHistoryRepository;
    private final QuestionTitleRepository questionTitleRepository;
    private final UserRepository userRepository;

    @Override
    public PaginationResponse<UserTestHistoryResponse> getTestHistoryByQuestionTitleId(
            String questionTitleId, PaginationRequest pagination
    ) {
        Sort sort1 = PaginationUtil.getSort(pagination.getSort(), pagination.getSortBy());

        String search = "%" + pagination.getSearch() + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(pagination.getPage(), pagination.getRow(), sort1);

        Page<UserTestHistoryResponse> queries = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleId, search, pageRequest
        );

        List<UserTestHistoryResponse> response = queries.stream().toList();

        return PaginationUtil.createPageResponse(
                response,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                pagination.getPage()
        );
    }

    @Override
    public PaginationResponse<TestHistoryResponse> getTestHistoryByUserId(
            String userId, PaginationRequest pagination
    ) {

        Sort sort1 = PaginationUtil.getSort(pagination.getSort(), pagination.getSortBy());

        String search = "%" + pagination.getSearch() + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(pagination.getPage(), pagination.getRow(), sort1);

        Page<TestHistoryResponse> queries = testHistoryRepository.findAllTestHistoryByUserIdWithPage(
                search, pagination.getTopicId(), pagination.getDifficultyId(), userId, pageRequest
        );

        List<TestHistoryResponse> responses = queries.stream().toList();

        return PaginationUtil.createPageResponse(
                responses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                pagination.getPage()
        );
    }

    @Override
    public TestHistoryDetailResponse getTestHistoryDetail(String testHistoryId, UserObject userObject) {
        TestHistoryQuery testHistoryQuery = testHistoryRepository.findTestHistoryQueryById(testHistoryId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        QuestionTitleQuery questionTitleQuery = questionTitleRepository.findQuestionTitleQuery(testHistoryQuery.questionTitleId())
                .orElseThrow(ResponseStatusExceptionUtil::notFound);

        if (!questionTitleQuery.userId().equals(userObject.id())) ResponseStatusExceptionUtil.unauthorizedVoid();

        UserQuery userQuery = userRepository.findUserQueryByEmail(testHistoryQuery.email()).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        List<TestHistoryQuestionResponse> testHistoryQuestionResponses = testHistoryRepository.findAllHistoryAnswerById(testHistoryId);

        return new TestHistoryDetailResponse(
                testHistoryQuery.id(),
                testHistoryQuery.questionTitleId(),
                questionTitleQuery.title(),
                userQuery.name(),
                userObject.email(),
                testHistoryQuery.score(),
                testHistoryQuery.startedAt(),
                testHistoryQuery.finishedAt(),
                testHistoryQuestionResponses
        );
    }
}
