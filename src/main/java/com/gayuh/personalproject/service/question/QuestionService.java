package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionService {
    PaginationResponse<QuestionTitleResponse> getAllPublicQuestionTitle(
            Integer page, String sort, String sortBy, Long topicId, Long difficultyId, String search, Integer row
    );

    PaginationResponse<QuestionTitleResponse> getAllQuestionTitleCreatedByUser(
            UserObject userObject, Integer page, String sort, String sortBy, Long topicId, Long difficultyId, Long accessId, String search, Integer row
    );

    QuestionTitleDetailResponse getQuestionDetail(String questionTitleId);

    @Transactional
    String addQuestionTitle(QuestionTitleRequest request, UserObject userObject);

    @Transactional
    String updateQuestionTitle(QuestionTitleRequest request, String questionTitleId, UserObject userObject);

    @Transactional
    void deleteQuestionTitle(String questionTitleId, UserObject userObject);

    List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId);
}
