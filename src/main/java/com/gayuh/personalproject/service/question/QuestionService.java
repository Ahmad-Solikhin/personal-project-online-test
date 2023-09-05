package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.QuestionRequest;
import com.gayuh.personalproject.dto.QuestionResponse;
import com.gayuh.personalproject.dto.UserObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId);

    @Transactional
    Long addQuestion(String questionTitleId, MultipartFile file, QuestionRequest request, UserObject userObject);

    @Transactional
    Long updateQuestion(String questionTitleId, Long questionId, MultipartFile multipartFile, QuestionRequest request, UserObject userObject);

    QuestionResponse getQuestionDetail(String questionTitleId, Long questionId, UserObject userObject);

    @Transactional
    void deleteQuestion(String questionTitleId, Long questionId, UserObject userObject);
}
