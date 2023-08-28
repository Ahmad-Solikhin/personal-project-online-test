package com.gayuh.personalproject.service.storage;

import com.gayuh.personalproject.entity.Media;
import com.gayuh.personalproject.entity.Question;
import com.gayuh.personalproject.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final MediaRepository mediaRepository;
    private final Path folderImagePath = Paths.get("src/main/resources/static/img");

    @Override
    public String saveImage(MultipartFile multipartFile, Question question) {
        checkContentType("image", multipartFile);

        Media media = mediaRepository.save(Media.builder()
                .name(multipartFile.getOriginalFilename())
                .type(multipartFile.getContentType())
                .size((int) multipartFile.getSize())
                .question(question)
                .build());

        String fileName = media.getId() + multipartFile.getOriginalFilename();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = folderImagePath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error saving file " + multipartFile.getOriginalFilename());
        }

        return media.getId();
    }

    @Override
    public void deleteImage(String mediaId) {
        //Todo : search media using mediaquery and send name to resolve attributr
        Path filePath = folderImagePath.resolve("TODO");

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error delete file " + "TODO : FILE_NAME");
        }
    }

    private void checkContentType(String content, MultipartFile file) {
        String type = Objects.requireNonNull(file.getContentType()).split("/")[0];
        if (!type.equalsIgnoreCase(content)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only accept " + content);
        }
    }
}
