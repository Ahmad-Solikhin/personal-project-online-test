package com.gayuh.personalproject.service.storage;

import com.gayuh.personalproject.entity.Question;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String saveImage(MultipartFile multipartFile, Question question);
    void deleteImage(String mediaId);
}
