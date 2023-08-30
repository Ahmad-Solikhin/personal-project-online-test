package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.query.QuestionQuery;
import com.gayuh.personalproject.query.QuestionTitleQuery;
import com.gayuh.personalproject.repository.*;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.service.storage.StorageService;
import com.gayuh.personalproject.util.PaginationUtil;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl extends ParentService implements QuestionService {

    private final QuestionTitleRepository questionTitleRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final DifficultyRepository difficultyRepository;
    private final AccessRepository accessRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final ChoiceRepository choiceRepository;
    private final StorageService storageService;

    @Override
    public PaginationResponse<QuestionTitleResponse> getAllPublicQuestionTitle(
            Integer page,
            String sort,
            String sortBy,
            Long topicId,
            Long difficultyId,
            String search,
            Integer row
    ) {
        Sort sort1 = PaginationUtil.getSort(sort, sortBy);

        search = "%" + search + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(page, row, sort1);

        Page<QuestionTitleQuery> queries = questionTitleRepository.findAllPublicQuestionTitleWithPageResult(
                search,
                topicId,
                difficultyId,
                pageRequest
        );


        List<QuestionTitleResponse> questionTitleResponses = setPageQuestionTitleQueryToListQuestionTitleResponse(queries);

        return PaginationUtil.createPageResponse(
                questionTitleResponses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(), page
        );
    }

    @Override
    public PaginationResponse<QuestionTitleResponse> getAllQuestionTitleCreatedByUser(
            UserObject userObject,
            Integer page,
            String sort,
            String sortBy,
            Long topicId,
            Long difficultyId,
            Long accessId,
            String search,
            Integer row
    ) {

        Sort sort1 = PaginationUtil.getSort(sort, sortBy);

        search = "%" + search + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(page, row, sort1);

        Page<QuestionTitleQuery> queries = questionTitleRepository.findAllQuestionTitleCreatedByUserWithPageResult(
                userObject.id(),
                search,
                topicId,
                difficultyId,
                accessId,
                pageRequest
        );

        List<QuestionTitleResponse> questionTitleResponses = setPageQuestionTitleQueryToListQuestionTitleResponse(queries);

        return PaginationUtil.createPageResponse(
                questionTitleResponses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(), page
        );
    }

    @Override
    public QuestionTitleDetailResponse getQuestionTitleDetail(String questionTitleId) {
        QuestionTitleQuery query = getQuestionTitleQueryById(questionTitleId);

        return new QuestionTitleDetailResponse(
                query.id(),
                query.title(),
                query.createdAt(),
                query.userId(),
                query.userName(),
                query.topicId(),
                query.topic(),
                query.difficultyId(),
                query.difficulty(),
                query.access(),
                query.tested()
        );
    }

    @Override
    public String addQuestionTitle(QuestionTitleRequest request, UserObject userObject) {
        validationService.validate(request);

        QuestionTitle questionTitle = setQuestionTitleRequestToQuestionTitleEntity(request, new QuestionTitle(), userObject);
        questionTitleRepository.save(questionTitle);

        return questionTitle.getId();
    }

    @Override
    public String updateQuestionTitle(QuestionTitleRequest request, String questionTitleId, UserObject userObject) {
        validationService.validate(request);

        QuestionTitleQuery query = getQuestionTitleQueryById(questionTitleId);

        checkAccessUser(userObject, query);

        QuestionTitle questionTitle = setQuestionTitleQueryToQuestionTitle(query);

        setQuestionTitleRequestToQuestionTitleEntity(request, questionTitle, userObject);
        questionTitleRepository.save(questionTitle);

        return questionTitle.getId();
    }

    @Override
    public void deleteQuestionTitle(String questionTitleId, UserObject userObject) {
        QuestionTitleQuery query = getQuestionTitleQueryById(questionTitleId);

        checkAccessUser(userObject, query);

        List<Long> idQuestions = questionRepository.findAllQuestionIdByQuestionTitleId(questionTitleId);

        if (!idQuestions.isEmpty()) {
            testRepository.deleteAllTestByQuestionId(idQuestions);

            choiceRepository.deleteAllChoiceByQuestionId(idQuestions);

            storageService.deleteAllImageByQuestionTitleId(questionTitleId);
        }

        testHistoryRepository.deleteAllTestHistoryByQuestionTitleId(questionTitleId);

        questionRepository.deleteAllQuestionByQuestionTitleId(questionTitleId);

        questionTitleRepository.deleteQuestionTitleById(questionTitleId);
    }

    @Override
    public List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId) {
        getQuestionTitleDetail(questionTitleId);

        return questionRepository.getAllQuestionByQuestionTitleId(questionTitleId)
                .stream().map(questionQuery -> new QuestionResponse(
                        questionQuery.id(),
                        questionQuery.questionText(),
                        questionQuery.time(),
                        questionQuery.score(),
                        questionQuery.createdAt(),
                        questionQuery.updatedAt(),
                        questionQuery.mediaId()
                )).toList();
    }

    @Override
    public String addQuestionByQuestionTitleId(
            String questionTitleId,
            MultipartFile file,
            QuestionRequest request,
            UserObject userObject
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

        return question.getId().toString();
    }

    @Override
    public String updateQuestionByQuestionTitleId(
            String questionTitleId,
            Long questionId,
            MultipartFile file,
            QuestionRequest request,
            UserObject userObject) {
        validationService.validate(request);

        Question question = getQuestionByIdAndQuestionTitleId(questionId, questionTitleId);

        if (file != null && (file.isEmpty())) file = null;

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

        return question.getId().toString();
    }

    @Override
    public QuestionResponse getQuestionDetail(String questionTitleId, Long questionId, UserObject userObject) {
        QuestionQuery query = questionRepository.getQuestionDetail(questionTitleId, questionId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );

        return new QuestionResponse(query.id(), query.questionText(), query.time(), query.score(), query.createdAt(), query.updatedAt(), query.mediaId());
    }

    @Override
    public void deleteQuestion(String questionTitleId, Long questionId, UserObject userObject) {
        Question question = getQuestionByIdAndQuestionTitleId(questionId, questionTitleId);

        testRepository.deleteTestByQuestionId(questionId);
        if (question.getMedia() != null) storageService.deleteImageQuestion(question.getMedia().getId());
        choiceRepository.deleteChoiceByQuestionId(questionId);
        questionRepository.delete(question);
    }

    @Override
    public void addChoice(String questionTitleId, Long questionId, ChoiceRequest request, UserObject userObject) {
        validationService.validate(request);
        Question question = getQuestionByIdAndQuestionTitleId(questionId, questionTitleId);
        Choice choice = Choice.builder()
                .question(question)
                .correct(request.correct())
                .choiceText(request.choiceText())
                .build();

        choiceRepository.save(choice);
    }

    @Override
    public List<ChoiceResponse> getAllChoice(String questionTitleId, Long questionId, UserObject userObject) {
        return choiceRepository.findAllChoiceByQuestionTitleIdAndQuestionId(questionTitleId, questionId);
    }

    @Override
    public void updateChoice(String questionTitleId, Long questionId, Long choiceId, ChoiceRequest request, UserObject userObject) {
        validationService.validate(request);
        Choice choice = getChoiceByQuestionTitleIdAndQuestionIdAndChoiceId(questionTitleId, questionId, choiceId);
        choice.setChoiceText(request.choiceText());
        choice.setCorrect(request.correct());
        choiceRepository.save(choice);
    }

    @Override
    public void deleteChoice(String questionTitleId, Long questionId, Long choiceId, UserObject userObject) {
        Choice choice = getChoiceByQuestionTitleIdAndQuestionIdAndChoiceId(questionTitleId, questionId, choiceId);
        choiceRepository.delete(choice);
    }

    private List<QuestionTitleResponse> setPageQuestionTitleQueryToListQuestionTitleResponse(
            Page<QuestionTitleQuery> queries
    ) {
        return queries.stream().map(query ->
                new QuestionTitleResponse(query.id(), query.title(), query.createdAt(), query.userName(), query.topic(), query.difficulty(), query.access(), query.tested())
        ).toList();
    }

    private void checkAccessUser(UserObject userObject, QuestionTitleQuery query) {
        if (!userObject.id().equals(query.userId())) {
            ResponseStatusExceptionUtil.unauthorizedVoid();
        }
    }

    private QuestionTitleQuery getQuestionTitleQueryById(String questionTitleId) {
        return questionTitleRepository.getDetailQuestionTitle(questionTitleId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private QuestionTitle setQuestionTitleQueryToQuestionTitle(QuestionTitleQuery query) {
        return QuestionTitle.builder()
                .id(query.id())
                .title(query.title())
                .token(query.token())
                .started(query.started())
                .createdAt(query.createdAt())
                .updatedAt(query.updatedAt())
                .topic(getTopicById(query.topicId()))
                .access(getAccessById(query.accessId()))
                .difficulty(getDifficultyById(query.difficultyId()))
                .user(getUserById(query.userId()))
                .build();
    }

    private QuestionTitle setQuestionTitleRequestToQuestionTitleEntity(QuestionTitleRequest request, QuestionTitle questionTitle, UserObject userObject) {
        if (questionTitle.getId() == null) {
            questionTitle = new QuestionTitle();
            questionTitle.setStarted(false);
            questionTitle.setCreatedAt(LocalDateTime.now());
            questionTitle.setUser(getUserByEmail(userObject.email()));
        }

        questionTitle.setAccess(getAccessById(request.accessId()));
        questionTitle.setTitle(request.title());
        questionTitle.setUpdatedAt(LocalDateTime.now());
        questionTitle.setTopic(getTopicById(request.topicId()));
        questionTitle.setDifficulty(getDifficultyById(request.difficultyId()));

        return questionTitle;
    }

    private User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private User getUserById(String userId) {
        return userRepository.getUserById(userId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private Topic getTopicById(Long topicId) {
        return topicRepository.findById(topicId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private Access getAccessById(Long accessId) {
        return accessRepository.findById(accessId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private Difficulty getDifficultyById(Long difficultyId) {
        return difficultyRepository.findById(difficultyId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private QuestionTitle getQuestionTitleById(String questionTitleId) {
        return questionTitleRepository.findById(questionTitleId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private Question getQuestionByIdAndQuestionTitleId(Long questionId, String questionTitleId) {
        return questionRepository.findByQuestionTitleIdAndQuestionId(questionTitleId, questionId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private Choice getChoiceByQuestionTitleIdAndQuestionIdAndChoiceId(
            String questionTitleId,
            Long questionId,
            Long choiceId
    ) {
        return choiceRepository.findChoiceByQuestionTitleIdAndQuestionIdAndChoiceId(questionTitleId, questionId, choiceId)
                .orElseThrow(ResponseStatusExceptionUtil::notFound);
    }
}
