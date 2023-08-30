package com.gayuh.personalproject.service.testHistory;

import com.gayuh.personalproject.dto.PaginationResponse;
import com.gayuh.personalproject.dto.TestHistoryResponse;
import com.gayuh.personalproject.dto.UserTestHistoryResponse;

public interface TestHistoryService {
    PaginationResponse<UserTestHistoryResponse> getTestHistoryByQuestionTitleId(String questionTitleId, Integer page, String sort, String sortBy, String search, Integer row);

    PaginationResponse<TestHistoryResponse> getTestHistoryByUserId(Integer page, String sort, String sortBy, String search, Long topicId, Long difficultyId, Integer row, String userId);
}
