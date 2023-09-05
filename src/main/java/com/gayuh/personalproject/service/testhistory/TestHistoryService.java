package com.gayuh.personalproject.service.testhistory;

import com.gayuh.personalproject.dto.PaginationRequest;
import com.gayuh.personalproject.dto.PaginationResponse;
import com.gayuh.personalproject.dto.TestHistoryResponse;
import com.gayuh.personalproject.dto.UserTestHistoryResponse;

public interface TestHistoryService {
    PaginationResponse<UserTestHistoryResponse> getTestHistoryByQuestionTitleId(String questionTitleId, PaginationRequest pagination);

    PaginationResponse<TestHistoryResponse> getTestHistoryByUserId(String userId, PaginationRequest pagination);
}
