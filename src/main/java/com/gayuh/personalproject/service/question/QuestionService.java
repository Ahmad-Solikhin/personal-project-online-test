package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    PaginationResponse<QuestionTitleResponse> getAllPublicQuestionTitle(
            Integer page, String sort, String sortBy, Long topicId, Long difficultyId, String search, Integer row
    );

    PaginationResponse<QuestionTitleResponse> getAllQuestionTitleCreatedByUser(
            UserObject userObject, Integer page, String sort, String sortBy, Long topicId, Long difficultyId, Long accessId, String search, Integer row
    );

    QuestionTitleDetailResponse getQuestionTitleDetail(String questionTitleId);

    @Transactional
    String addQuestionTitle(QuestionTitleRequest request, UserObject userObject);

    @Transactional
    String updateQuestionTitle(QuestionTitleRequest request, String questionTitleId, UserObject userObject);

    @Transactional
    void deleteQuestionTitle(String questionTitleId, UserObject userObject);

    List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId);

    @Transactional
    String addQuestionByQuestionTitleId(String questionTitleId, MultipartFile file, QuestionRequest request, UserObject userObject);

    @Transactional
    String updateQuestionByQuestionTitleId(String questionTitleId, Long questionId, MultipartFile file, QuestionRequest request, UserObject userObject);

    QuestionResponse getQuestionDetail(String questionTitleId, Long questionId, UserObject userObject);

    @Transactional
    void deleteQuestion(String questionTitleId, Long questionId, UserObject userObject);

    @Transactional
    void addChoice(String questionTitleId, Long questionId, ChoiceRequest request, UserObject userObject);

    List<ChoiceResponse> getAllChoice(String questionTitleId, Long questionId, UserObject userObject);

    @Transactional
    void updateChoice(String questionTitleId, Long questionId, Long choiceId, ChoiceRequest request, UserObject userObject);

    @Transactional
    void deleteChoice(String questionTitleId, Long questionId, Long choiceId, UserObject userObject);
}
