package com.gayuh.personalproject.service.question;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.query.QuestionTitleQuery;
import com.gayuh.personalproject.repository.*;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public QuestionTitleDetailResponse getQuestionDetail(String questionTitleId) {
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

            //Todo : media service to delete all media by idQuestions and unlink all document
        }

        testHistoryRepository.deleteAllTestHistoryByQuestionTitleId(questionTitleId);

        questionRepository.deleteAllQuestionByQuestionTitleId(questionTitleId);

        questionTitleRepository.deleteQuestionTitleById(questionTitleId);
    }

    @Override
    public List<QuestionResponse> getAllQuestionByQuestionTitleId(String questionTitleId) {

        return questionRepository.getAllQuestionByQuestionTitleId(questionTitleId)
                .stream().map(questionQuery -> new QuestionResponse(
                        questionQuery.id(),
                        questionQuery.time(),
                        questionQuery.score(),
                        questionQuery.createdAt(),
                        questionQuery.updatedAt(),
                        questionQuery.mediaId()
                )).toList();
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.UNAUTHORIZED.value());
        }
    }

    private QuestionTitleQuery getQuestionTitleQueryById(String questionTitleId) {
        return questionTitleRepository.getDetailQuestionTitle(questionTitleId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
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

        questionTitle.setTitle(request.title());
        questionTitle.setUpdatedAt(LocalDateTime.now());
        questionTitle.setTopic(getTopicById(request.topicId()));
        questionTitle.setAccess(getAccessById(request.accessId()));
        questionTitle.setDifficulty(getDifficultyById(request.difficultyId()));

        return questionTitle;
    }

    private User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }

    private User getUserById(String userId) {
        return userRepository.getUserById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }

    private Topic getTopicById(Long topicId) {
        return topicRepository.findById(topicId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }

    private Access getAccessById(Long accessId) {
        return accessRepository.findById(accessId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }

    private Difficulty getDifficultyById(Long difficultyId) {
        return difficultyRepository.findById(difficultyId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );
    }
}
