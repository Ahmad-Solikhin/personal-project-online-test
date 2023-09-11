package com.gayuh.personalproject.service.media;

import com.gayuh.personalproject.dto.MediaResponse;
import com.gayuh.personalproject.entity.Question;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String saveImageQuestion(MultipartFile file, Question question);
    void updateImageQuestion(MultipartFile file, String mediaId);
    void deleteImageQuestion(String mediaId);
    MediaResponse getTheMediaById(String mediaId);
    void deleteAllImageByQuestionTitleId(String questionTitleId);
}
