package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.MediaResponse;
import com.gayuh.personalproject.service.media.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/v1/medias")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping(value = "{mediaId}")
    public ResponseEntity<byte[]> getMediaById(
            @PathVariable(name = "mediaId") String mediaId
    ) {
        MediaResponse response = mediaService.getTheMediaById(mediaId);

        return ResponseEntity.ok().contentType(
                MediaType.parseMediaType(response.contentType())
        ).body(response.media());
    }

    @GetMapping(value = "{mediaId}/download")
    public ResponseEntity<byte[]> downloadMediaById(
            @PathVariable(name = "mediaId") String mediaId
    ) {
        MediaResponse response = mediaService.getTheMediaById(mediaId);

        String headerValue = "attachment; filename=\"" + response.fileName() + "\"";

        return ResponseEntity.ok().contentType(
                MediaType.parseMediaType(response.contentType())
        ).header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(response.media());
    }
}
