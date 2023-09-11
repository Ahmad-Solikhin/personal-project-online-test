package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.MasterRequest;
import com.gayuh.personalproject.dto.MasterResponse;
import com.gayuh.personalproject.dto.UserObject;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.topic.TopicService;
import com.gayuh.personalproject.util.CustomResponse;
import com.gayuh.personalproject.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/topics")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<Object> getAllTopic() {
        var response = topicService.getAll();

        return CustomResponse.generateResponse(
                ResponseMessage.GET_ALL_DATA.value() + response.size(),
                HttpStatus.OK,
                response
        );
    }

    @GetMapping(value = "/{topicId}")
    public ResponseEntity<Object> getTopicByid(
            @PathVariable(name = "topicId") Long topicId
    ) {
        MasterResponse response = topicService.getById(topicId);

        return CustomResponse.generateResponse(
                ResponseMessage.GET_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping
    public ResponseEntity<Object> createTopic(
            @RequestBody MasterRequest request,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        MasterResponse response = topicService.create(request);

        return CustomResponse.generateResponse(
                ResponseMessage.CREATE_DATA.value(),
                HttpStatus.CREATED,
                response
        );
    }

    @PutMapping(value = "/{topicId}")
    public ResponseEntity<Object> updateTopic(
            @RequestBody MasterRequest request,
            @PathVariable(name = "topicId") Long topicId,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        MasterResponse response = topicService.update(request, topicId);

        return CustomResponse.generateResponse(
                ResponseMessage.UPDATE_DATA.value(),
                HttpStatus.OK,
                response
        );
    }

    @DeleteMapping(value = "/{topicId}")
    public ResponseEntity<Object> deleteTopic(
            @PathVariable(name = "topicId") Long topicId,
            UserObject userObject
    ) {
        FilterUtil.filterAdmin(userObject);

        topicService.deleteById(topicId);

        return CustomResponse.generateResponse(
                ResponseMessage.DELETE_DATA.value(),
                HttpStatus.OK
        );
    }
}
