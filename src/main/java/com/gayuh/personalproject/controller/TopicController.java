package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.TopicRequest;
import com.gayuh.personalproject.dto.TopicResponse;
import com.gayuh.personalproject.service.topic.TopicService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping(value = "v1/topics")
    public ResponseEntity<Object> getAllTopic() {
        var response = topicService.getAllTopic();

        return CustomResponse.generateResponse(
                "Data found with size " + response.size(),
                HttpStatus.OK,
                response
        );
    }

    @GetMapping(value = "v1/topics/{topicId}")
    public ResponseEntity<Object> getTopicByid(
            @PathVariable(name = "topicId") Long topicId
    ) {
        TopicResponse response = topicService.findById(topicId);

        return CustomResponse.generateResponse(
                "Data found",
                HttpStatus.OK,
                response
        );
    }

    @PostMapping(value = "v1/topics")
    public ResponseEntity<Object> createTopic(
            @RequestBody TopicRequest request
    ) {
        TopicResponse response = topicService.create(request);

        return CustomResponse.generateResponse(
                "Success created new topic",
                HttpStatus.CREATED,
                response
        );
    }

    @PutMapping(value = "v1/topics/{topicId}")
    public ResponseEntity<Object> updateTopic(
            @RequestBody TopicRequest request,
            @PathVariable(name = "topicId") Long topicId
    ) {
        TopicResponse response = topicService.update(request, topicId);

        return CustomResponse.generateResponse(
                "Success update topic",
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping(value = "v1/topics/{topicId}")
    public ResponseEntity<Object> deleteTopic(
            @PathVariable(name = "topicId") Long topicId
    ) {
        topicService.deleteById(topicId);

        return CustomResponse.generateResponse(
                "Success delete topic",
                HttpStatus.NO_CONTENT
        );
    }
}
