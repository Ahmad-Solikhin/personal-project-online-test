package com.gayuh.personalproject.service.choice;

import com.gayuh.personalproject.dto.ChoiceRequest;
import com.gayuh.personalproject.dto.ChoiceResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.entity.Choice;
import com.gayuh.personalproject.entity.Question;
import com.gayuh.personalproject.repository.ChoiceRepository;
import com.gayuh.personalproject.repository.QuestionRepository;
import com.gayuh.personalproject.service.ParentService;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceServiceImpl extends ParentService implements ChoiceService {
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;

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
