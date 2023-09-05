package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.QuestionRequest;
import com.gayuh.personalproject.dto.QuestionResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.entity.Question;
import com.gayuh.personalproject.entity.QuestionTitle;
import com.gayuh.personalproject.query.QuestionQuery;
import com.gayuh.personalproject.repository.ChoiceRepository;
import com.gayuh.personalproject.repository.QuestionRepository;
import com.gayuh.personalproject.repository.QuestionTitleRepository;
import com.gayuh.personalproject.repository.TestRepository;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.service.storage.StorageService;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ParentService implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionTitleRepository questionTitleRepository;
    private final StorageService storageService;
    private final TestRepository testRepository;
    private final ChoiceRepository choiceRepository;

    @Override
    public List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId) {
        return questionRepository.findAllQuestionByQuestionTitleId(questionTitleId)
                .stream().map(query -> new QuestionResponse(
                        query.id(),
                        query.questionText(),
                        query.time(),
                        query.score(),
                        query.createdAt(),
                        query.updatedAt(),
                        query.mediaId()
                )).toList();
    }

    @Override
    public Long addQuestion(
            String questionTitleId, MultipartFile file,
            QuestionRequest request, UserObject userObject
    ) {
        validationService.validate(request);

        QuestionTitle questionTitle = getQuestionTitleById(questionTitleId);

        if (!questionTitle.getUser().getEmail().equals(userObject.email())) {
            ResponseStatusExceptionUtil.unauthorizedVoid();
        }

        Question question = Question.builder()
                .questionText(request.questionText())
                .time(request.time())
                .score(request.score())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .questionTitle(questionTitle)
                .build();

        questionRepository.save(question);

        if (file != null) {
            storageService.saveImageQuestion(file, question);
        }

        return question.getId();
    }

    @Override
    public Long updateQuestion(
            String questionTitleId, Long questionId,
            MultipartFile file, QuestionRequest request,
            UserObject userObject
    ) {
        validationService.validate(request);

        Question question = getQuestionByIdAndQuestionTitleId(questionId, questionTitleId);

        if (file != null && file.isEmpty()) file = null;

        if (question.getMedia() != null && file != null) {
            storageService.updateImageQuestion(file, question.getMedia().getId());
        } else if (question.getMedia() == null && file != null) {
            storageService.saveImageQuestion(file, question);
        } else if (question.getMedia() != null) {
            storageService.deleteImageQuestion(question.getMedia().getId());
        }

        question.setQuestionText(request.questionText());
        question.setTime(request.time());
        question.setScore(request.score());
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);

        return question.getId();
    }

    @Override
    public QuestionResponse getQuestionDetail(String questionTitleId, Long questionId, UserObject userObject) {
        QuestionQuery query = questionRepository.findQuestionQuery(questionTitleId, questionId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        return new QuestionResponse(
                query.id(), query.questionText(),
                query.time(), query.score(),
                query.createdAt(),
                query.updatedAt(), query.mediaId()
        );
    }

    @Override
    public void deleteQuestion(String questionTitleId, Long questionId, UserObject userObject) {
        Question question = getQuestionByIdAndQuestionTitleId(questionId, questionTitleId);

        testRepository.deleteTestByQuestionId(questionId);
        if (question.getMedia() != null) storageService.deleteImageQuestion(question.getMedia().getId());
        choiceRepository.deleteChoiceByQuestionId(questionId);
        questionRepository.delete(question);
    }

    private Question getQuestionByIdAndQuestionTitleId(Long questionId, String questionTitleId) {
        return questionRepository.findByQuestionTitleIdAndQuestionId(questionTitleId, questionId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private QuestionTitle getQuestionTitleById(String questionTitleId) {
        return questionTitleRepository.findById(questionTitleId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }
}
