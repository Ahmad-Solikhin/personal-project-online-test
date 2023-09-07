package com.gayuh.personalproject.service.testhistory;

import com.gayuh.personalproject.dto.*;

public interface TestHistoryService {
    PaginationResponse<UserTestHistoryResponse> getTestHistoryByQuestionTitleId(String questionTitleId, PaginationRequest pagination);

    PaginationResponse<TestHistoryResponse> getTestHistoryByUserId(String userId, PaginationRequest pagination);

    TestHistoryDetailResponse getTestHistoryDetail(String testHistoryId, UserObject userObject);
}
