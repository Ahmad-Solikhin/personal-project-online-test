package com.gayuh.personalproject.service.questiontitle;

import com.gayuh.personalproject.dto.*;
import org.springframework.transaction.annotation.Transactional;

public interface QuestionTitleService {
    PaginationResponse<QuestionTitleResponse> getAllPublicQuestionTitle(PaginationRequest pagination);

    PaginationResponse<QuestionTitleResponse> getAllQuestionTitleCreatedByUser(
            UserObject userObject, PaginationRequest pagination
    );

    QuestionTitleDetailResponse getQuestionTitleDetail(String questionTitleId);

    @Transactional
    String addQuestionTitle(QuestionTitleRequest request, UserObject userObject);

    @Transactional
    String updateQuestionTitle(QuestionTitleRequest request, String questionTitleId, UserObject userObject);

    @Transactional
    void deleteQuestionTitle(String questionTitleId, UserObject userObject);
    @Transactional
    String sharePrivateQuestionTitle(String questionTitleId, String generate, UserObject userObject);

    QuestionTitleDetailResponse getQuestionTitleDetailByToken(String token);
}
