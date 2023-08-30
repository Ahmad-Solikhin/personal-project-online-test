package com.gayuh.personalproject.service.storage;

import com.gayuh.personalproject.dto.MediaResponse;
import com.gayuh.personalproject.entity.Media;
import com.gayuh.personalproject.entity.Question;
import com.gayuh.personalproject.repository.MediaRepository;
import com.gayuh.personalproject.util.ResponseStatusExceptionUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final MediaRepository mediaRepository;
    private final Path folderImagePath = Paths.get("src/main/resources/static/img");

    @Override
    public String saveImageQuestion(MultipartFile file, Question question) {
        checkContentType("image", file);

        Media media = mediaRepository.save(Media.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size((int) file.getSize())
                .question(question)
                .build());

        String fileName = media.getId() + "-" + file.getOriginalFilename();

        saveImage(file, fileName);

        return media.getId();
    }

    @Override
    public void updateImageQuestion(MultipartFile file, String mediaId) {
        checkContentType("image", file);
        Media media = getMediaById(mediaId);
        String fileName = mediaId + "-" + media.getName();

        try {
            if (Boolean.FALSE.equals(compareTwoImage(file, fileName))) {
                deleteFile(media);

                media.setName(file.getOriginalFilename());
                media.setSize((int) file.getSize());
                mediaRepository.save(media);

                fileName = mediaId + "-" + file.getOriginalFilename();
                saveImage(file, fileName);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error update file " + file.getOriginalFilename());
        }
    }

    @Override
    public void deleteImageQuestion(String mediaId) {
        Media media = getMediaById(mediaId);

        deleteFile(media);

        mediaRepository.delete(media);
    }

    @Override
    public MediaResponse getTheMediaById(String mediaId) {
        Media media = getMediaById(mediaId);
        String fileName = mediaId + "-" + media.getName();
        Path filepath = folderImagePath.resolve(fileName);

        try {
            return new MediaResponse(media.getType(), media.getName(), Files.readAllBytes(filepath));
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting file " + media.getName());
        }
    }

    @Override
    public void deleteAllImageByQuestionTitleId(String questionTitleId) {
        List<Media> medias = mediaRepository.findAllMediaByQuestionTitleId(questionTitleId);

        if (!medias.isEmpty()) {
            medias.forEach(this::deleteFile);
            mediaRepository.deleteAllMediaByQuestionTitleId(questionTitleId);
        }
    }

    public void saveImage(MultipartFile file, String fileName) {
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = folderImagePath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error save file " + file.getOriginalFilename());
        }
    }

    public void deleteFile(Media media) {
        try {
            Path filePath = folderImagePath.resolve(media.getId() + "-" + media.getName());
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error delete file " + media.getName());
        }
    }

    /**
     * For compare the image input
     * Arrays.equals(multipartFile.getBytes(), Files.readAllBytes(filePath));
     */

    private Boolean compareTwoImage(MultipartFile file, String fileName) throws IOException {
        Path filePath = folderImagePath.resolve(fileName);
        return Arrays.equals(file.getBytes(), Files.readAllBytes(filePath));
    }

    private void checkContentType(String content, MultipartFile file) {
        String type = Objects.requireNonNull(file.getContentType()).split("/")[0];
        if (!type.equalsIgnoreCase(content)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only accept " + content);
        }
    }

    private Media getMediaById(String mediaId) {
        return mediaRepository.findById(mediaId).orElseThrow(
                ResponseStatusExceptionUtil::notFound
        );
    }

}
