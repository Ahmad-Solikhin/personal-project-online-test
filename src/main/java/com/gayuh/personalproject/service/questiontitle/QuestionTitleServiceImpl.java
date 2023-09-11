package com.gayuh.personalproject.service.questiontitle;

import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.entity.*;
import com.gayuh.personalproject.query.QuestionTitleQuery;
import com.gayuh.personalproject.repository.*;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.service.media.MediaService;
import com.gayuh.personalproject.util.PaginationUtil;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionTitleServiceImpl extends ParentService implements QuestionTitleService {

    private final QuestionTitleRepository questionTitleRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final DifficultyRepository difficultyRepository;
    private final AccessRepository accessRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final TestHistoryRepository testHistoryRepository;
    private final ChoiceRepository choiceRepository;
    private final MediaService mediaService;

    @Override
    public PaginationResponse<QuestionTitleResponse> getAllPublicQuestionTitle(PaginationRequest pagination) {
        Sort sort1 = PaginationUtil.getSort(pagination.getSort(), pagination.getSortBy());

        String search = "%" + pagination.getSearch() + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(pagination.getPage(), pagination.getRow(), sort1);

        Page<QuestionTitleQuery> queries = questionTitleRepository.findAllPublicQuestionTitleWithPageResult(
                search,
                pagination.getTopicId(),
                pagination.getDifficultyId(),
                pageRequest
        );


        List<QuestionTitleResponse> questionTitleResponses = setPageQuestionTitleQueryToListQuestionTitleResponse(queries);

        return PaginationUtil.createPageResponse(
                questionTitleResponses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                pagination.getPage()
        );
    }

    @Override
    public PaginationResponse<QuestionTitleResponse> getAllQuestionTitleCreatedByUser(
            UserObject userObject,
            PaginationRequest pagination
    ) {

        Sort sort1 = PaginationUtil.getSort(pagination.getSort(), pagination.getSortBy());

        String search = "%" + pagination.getSearch() + "%";

        PageRequest pageRequest = PaginationUtil.getPageRequest(pagination.getPage(), pagination.getRow(), sort1);

        Page<QuestionTitleQuery> queries = questionTitleRepository.findAllQuestionTitleCreatedByUserWithPageResult(
                userObject.id(),
                search,
                pagination.getTopicId(),
                pagination.getDifficultyId(),
                pagination.getAccessId(),
                pageRequest
        );

        List<QuestionTitleResponse> questionTitleResponses = setPageQuestionTitleQueryToListQuestionTitleResponse(queries);

        return PaginationUtil.createPageResponse(
                questionTitleResponses,
                (int) queries.getTotalElements(),
                queries.getTotalPages(),
                pagination.getPage()
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
    public QuestionTitleDetailResponse getQuestionTitleDetailByToken(String token) {
        QuestionTitleQuery query = getQuestionTitleQueryByToken(token);

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

            mediaService.deleteAllImageByQuestionTitleId(questionTitleId);
        }

        testHistoryRepository.deleteAllTestHistoryByQuestionTitleId(questionTitleId);

        questionRepository.deleteAllQuestionByQuestionTitleId(questionTitleId);

        questionTitleRepository.deleteQuestionTitleById(questionTitleId);
    }

    @Override
    public String sharePrivateQuestionTitle(String questionTitleId, String generate, UserObject userObject) {
        QuestionTitleQuery query = getQuestionTitleQueryById(questionTitleId);

        checkAccessUser(userObject, query);

        if (query.token() != null && generate.equalsIgnoreCase("false")) return query.token();

        QuestionTitle questionTitle = setQuestionTitleQueryToQuestionTitle(query);
        questionTitle.setToken(UUID.randomUUID().toString());
        questionTitleRepository.save(questionTitle);

        return questionTitle.getToken();
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
        return questionTitleRepository.findQuestionTitleQuery(questionTitleId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

    private QuestionTitleQuery getQuestionTitleQueryByToken(String token) {
        return questionTitleRepository.findQuestionTitleQueryByToken(token).orElseThrow(
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
        LocalDateTime currentTime = LocalDateTime.now();

        if (questionTitle.getId() == null) {
            questionTitle = new QuestionTitle();
            questionTitle.setStarted(false);
            questionTitle.setCreatedAt(currentTime);
            questionTitle.setUser(getUserByEmail(userObject.email()));
        }

        questionTitle.setAccess(getAccessById(request.accessId()));
        questionTitle.setTitle(request.title());
        questionTitle.setUpdatedAt(currentTime);
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
}
