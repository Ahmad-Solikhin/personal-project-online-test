package com.gayuh.personalproject.service.storage;

import com.gayuh.personalproject.dto.MediaResponse;
import com.gayuh.personalproject.entity.Question;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveImageQuestion(MultipartFile file, Question question);
    void updateImageQuestion(MultipartFile file, String mediaId);
    void deleteImageQuestion(String mediaId);
    MediaResponse getTheMediaById(String mediaId);
    void deleteAllImageByQuestionTitleId(String questionTitleId);
}
