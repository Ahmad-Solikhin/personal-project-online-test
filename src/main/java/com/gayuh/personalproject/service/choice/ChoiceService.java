package com.gayuh.personalproject.service.choice;

import com.gayuh.personalproject.dto.ChoiceRequest;
import com.gayuh.personalproject.dto.ChoiceResponse;
import com.gayuh.personalproject.dto.UserObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChoiceService {
    @Transactional
    void addChoice(String questionTitleId, Long questionId, ChoiceRequest request, UserObject userObject);

    List<ChoiceResponse> getAllChoice(String questionTitleId, Long questionId, UserObject userObject);

    @Transactional
    void updateChoice(String questionTitleId, Long questionId, Long choiceId, ChoiceRequest request, UserObject userObject);

    @Transactional
    void deleteChoice(String questionTitleId, Long questionId, Long choiceId, UserObject userObject);
}
