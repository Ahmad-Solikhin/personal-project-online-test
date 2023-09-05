package com.gayuh.personalproject.service.testhistory;

import com.gayuh.personalproject.dto.PaginationRequest;
import com.gayuh.personalproject.dto.PaginationResponse;
import com.gayuh.personalproject.dto.TestHistoryResponse;
import com.gayuh.personalproject.dto.UserTestHistoryResponse;
import com.gayuh.personalproject.repository.TestHistoryRepository;
import com.gayuh.personalproject.util.PaginationUtil;
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
}
