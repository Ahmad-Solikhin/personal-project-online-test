package com.gayuh.personalproject.service.testHistory;

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
            String questionTitleId, Integer page,
            String sort, String sortBy,
            String search, Integer row
    ) {
        Sort sort1 = PaginationUtil.getSort(sort, sortBy);

        search = "%" + search + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(page, row, sort1);

        Page<UserTestHistoryResponse> queries = testHistoryRepository.findAllTestHistoryByQuestionTitleId(
                questionTitleId, search, pageRequest
        );

        List<UserTestHistoryResponse> response = queries.stream().toList();

        return PaginationUtil.createPageResponse(
                response,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                page
        );
    }

    @Override
    public PaginationResponse<TestHistoryResponse> getTestHistoryByUserId(
            Integer page, String sort,
            String sortBy, String search,
            Long topicId, Long difficultyId,
            Integer row, String userId
    ) {

        Sort sort1 = PaginationUtil.getSort(sort, sortBy);

        search = "%" + search + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(page, row, sort1);

        Page<TestHistoryResponse> queries = testHistoryRepository.findAllTestHistoryByUserIdWithPage(
                search, topicId, difficultyId, userId, pageRequest
        );

        List<TestHistoryResponse> responses = queries.stream().toList();

        return PaginationUtil.createPageResponse(
                responses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                page
        );
    }
}
